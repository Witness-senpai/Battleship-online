package logic;
import Cells.Cell;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import static Cells.Cell.Type.*;

public class Field {
    private GraphicsContext graphics;
    protected static final int countCells = 10;
    private Cell.Type[][] cells = new Cell.Type[countCells][countCells];
    public static final double cellSize = 28.0;

    public Field(GraphicsContext graphics, int x, int y) {
        this.graphics = graphics;
        graphics.setFill(Color.gray(0.0863));
        graphics.setFont(new Font(15));
        //Отрисовка строки букв
        for (int i = 0; i < countCells; ++i) {
            //this.drawABC123(x,y);
            this.graphics.fillText(String.valueOf((char) ((int) 'A' + i)), (x + i + 1) * cellSize + cellSize / 2.0, cellSize / 1.5);
        }
        //Отрисовка столбца чисел
        for (int i = 0; i < countCells; ++i) {
            // this.drawABC123(x,y);
            this.graphics.fillText(String.valueOf((i + 1)), x * cellSize + cellSize / 2.5, (i + 1) * cellSize + cellSize / 1.5);
        }
        for (int i = 0; i < countCells; ++i) {
            for (int j = 0; j < countCells; ++j) {
                cells[i][j] = Cell.Type.EMPTY;
            }
        }
    }

    public void clearFrom(Cell.Type type) {
        for (int i = 0; i < countCells; ++i) {
            for (int j = 0; j < countCells; ++j) {
                if (cells[i][j] == type)
                    cells[i][j] = Cell.Type.EMPTY;
            }
        }
    }

    //Отрисовка каждой ячейки поля, просто обновление поля
    public void draw(int x, int y) {
        for (int i = 0; i < countCells; ++i) {
            for (int j = 0; j < countCells; ++j) {
                drawCell(cells[i][j], x + i + 1, y + j + 1);
            }
        }
    }

    public void setCell(int x, int y, Cell.Type type) {
        if (x >= 0 && x <= 9 && y >= 0 && y <= 9)
            cells[x][y] = type;
    }

    public Cell.Type getTypeCell(int x, int y) {
        if (x >= 0 && y >= 0 && x <= 9 && y <= 9)
            return cells[x][y];
        else
            return null;
    }

    //Проверка, умер ли корабль и его обводка в положительном случае
    public boolean checkDeadShips(int x0, int y0) {
        //Если ориентация коробля - горизонтальная
        if (getTypeCell(x0 - 1, y0) == FIRED || getTypeCell(x0 + 1, y0) == FIRED ||
                getTypeCell(x0 - 1, y0) == SHIP || getTypeCell(x0 + 1, y0) == SHIP) {
            //Координаты x рассматриваемой ячейки подбитого коробля слева и справа
            int xR = x0 + 1;
            int xL = x0 - 1;
            //Проверка коробля справа
            while (getTypeCell(xR, y0) == FIRED) { xR++; }
            if (getTypeCell(xR, y0) == SHIP)
                return false;
            //Проверка коробля слева
            while (getTypeCell(xL, y0) == FIRED) { xL--; }
            if (getTypeCell(xL, y0) == SHIP)
                return false;
            //Корабль подбит польность, теперь окружаем его точками
            setCell(xL, y0, MISS);
            setCell(xR, y0, MISS);
            for (int bufX = xL; bufX <= xR; bufX++) {
                setCell(bufX, y0 - 1, MISS);
                setCell(bufX, y0 + 1, MISS);
            }
            return true;
        } else//Если ориентация коробля - вертикальная или корабль однопалубный
            if (getTypeCell(x0, y0 - 1) == FIRED || getTypeCell(x0, y0 + 1) == FIRED ||
                    getTypeCell(x0, y0 - 1) == SHIP || getTypeCell(x0, y0 + 1) == SHIP) {
                //Координата Y сверху и снизу от рассматриваемой ячейки, учитывая, что ось Y направлена вниз
                int yU = y0 - 1;
                int yD = y0 + 1;
                //Проверка коробля cверху
                while (getTypeCell(x0, yU) == FIRED) { yU--; }
                if (getTypeCell(x0, yU) == SHIP)
                    return false;
                //Проверка коробля cнизу
                while (getTypeCell(x0, yD) == FIRED) { yD++; }
                if (getTypeCell(x0, yD) == SHIP)
                    return false;
                //Корабль подбит полностью, теперь окружаем его точками
                setCell(x0, yU, MISS);
                setCell(x0, yD, MISS);
                for (int bufY = yU; bufY <= yD; bufY++) {
                    setCell(x0 - 1, bufY, MISS);
                    setCell(x0 + 1, bufY, MISS);
                }
                return true;
            } else {
                setCell(x0, y0 - 1, MISS);
                setCell(x0, y0 + 1, MISS);
                for (int bufY = y0 - 1; bufY <= y0 + 1; bufY++) {
                    setCell(x0 - 1, bufY, MISS);
                    setCell(x0 + 1, bufY, MISS);
                }
                return true;
            }
    }

