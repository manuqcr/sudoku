package fr.sudoku;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.function.Function;

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
     * Commençons à tester pour un sous-groupe de taille 1
     */
    @Test
    public void testGetSubsetById_Size1() {
        createBoardForFile("src/test/resources/board_testGetSubsetById.txt");
        // On a besoin d'une liste car les éléments d'un Set n'ont pas d'ordre
    //    ArrayList<Cell> cells = new ArrayList<>(board.listAllInSquare(0, Function.identity()));
        // Pour simplifier les tests, on retrie les cellules (car comme elles proviennent d'un Set, on
        // n'a aucune idée de l'ordre dans lequel elles sont) :
      //  cells.sort((c1, c2) -> c1.getValue().compareTo(c2.getValue()));


        for (int sequenceId = 0; sequenceId < 9; ++sequenceId) {
            //List<Cell> subset = board.getSubsetById(cells, 1, sequenceId);
            // Il ne doit y avoir qu'un élément
            //assertEquals(1, subset.size());
            // Sa valeur doit être 1, puis 2, puis 3... (et on affiche un message plus précis en cas d'erreur)
            // Le cast est obligé sinon il y a un conflit entre ces deux méthodes et java ne sait pas laquelle appeler :
            //   assertEquals(String message, long expected, long actual)
            //   assertEquals(String message, Object expected, Object actual)
            // C'est un cas un peu particulier
            //assertEquals("Taille 1, séquence " + sequenceId, sequenceId + 1, (int) subset.get(0).getValue());
        }
    }

    /**
     * Maintenant avec un sous-groupe de taille 2
     *//*
    @Test
    public void testGetSubsetById_Size2() {
        createBoardForFile("src/test/resources/board_testGetSubsetById.txt");
        ArrayList<Cell> cells = new ArrayList<>(board.listAllInSquare(0, Function.identity()));
        cells.sort((c1, c2) -> c1.getValue().compareTo(c2.getValue()));

        // Commençons à tester pour un élément de taille 1
        for (int sequenceId = 0; sequenceId < 9; ++sequenceId) {
        //    List<Cell> subset = board.getSubsetById(cells, 1, sequenceId);
            // Il ne doit y avoir qu'un élément
            assertEquals(2, subset.size());
        }

        // Testons des cas particuliers. Le plus simple pour les tests c'est
        // sûrement de transformer la liste en chaîne de caractères et de s'assurer
        // qu'elle est bien celle qu'on pensait.
        Assert.assertEquals("[1, 2]", board.getSubsetById(cells, 2, 0).toString());
        Assert.assertEquals("[1, 3]", board.getSubsetById(cells, 2, 1).toString());
        Assert.assertEquals("[1, 4]", board.getSubsetById(cells, 2, 2).toString());
        Assert.assertEquals("[1, 9]", board.getSubsetById(cells, 2, 8).toString());
        Assert.assertEquals("[2, 3]", board.getSubsetById(cells, 2, 9).toString());
        Assert.assertEquals("[2, 4]", board.getSubsetById(cells, 2, 10).toString());
        Assert.assertEquals("[2, 9]", board.getSubsetById(cells, 2, 15).toString());
        Assert.assertEquals("[3, 1]", board.getSubsetById(cells, 2, 16).toString());
        Assert.assertEquals("[3, 9]", board.getSubsetById(cells, 2, 24).toString());
        Assert.assertEquals("[4, 1]", board.getSubsetById(cells, 2, 25).toString());
        Assert.assertEquals("[4, 9]", board.getSubsetById(cells, 2, 33).toString());
        Assert.assertEquals("[5, 1]", board.getSubsetById(cells, 2, 34).toString());
        Assert.assertEquals("[5, 9]", board.getSubsetById(cells, 2, 42).toString());
        Assert.assertEquals("[6, 1]", board.getSubsetById(cells, 2, 43).toString());
        Assert.assertEquals("[6, 9]", board.getSubsetById(cells, 2, 51).toString());
        Assert.assertEquals("[7, 1]", board.getSubsetById(cells, 2, 52).toString());
        Assert.assertEquals("[7, 9]", board.getSubsetById(cells, 2, 60).toString());
        Assert.assertEquals("[8, 1]", board.getSubsetById(cells, 2, 61).toString());
        Assert.assertEquals("[8, 9]", board.getSubsetById(cells, 2, 69).toString());
        Assert.assertEquals("[9, 1]", board.getSubsetById(cells, 2, 70).toString());
        Assert.assertEquals("[9, 9]", board.getSubsetById(cells, 2, 78).toString());
    }

    @Test
    public void testGetCellIndexForSubsetId() {

        // Pour un set de taille 1
        for (int i = 0; i < 9; ++i){
            assertEquals(i, board.getCellIndexForSubsetId(1, i, 0));
        }

        // Pour un set de taille 2
        int i = 0;
        assertEquals(0, board.getCellIndexForSubsetId(2, i, 0));
        assertEquals(1, board.getCellIndexForSubsetId(2, i, 1));

        ++i;
        assertEquals(0, board.getCellIndexForSubsetId(2, i, 0));
        assertEquals(2, board.getCellIndexForSubsetId(2, i, 1));

        ++i;
        assertEquals(0, board.getCellIndexForSubsetId(2, i, 0));
        assertEquals(3, board.getCellIndexForSubsetId(2, i, 1));

        i+=5;
        assertEquals(0, board.getCellIndexForSubsetId(2, i, 0));
        assertEquals(8, board.getCellIndexForSubsetId(2, i, 1));

        ++i;
        assertEquals(1, board.getCellIndexForSubsetId(2, i, 0));
        assertEquals(2, board.getCellIndexForSubsetId(2, i, 1));
    }*/

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
            board.applyRuleNPossibleValuesInNCells();
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
