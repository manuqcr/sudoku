package fr.sudoku;

import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 * Created by emmanuel on 10/07/16.
 */
public class BoardTest {

    Board board;

    @Before
    public void init() {
        board = new Board();
        for (int i = 0; i < 81; ++i) {
            board.add(new Cell(null));
        }
        board.buildTransposedBoard();
    }

    @Test
    public void test_scenario1() {
        createBoardForFile("src/test/resources/board1.txt");
        assertEquals(31, countNotEmptyCells());

        board.solveSinglePossibleValue();
        assertEquals(33, countNotEmptyCells());

        board.updateAllPossibleValues();
        board.solveSinglePossibleValue();
        assertEquals(34, countNotEmptyCells());

        board.updateAllPossibleValues();
        board.solveSinglePossibleValue();
        assertEquals(35, countNotEmptyCells());

        board.solveSingleCellInRowForValue();
        assertEquals(40, countNotEmptyCells());

        board.updateAllPossibleValues();
        board.solveSingleCellInRowForValue();
        assertEquals(41, countNotEmptyCells());

        bruteResolve();
        assertEquals(81, countNotEmptyCells());
    }

    @Test
    public void test_scenario2() {
        createBoardForFile("src/test/resources/board2.txt");
        assertEquals(38, countNotEmptyCells());
        int steps = bruteResolve();
        assertEquals(81, countNotEmptyCells());
        assertEquals(2, steps);
    }

    @Test
    public void test_scenario3() {
        createBoardForFile("src/test/resources/board3.txt");
        assertEquals(26, countNotEmptyCells());
        int steps = bruteResolve();
        assertEquals(81, countNotEmptyCells());
        assertEquals(3, steps);
    }

    @Test
    public void test_scenario4() {
        createBoardForFile("src/test/resources/board4.txt");
        assertEquals(26, countNotEmptyCells());
        int steps = bruteResolve();
        assertEquals(81, countNotEmptyCells());
        assertEquals(3, steps);
    }

    /**
     * Applique chacune des règles tant qu'au moins une a un effet
     *
     * @return le nombre d'étapes nécessaires
     */
    int bruteResolve() {
        int steps = 0;
        int notEmptyCount = 0;
        while (notEmptyCount != countNotEmptyCells()) {
            steps++;
            notEmptyCount = countNotEmptyCells();
            board.updateAllPossibleValues();
            board.solveSinglePossibleValue();
            board.solveSingleCellInColumnForValue();
            board.solveSingleCellInRowForValue();
            board.solveSingleCellInSquareForValue();
        }
        return steps;
    }

    void createBoardForFile(String filename) {
        board.loadFile(new File(filename));
    }

    /**
     * Renvoie le nombre de cellules dont on connaît la valeur de manière sûre
     *
     * @return le nombre de cellules
     */
    int countNotEmptyCells() {
        int notEmptyCount = 0;
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                if (board.getCell(row, column).getValue() != null) {
                    notEmptyCount++;
                }
            }
        }
        return notEmptyCount;
    }
}
