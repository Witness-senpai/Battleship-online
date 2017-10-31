package visualContent;

import Cells.Cell;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import logic.*;
import multiplayer.Client;
import multiplayer.Server;

import java.util.Random;

import static Cells.Cell.Type.*;
import static logic.Action.Event.CHECK;
import static logic.Action.Event.TURN;
import static logic.Field.cellSize;
import static logic.Player.X0_FOR_ENEMY_FIELD;

class Game extends MainMenuApp {
    public Thread clientThread = null;
    public Thread serverThread = null;

    public Info info;

    boolean playerIsServer = false; //Запущено ли приложение в роли сервера. Меняется на true только при нажатии соответствующей кнопки

    private boolean btnNewGameWasPressed = false;

    public Game() throws Exception {
        Canvas canvas = new Canvas(cellSize * 31, cellSize * 12);
        GraphicsContext context = canvas.getGraphicsContext2D();
        context.drawImage(new Image("img/bg.png"),0,0);

        player = new Player("Вы", new Field(context,0,0));
        enemy = new Player("Враг", new Field(context, X0_FOR_ENEMY_FIELD,0));
        player.field.draw(0,0);
        enemy.field.draw(X0_FOR_ENEMY_FIELD,0);

        Stage stage = new Stage();
        stage.setTitle("Battleship Online");

        Button btnNewGame    = new Button();
        Pane playerPane      = new Pane();
        Pane enemyPane       = new Pane();
        Button btnClearShips = new Button();
        Label labelTurn      = new Label();
        TextArea textLog     = new TextArea();

        info = new Info(stage, labelTurn, textLog);

        if (btnServerWasPressed) {
            //Если нажата кнопка поиска сервера, то:
            //Игрок становится сервером, а враг - клиентом
            server = new Server(info);
            client = new Client(info);

            server.create(port);
            serverThread = new Thread(server,"ServerThread");
            serverThread.start();
            playerIsServer = true;
        } else {
            //Если нажата кнопка поиска сервера, то:
            //Игрок становится клиентом, а враг - сервером
            client = new Client(info);
            server = new Server(info);

            if (client.findServer(host,port)) {
                clientThread = new Thread(client, "ClientThread");
                clientThread.start();
            }
        }

        //Панель для player
        playerPane.setLayoutX(0);
        playerPane.setLayoutY(0);
        playerPane.setPrefHeight(11 * cellSize);
        playerPane.setPrefWidth (11 * cellSize);
        playerPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (!btnNewGameWasPressed) {
                    int x = (int) event.getX() / (int) cellSize - 1;
                    int y = (int) event.getY() / (int) cellSize - 1;
                    if (player.field.getTypeCell(x, y) == EMPTY)
                        player.field.setCell(x, y, Cell.Type.SHIP);
                    else
                        player.field.setCell(x, y, Cell.Type.EMPTY);
                    player.field.draw(0, 0);
                }
            }
        });

        //Панель для enemy
        enemyPane.setLayoutX(X0_FOR_ENEMY_FIELD * cellSize);
        enemyPane.setLayoutY(0);
        enemyPane.setPrefHeight(11 * cellSize);
        enemyPane.setPrefWidth (11 * cellSize);
        enemyPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (player.isTurn()){
                    int x = (int) event.getX() / (int)cellSize - 1;
                    int y = (int) event.getY() / (int)cellSize - 1;
                    //Если ячейка врага для игрока пустая или на ней прицел(тоже пустая)
                    if (enemy.field.getTypeCell(x,y) == EMPTY || enemy.field.getTypeCell(x,y) == AIM) {
                        if (btnServerWasPressed){ //Если игрок - это сервер
                            server.sendData(new Action(x, y, CHECK));
                        } else {
                            client.sendData(new Action(x, y, CHECK));
                        }
                    }
                    enemy.field.draw(X0_FOR_ENEMY_FIELD, 0);
                }
            }
        });
        enemyPane.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                    int x = (int)event.getX() / (int)cellSize - 1;
                    int y = (int)event.getY() / (int)cellSize - 1;
                    if (enemy.field.getTypeCell(x,y) == EMPTY){
                        enemy.field.setCell(x,y,AIM);
                    }
                    enemy.field.draw(X0_FOR_ENEMY_FIELD,0);
                    enemy.field.clearFrom(AIM);
            }
        });

        //Текстовое поле-лог событий
        textLog.setLayoutX(cellSize * 12);
        textLog.setLayoutY(cellSize * 3);
        textLog.setPrefSize(cellSize * 7, cellSize * 8);
        textLog.setVisible(false);

        //Поле, для вывода событий об игре
        labelTurn.setPrefSize(cellSize * 7, cellSize * 8);
        labelTurn.setLayoutY(cellSize * 2);
        labelTurn.setFont(new Font("Century Gothic", 24));
        labelTurn.setVisible(false);

        //Создание кнопки для очистки поля с короблями
        btnClearShips.setLayoutX(cellSize * 12);
        btnClearShips.setLayoutY(cellSize * 6);
        btnClearShips.setPrefSize(cellSize * 7, cellSize * 2);
        btnClearShips.setText("ОЧИСТИТЬ");
        btnClearShips.setFont(new Font("Century Gothic", 20));
        btnClearShips.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                player.field.clearFrom(SHIP);
                player.field.draw(0,0);
            }
        });

        //Кнопка начала игры для сервера и готовности для клиента
        btnNewGame.setLayoutX(cellSize * 12);
        btnNewGame.setLayoutY(cellSize * 3);
        btnNewGame.setPrefSize(cellSize * 7, cellSize * 2);
        btnNewGame.setFont(new Font("Century Gothic", 24));
        if (playerIsServer)
            btnNewGame.setText("НАЧАТЬ ИГРУ");
        else
            btnNewGame.setText("ГОТОВ");
        btnNewGame.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                if (player.field.shipsIsCorrect()) { //Если коробли у игрока расставлены корректно
                    if (enemy.shipsIsReady && playerIsServer) {//Если коробли врага расставлены корректно и игрок - сервер
                        btnNewGameWasPressed = true;
                        Random rnd = new Random();
                        info.appendBattleInfo("***ИГРА НАЧАЛАСЬ!***");
                        //Уловие, кто начнёт игру первым
                        if (rnd.nextInt(2) % 2 == 0) {
                            player.setTurn(true);
                            enemy.setTurn(false);
                            server.sendData(new Action(TURN, false));//Отсылка клиенту, что он ходит вторым
                            info.showLabel(true, "ВАШ ХОД");
                            info.appendBattleInfo("===ВАШ ХОД===");
                        } else {
                            player.setTurn(false);
                            enemy.setTurn(true);
                            server.sendData(new Action(TURN, true)); //Отсылка клиенту, что он ходит первым
                            info.showLabel(false, "ХОД ВРАГА");
                            info.appendBattleInfo("===ХОД ВРАГА===");
                        }
                        if (player.isTurn())
                            info.showLabel(true, "ВАШ ХОД");
                        else
                            info.showLabel(false, "ХОД ВРАГА");
                        btnClearShips.setVisible(false);
                        btnNewGame.setVisible(false);
                        textLog.setVisible(true);
                    } else if (!playerIsServer) {//Если игрок - это клиент
                        client.sendData(new Action(-1, 0, TURN));//Специальный набор данных о готовности хода в начале игры
                        btnNewGameWasPressed = true;
                        textLog.appendText("Ожидаем сервер...\n");
                        btnClearShips.setVisible(false);
                        btnNewGame.setVisible(false);
                        textLog.setVisible(true);
                    }
                }
                else {
                    info.showAlert("ships");
                }
            }
        });
        Group root = new Group();
        root.getChildren().addAll(canvas,btnClearShips,btnNewGame,enemyPane,playerPane, textLog, labelTurn);
        updateStage(stage,root);
    }
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}

