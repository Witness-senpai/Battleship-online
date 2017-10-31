package visualContent;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import logic.Player;
import multiplayer.Client;
import multiplayer.Server;
import visualContent.Game;

import static logic.Field.cellSize;
import static logic.Info.showAlert;

public class MainMenuApp extends Application{

    public static boolean btnServerWasPressed = false; //Была ли нажата кнопка для сервера. Нужно для обработчика событий клика и его работы

    public Group root;

    public static Player player;
    public static Player enemy;

    public static Server server;
    public static Client client;

    public static int port = 6666;
    public static String host = "localhost";

    @Override
    public void start(Stage primaryStage) throws Exception{

        //Текстовое поле для изменения ip подключения
        TextField hostName = new TextField();
        hostName.setLayoutX(cellSize * 3);
        hostName.setLayoutY(cellSize * 11);
        hostName.setPrefSize(cellSize * 6,cellSize * 1);
        hostName.setFont(new Font("Century Gothic", 18));
        hostName.setText("localhost");

        //Текстовое поля для изменения порта
        TextField portName = new TextField();
        portName.setLayoutX(cellSize * 10);
        portName.setLayoutY(cellSize * 11);
        portName.setPrefSize(cellSize * 3,cellSize * 1);
        portName.setFont(new Font("Century Gothic", 18));
        portName.setText("6666");

        //Кнопка создания сервера
        Button btnStartServer = new Button();
        btnStartServer.setLayoutX(cellSize * 3);
        btnStartServer.setLayoutY(cellSize * 5);
        btnStartServer.setPrefSize(cellSize * 10,cellSize * 2);
        btnStartServer.setFont(new Font("Century Gothic", 22));
        btnStartServer.setText("Cоздать сервер");
        btnStartServer.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                btnServerWasPressed = true;
                //Запуск окна игры
                try {
                    host = hostName.getText();
                    port = Integer.parseInt(portName.getText());
                    new Game();
                    primaryStage.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //Кнопка поиска сервера
        Button btnStartClient = new Button();
        btnStartClient.setLayoutX(cellSize * 3);
        btnStartClient.setLayoutY(cellSize * 8);
        btnStartClient.setPrefSize(cellSize * 10,cellSize * 2);
        btnStartClient.setFont(new Font("Century Gothic", 18));
        btnStartClient.setText("ПОДКЛЮЧИТЬСЯ К СЕРВЕРУ");
        btnStartClient.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                try {
                    host = hostName.getText();
                    port = Integer.parseInt(portName.getText());
                    new Game();
                    primaryStage.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert("connection");
                }
            }
        });

        //Кнопка выхода их меню/игры
        Button btnExit = new Button();
        btnExit.setLayoutX(cellSize * 3);
        btnExit.setLayoutY(cellSize * 13);
        btnExit.setPrefSize(cellSize * 10,cellSize * 2);
        btnExit.setFont(new Font("Century Gothic", 24));
        btnExit.setText("ВЫХОД");
        btnExit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                primaryStage.close();
            }
        });

        primaryStage.setTitle("Battleship online : main menu");

        Group buttons = new Group();
        buttons.getChildren().addAll(btnExit,btnStartServer,btnStartClient);

        Canvas menuCanvas = new Canvas(16 * cellSize, cellSize * 16);
        GraphicsContext menuContext = menuCanvas.getGraphicsContext2D();
        menuContext.drawImage(new Image("img/menuBG.png"),-200,-20);

        root = new Group();
        root.getChildren().addAll(menuCanvas,buttons, hostName, portName);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void updateStage(Stage primaryStage, Group root) {
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
