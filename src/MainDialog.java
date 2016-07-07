import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class MainDialog extends JDialog {
    private JPanel contentPane;
    private JButton regle1Button;
    private JButton regle2Button;
    private JPanel gridPanel;
    private JLabel mainLabel;
    private JSpinner spinner1;

    GridLayout cellLayout = new GridLayout(11, 11);

    ArrayList<Cell> cells = new ArrayList<>(81);

    public MainDialog() {
        setContentPane(contentPane);
        setModal(true);

        generateGrid();
        spinner1.setValue(1);

        /* Pour que quand on appuie sur un chiffre au clavier, ce chiffre soit séléctionné */
        for (int i = 0; i < 9; ++i) {
            ActionListener actionListener = getActionListener(i + 1);
            getRootPane().registerKeyboardAction(actionListener,
                    KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD1 + i, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        }

        /* Ici on enregistre les règles : */
        regle1Button.addActionListener((actionEvent) -> colorCellInBlue());
    }

    private ActionListener getActionListener(final int i) {
        return (actionEvent) -> {
            spinner1.setValue(i);
        };
    }

    void colorCellInBlue() {
        getCell(0, 5).setBackground(Color.BLUE);
    }

    Cell getCell(int row, int column) {
        return cells.get(row * 9 + column);
    }

    private void generateGrid() {
        gridPanel.setLayout(cellLayout);
        for (int row = 0; row < 9; ++row) {
            for (int column = 0; column < 9; ++column) {
                Cell cell = new Cell(spinner1);
                cells.add(cell);
                gridPanel.add(cell);
                /* Ajout d'un élément vide pour séparer les carrés horizontalement */
                if (column == 2 || column == 5) {
                    gridPanel.add(new JLabel());
                }
            }
            /* Ajout d'une ligne d'élément vide pour séparer les carrés verticalement */
            if (row == 2 || row == 5) {
                for (int column = 0; column < 11; ++column) {
                    gridPanel.add(new JLabel());
                }
            }
        }
    }

    public static void main(String[] args) {
        MainDialog dialog = new MainDialog();
        dialog.setMinimumSize(new Dimension(400, 400));
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
