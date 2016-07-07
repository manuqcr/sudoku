import java.util.ArrayList;

/**
 * Created by emmanuel on 07/07/16.
 */
public class Board {

    private ArrayList<Cell> cells = new ArrayList<>(81);

    Cell getCell(int row, int column) {
        return cells.get(row * 9 + column);
    }

    public void add(Cell cell) {
        cells.add(cell);
    }

    public void lockEverything(){
        cells.forEach(cell -> cell.lock());
    }
}