    //Правильно ли расставлены все коробли на поле
    public boolean shipsIsCorrect() {
        //Коробли с n палубами и их необходимое количество
        int ship4 = 1;
        int ship3 = 2;
        int ship2 = 3;
        int ship1 = 4;
        //Первый двойной цикл для горизонтальных кораблей
        for (int y0 = 0; y0 < countCells; y0++) {
            for (int x0 = 0; x0 < countCells; x0++) {
                if (getTypeCell(x0, y0) == SHIP && (getTypeCell(x0 - 1, y0) != SHIP && getTypeCell(x0 + 1, y0) == SHIP)) {
                    int bufX = x0;//Сейчас x0 - первая ячейка коробля слева
                    //System.out.println(bufX + "   :   ");
                    while (getTypeCell(bufX++, y0) == SHIP) ;
                    bufX--;//Первая ячейка после коробля справа
                    int len = bufX - x0; //Длина коробля
                    switch (len) {
                        case 1:
                            if (--ship1 < 0)
                                return false;
                            break;
                        case 2:
                            if (--ship2 < 0)
                                return false;
                            break;
                        case 3:
                            if (--ship3 < 0)
                                return false;
                            break;
                        case 4:
                            if (--ship4 < 0)
                                return false;
                            break;
                        default:
                            return false;
                    }
                    for (int ix = x0 - 1; ix <= bufX; ix++) {
                        if (getTypeCell(ix, y0 - 1) == SHIP) return false;
                        if (getTypeCell(ix, y0 + 1) == SHIP) return false;
                    }
                }
            }
        }
        //Второй двойной цикл для вертикальных короблей
        for (int x0 = 0; x0 < countCells; x0++) {
            for (int y0 = 0; y0 < countCells; y0++) {
                if (getTypeCell(x0, y0) == SHIP && (getTypeCell(x0, y0 - 1) != SHIP && getTypeCell(x0, y0 + 1) == SHIP)) {
                    int bufY = y0;//Сейчас x0 - первая ячейка коробля сверху
                    while (getTypeCell(x0, bufY++) == SHIP) ;
                    bufY--;//Первая ячейка после коробля cнизу
                    int len = bufY - y0; //Длина коробля
                    switch (len) {
                        case 1:
                            if (--ship1 < 0)
                                return false;
                            break;
                        case 2:
                            if (--ship2 < 0)
                                return false;
                            break;
                        case 3:
                            if (--ship3 < 0)
                                return false;
                            break;
                        case 4:
                            if (--ship4 < 0)
                                return false;
                            break;
                        default:
                            return false;
                    }
                    for (int iy = y0 - 1; iy <= bufY; iy++) {
                        if (getTypeCell(x0 - 1, iy) == SHIP) return false;
                        if (getTypeCell(x0 + 1, iy) == SHIP) return false;
                    }
                }
                else if (getTypeCell(x0,y0) == SHIP && getTypeCell(x0 + 1,y0) != SHIP && getTypeCell(x0 - 1 ,y0) != SHIP &&
                        getTypeCell(x0,y0 + 1) != SHIP && getTypeCell(x0,y0 - 1) != SHIP){
                        if (getTypeCell(x0 + 0, y0 - 1) == SHIP) return false;
                        if (getTypeCell(x0 + 0, y0 + 1) == SHIP) return false;
                        if (getTypeCell(x0 + 1, y0 - 1) == SHIP) return false;
                        if (getTypeCell(x0 + 1, y0 + 0) == SHIP) return false;
                        if (getTypeCell(x0 + 1, y0 + 1) == SHIP) return false;
                        if (getTypeCell(x0 - 1, y0 - 1) == SHIP) return false;
                        if (getTypeCell(x0 - 1, y0 + 0) == SHIP) return false;
                        if (getTypeCell(x0 - 1, y0 + 1) == SHIP) return false;
                        if (--ship1 < 0) return false;
                }
            }
        }
        if (ship1 != 0 || ship2 != 0 || ship3 != 0 || ship4 != 0)
            return false;
        return true;
    }

    //Отрисовка кадой ячейки в зависимости от её типа
    private void drawCell(Cell.Type type, int x, int y) {
        switch (type) {
            case EMPTY:
                graphics.setLineWidth(1);
                graphics.setFill(Color.color(0.1922, 0.3255, 0.6));
                graphics.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
                break;

            case SHIP:
                graphics.setLineWidth(1);
                graphics.setFill(Color.gray(0.4275 ));
                graphics.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
                break;

            case FIRED:
                graphics.setLineWidth(1);
                graphics.setFill(Color.gray(0.4275));
                graphics.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);

                graphics.setStroke(Color.color(1, 0.0824, 0.1137));
                graphics.strokeLine(x * cellSize + 1, y *cellSize + 1, (x + 1) * cellSize, (y + 1) * cellSize);
                graphics.strokeLine((x + 1) * cellSize, y * cellSize, x * cellSize + 1, (y + 1) * cellSize - 1);
                break;

            case MISS:
                graphics.setLineWidth(1);
                graphics.setFill(Color.color(0.1922, 0.3255, 0.6));
                graphics.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
                graphics.setFill(Color.color(0.8471, 0.9765, 0.9843));
                graphics.fillOval((x + 1 / 3.0) * cellSize, (y + 1 / 3.0) * cellSize, cellSize / 3.0f, cellSize / 3.0f);
                break;

            case AIM:
                graphics.setLineWidth(2);
                graphics.setStroke(Color.color(1, 0.4941, 0.1647));
                graphics.strokeLine((x + 0.5) * cellSize, y * cellSize + 2, (x + 0.5) * cellSize, (y + 1) * cellSize - 2);
                graphics.strokeLine(x * cellSize + 2, (y + 0.5) * cellSize, (x + 1) * cellSize - 2, (y + 0.5) * cellSize);
                break;
        }
        //Обводка у каждой ячейки
        graphics.setStroke(Color.color(0.0863, 0.0863, 0.0863, 0.2));
        graphics.strokeRect(x * cellSize, y * cellSize, cellSize, cellSize);
    }
}