package multiplayer;

import Cells.Cell;
import logic.Action;
import logic.Info;

import java.io.*;
import java.net.*;

import static logic.Action.Event.ANSWER;
import static visualContent.MainMenuApp.enemy;
import static visualContent.MainMenuApp.host;
import static visualContent.MainMenuApp.player;
import static logic.Player.X0_FOR_ENEMY_FIELD;

public class Server implements Runnable{
    private ServerSocket serverSocket;
    private Socket socket;
    private int port;
    public boolean isConnected = false;
    private Info info;

    //Потоки ввода и вывода по сети
    private ObjectOutputStream serializer;
    private ObjectInputStream deserializer;

    public Server(Info info) {
        this.serverSocket = null;
        this.socket = null;
        this.port = 1234;
        this.serializer = null;
        this.deserializer = null;
        this.info = info;
    }

    //Создание сервера на определённом порту
    public void create(int port){
        try {
            this.serverSocket = new ServerSocket(port);
            this.port = port;
            System.out.println("====SERVER====");
            System.out.println("Сервер создан на " + host + ":" + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try{
            this.socket = serverSocket.accept(); //ждём, пока подключится клиент

            this.isConnected = true;
            System.out.println("Соединение устанвлено!");

            this.serializer = new ObjectOutputStream(socket.getOutputStream());
            this.deserializer = new ObjectInputStream(socket.getInputStream());

            while(true){
                Action action = readData();
                System.out.println("Пришло: " + action.getPosX() + " " + action.getPosY() + " " + action.getEvent());

                switch (action.getEvent()) {
                    case TURN:
                        //Если x == -1, то это спец. оповещение о готовности клиента
                        if (action.getPosX() == -1)
                            enemy.shipsIsReady = true;
                        else//В противном случае, это просто названчение хода в начале игры
                            player.setTurn(action.getTurn());
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
                            info.showLabel(true, "ВАШ ХОД");
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
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
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

    @Override
    public void finalize() throws Throwable {
        socket.close();
        serverSocket.close();
        super.finalize();
    }
}
