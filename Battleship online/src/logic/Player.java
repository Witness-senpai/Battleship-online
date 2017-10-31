package logic;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class Player{
    public static final int X0_FOR_ENEMY_FIELD = 19; //Всё равно зависит от cellSize

    private GraphicsContext context;
    public Field field; //Поле игрока
    private String name;
    private boolean isTurn; //Определяет его ход
    public boolean shipsIsReady; //Готовность короблей в начале
    private int aliveShips;

    public Player(String name, Field field){
        this.name = name;
        this.field = field;
        this.isTurn = false;
        this.shipsIsReady = false;
        this.aliveShips = 10;
    }

    public boolean isTurn() {
        return this.isTurn;
    }

    public void setTurn(boolean turn){
        this.isTurn = turn;
    }

    public int getAliveShips() {
        return this.aliveShips;
    }

    public void decDeadShips(){
        this.aliveShips--;
    }

    public String getName() {
        return name;
    }
}
