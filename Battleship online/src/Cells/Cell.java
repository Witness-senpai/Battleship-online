package Cells;

public abstract class Cell {
    public enum Type {
        EMPTY, //Пустая
        SHIP,  //С короблём
        FIRED, //С подбитым кораблём
        MISS,  //Промах в море
        AIM    //Прицел по вражеской клетке
    }

    private Type type;
    private int x;
    private int y;

    public Type getType() {
        return type;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
