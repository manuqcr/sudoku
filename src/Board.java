import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

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

    public void lockEverything() {
        cells.forEach(cell -> cell.lock());
    }

    public void saveToFile(File targetFile) {

        try (FileOutputStream fileOutputStream = new FileOutputStream(targetFile)) {
            for (int i = 0; i < 81; ++i) {
                if (i > 0 && i % 9 == 0) {
                    fileOutputStream.write('\n');
                }
                Cell cell = cells.get(i);
                String cellOutput = " ";
                if (cell.isLocked()) {
                    cellOutput = cell.chosenValue.toString();
                }
                fileOutputStream.write(cellOutput.getBytes());
            }
            fileOutputStream.flush();
        } catch (IOException e) {

        }
    }

    public void loadFile(File inputFile) {
        cells.forEach(Cell::reset);

        AtomicInteger lineNb = new AtomicInteger(0);
        try (Stream<String> stream = Files.lines(Paths.get(inputFile.getAbsolutePath()))) {
            stream.forEach(line -> loadLine(line, lineNb.getAndIncrement()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        lockEverything();
    }

    private void loadLine(String line, int rowNb) {
        AtomicInteger columnNumber = new AtomicInteger(0);
        line.chars().forEach(character -> {
                    Cell cell = getCell(rowNb, columnNumber.get());
                    int intValue = character - '0';
                    columnNumber.incrementAndGet();
                    if (intValue > 0 && intValue < 10) {
                        cell.setValue(intValue);
                    }
                    cell.updateText();
                }
        );
    }
}
