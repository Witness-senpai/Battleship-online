package logic;

import java.io.Serializable;

/*
Класс-событие, с помощью которого обмениваются информацией клиент и сервер
*/
public class Action implements Serializable {
    public boolean isKilling() {
        return isKilling;
    }

    public void setKilling(boolean killing) {
        isKilling = killing;
    }

    public enum Event{
        CHECK,    //Проверка координат
        ANSWER,   //Ответ по проверке координат
        TURN     //Манипуляция с ходом, его передаача или назначение
    }

    private  int posX;
    private  int posY;
    private  boolean turn;
    private  boolean answer;
    private  boolean isKilling;
    private Event event;

    public Action(int x, int y, Event event){
        this.posX = x;
        this.posY = y;
        this.event = event;
        this.answer = false;
        this.turn = false;
        this.isKilling = false;

    }

    public Action(Event event, boolean turn){
        this.posX = 0;
        this.posY = 0;
        this.event = event;
        this.answer = false;
        this.turn = turn;
    }

    //Для спец случаев, не трогать
    public Action(int x, Event event, boolean turn){
        this.posX = x;
        this.posY = 0;
        this.event = event;
        this.turn = turn;
    }

    public boolean getTurn() {
        return turn;
    }

    public void setTurn(boolean turn) {
        this.turn = turn;
    }

    public boolean getAnswer() {
        return answer;
    }

    public void setAnswer(boolean answer) {
        this.answer = answer;
    }

    public  int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public  void setPosY(int posY_) {
        this.posY = posY;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}

