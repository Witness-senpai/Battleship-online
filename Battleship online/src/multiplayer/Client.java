package multiplayer;

import Cells.Cell;
import logic.Action;
import logic.Info;
import visualContent.MainMenuApp;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

import static logic.Action.Event.ANSWER;
import static logic.Player.X0_FOR_ENEMY_FIELD;

public class Client extends MainMenuApp implements Runnable{
    private InetAddress host;
    private int port;
    private Socket socket;
    private Info info;

    //Потоки ввода и вывода класса с данными по сети
    private ObjectOutputStream serializer;
    private ObjectInputStream deserializer;

    public Client(Info info){
        this.host = null;
        this.port = 1234;
        this.socket = null;
        this.serializer = null;
        this.deserializer = null;
        this.info = info;
    }

    public boolean findServer(String host, int port) throws IOException{
        this.host = InetAddress.getByName(host);
        this.port = port;
        this.socket = new Socket(host, port);
        if (this.socket.isConnected()) {
            System.out.println("====CLIENT====");
            System.out.println("Клиент законнектился с сервером -> " + host + ":" + port);
            this.serializer = new ObjectOutputStream(socket.getOutputStream());
            this.deserializer = new ObjectInputStream(socket.getInputStream());
            return true;
        }
        return false;
    }

    public void sendData(Action action){
        try {
            serializer.writeObject(action);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Action readData() {
        Action action = new Action(0,0,null);
        try {
            action = (Action)deserializer.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return action;
    }

    public void run() {
        try {
            while(true) {
                Action action = readData();
                System.out.println("Пришло: " + action.getPosX() + " " + action.getPosY() + " " + action.getEvent());
                switch (action.getEvent()) {
                    case TURN:
                            player.setTurn(action.getTurn());
                            info.appendBattleInfo("clear");
                            info.appendBattleInfo("***ИГРА НАЧАЛАСЬ!***");
                            if (action.getTurn())
                                info.appendBattleInfo("===ВАШ ХОД===");
                            else
                                info.appendBattleInfo("===ХОД ВРАГА===");
                        break;
                    case CHECK: //Проверка от врага, что у нас в конкретной ячейке
                        if (player.field.getTypeCell(action.getPosX(), action.getPosY()) == Cell.Type.EMPTY) {
                            player.field.setCell(action.getPosX(), action.getPosY(), Cell.Type.MISS);
                            action.setAnswer(false); //Промах - ответ отрицательный
                            player.setTurn(true);
                            enemy.setTurn(false);
                            info.showLabel(true, "ВАШ ХОД");
                            info.appendBattleInfo(action.getPosX(), action.getPosY(), enemy.getName(), "промах!");
                            info.appendBattleInfo("===ВАШ ХОД===");
                            action.setKilling(false);
                        }
                        else
                        if (player.field.getTypeCell(action.getPosX(), action.getPosY()) == Cell.Type.SHIP) {
                            player.field.setCell(action.getPosX(), action.getPosY(), Cell.Type.FIRED);
                            action.setAnswer(true);
                            if (player.field.checkDeadShips(action.getPosX(), action.getPosY())) { //Если попали и подбили целый корабль
                                info.appendBattleInfo(action.getPosX(), action.getPosY(), enemy.getName(), "убит...=(");
                                action.setKilling(true);
                                player.decDeadShips();
                                if (player.getAliveShips() == 0)
                                    info.appendBattleInfo("==============\n==============\nВы проиграли битву...\nНо не войну!\n==============\n");
                            }
                            else {//Если просто попали, но не убили
                                info.appendBattleInfo(action.getPosX(), action.getPosY(), enemy.getName(), "попал...");
                                action.setKilling(false);
                            }
                            info.showLabel(false, "ХОД ВРАГА");
                        }
                        action.setEvent(ANSWER);
                        this.sendData(action);
                        player.field.draw(0, 0);
                        break;
                    case ANSWER://Ответ от врага, попали ли мы
                        if (!action.getAnswer() && !action.isKilling()) {
                            //Если был промах, то игрок не ходит
                            enemy.field.setCell(action.getPosX(), action.getPosY(), Cell.Type.MISS);
                            player.setTurn(false);
                            enemy.setTurn(true);
                            info.showLabel(false, "ХОД ВРАГА");
                            info.appendBattleInfo(action.getPosX(), action.getPosY(), player.getName(), "промах...");
                            info.appendBattleInfo("===ХОД ВРАГА===");
                        }
                        else if (action.getAnswer()){
                            enemy.field.setCell(action.getPosX(), action.getPosY(), Cell.Type.FIRED);
                            info.showLabel(true,"ВАШ ХОД");
                            if (action.isKilling()) {
                                info.appendBattleInfo(action.getPosX(), action.getPosY(), player.getName(), "убил!");
                                enemy.field.checkDeadShips(action.getPosX(), action.getPosY());
                                enemy.decDeadShips();
                                if(enemy.getAliveShips() == 0)
                                    info.appendBattleInfo("==============\n==============\nПобеда, победа,\n вместо обеда!\n==============\n");
                            }
                            else
                                info.appendBattleInfo(action.getPosX(), action.getPosY(), player.getName(), "попал!");
                        }
                        enemy.field.draw(X0_FOR_ENEMY_FIELD, 0);
                        break;
                }
                Thread.sleep(50);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        socket.close();
        super.finalize();
    }
}