package logic;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import static logic.Field.cellSize;
import static visualContent.MainMenuApp.host;
import static visualContent.MainMenuApp.port;

/*
* Класс отвечает за визуализации информации на форму
* Выводит надписи о том, кто ходит;
* Выводит события выстрелов в лог;
* Выводит надписи для победителя и проигравшего
* Выводит Alert-окна
*/
public class Info{
    private Stage stage;
    private Label labelTurn;
    private TextArea textLog;

    public Info(Stage stage, Label labelTurn, TextArea textLog){
        this.stage = stage;
        this.labelTurn = labelTurn;
        this.textLog = textLog;
        this.textLog.setOpacity(0.8);
        this.textLog.setFont(new Font("Century Gothic", 16));
    }

    //Отобразить надпись, кто ходит
    public void showLabel(boolean playerIsTurn, String str) {
        if (playerIsTurn){
            labelTurn.setPrefSize(cellSize * 7, cellSize * 8);
            labelTurn.setFont(new Font("Century Gothic", 24));
            labelTurn.setLayoutY(-cellSize * 3);
            labelTurn.setVisible(true);
            labelTurn.setLayoutX(cellSize * 12);
//            labelTurn.setText(str);
            labelTurn.setTextFill(Color.rgb(43, 171, 48));
        } else {
            labelTurn.setPrefSize(cellSize * 7, cellSize * 8);
            labelTurn.setFont(new Font("Century Gothic", 24));
            labelTurn.setLayoutY(-cellSize * 3);
            labelTurn.setVisible(true);
            labelTurn.setLayoutX(cellSize * 12.7);
//            labelTurn.setText(str);
            labelTurn.setTextFill(Color.rgb(171, 15, 24));
        }
    }

    public void appendBattleInfo(int x, int y, String name, String action){
        textLog.appendText(name + ": " + toGameCoords(x,y) + " " + action + "\n");
    }

    public void appendBattleInfo(String str){
        if (str.equals("clear"))
            textLog.clear();
        else
            textLog.appendText(str + "\n");
    }

    private String toGameCoords(int x, int y){
        String str = null;
        str = "[" + String.valueOf(intToASCII(x)) + String.valueOf(y + 1) + "]";
        return str;
    }

    private char intToASCII(int a) {
        char c = ' ';
        switch (a) {
            case 0:
                c = 'A';
                break;
            case 1:
                c = 'B';
                break;
            case 2:
                c = 'C';
                break;
            case 3:
                c = 'D';
                break;
            case 4:
                c = 'E';
                break;
            case 5:
                c = 'F';
                break;
            case 6:
                c = 'G';
                break;
            case 7:
                c = 'H';
                break;
            case 8:
                c = 'I';
                break;
            case 9:
                c = 'J';
                break;
        }
        return c;
    }

    public static void showAlert(String type){
        if (type.equals("connection")){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Предупреждение сети");
            alert.setHeaderText("Сервер " + host + ":" + port + " не найден");
            alert.setContentText("Проверьте введённые данные для корректного подключения");
            alert.showAndWait();
        } else
        if (type.equals("ships")){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Ошибка начала игры");
            alert.setHeaderText("Обнаружена неверная расстановка короблей");
            alert.setContentText("Пожалуйста, расставьте корабли на своём поле корректно и потом начните игру");
            alert.showAndWait();
        }
    }
}
