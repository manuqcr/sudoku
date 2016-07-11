package fr.sudoku;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

public class MainDialog extends JFrame {
    private final GridBagLayout cellLayout = new GridBagLayout();
    private final Board board = new Board();
    private JPanel contentPane;
    private JButton lockButton;
    private JButton regleVerifierButton;
    private JPanel gridPanel;
    private JLabel mainLabel;
    private JSpinner spinner1;
    private JButton ouvrirButton;
    private JButton sauverButton;
    private JButton majValeursPossiblesButton;
    private JButton uneSeuleValeurPossibleButton;
    private JButton seulEndroitPossibleSurButton;
    private JButton seulEndroitPossibleSurButton1;
    private JButton seulEndroitPossibleSurButton2;

    public MainDialog() {
        setContentPane(contentPane);

        generateGrid();
        spinner1.setValue(1);

        /* Pour que quand on appuie sur un chiffre au clavier, ce chiffre soit séléctionné */
        for (int i = 0; i < 9; ++i) {
            ActionListener actionListener = getActionListener(i + 1);
            getRootPane().registerKeyboardAction(actionListener,
                    KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD1 + i, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        }

        /* Ici on enregistre les règles : */
        sauverButton.addActionListener(actionEvent -> saveBoard());
        ouvrirButton.addActionListener(actionEvent -> loadBoard());
        lockButton.addActionListener(actionEvent -> board.lockEverything());
        regleVerifierButton.addActionListener(actionEvent -> board.findErrors());
        majValeursPossiblesButton.addActionListener(actionEvent -> board.updateAllPossibleValues());
        uneSeuleValeurPossibleButton.addActionListener(actionEvent -> board.solveSinglePossibleValue());
        seulEndroitPossibleSurButton.addActionListener(actionEvent -> board.solveSingleCellInRowForValue());
        seulEndroitPossibleSurButton1.addActionListener(actionEvent -> board.solveSingleCellInColumnForValue());
        seulEndroitPossibleSurButton2.addActionListener(actionEvent -> board.solveSingleCellInSquareForValue());

    }

    public static void main(String[] args) {
        MainDialog dialog = new MainDialog();
        dialog.setMinimumSize(new Dimension(400, 400));
        dialog.pack();
        dialog.setTitle("Sudoku");
        dialog.setVisible(true);
        dialog.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);;
    }

    private void loadBoard() {
        final JFileChooser fc = new JFileChooser();
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            board.loadFile(file);
        }
    }

    private void saveBoard() {
        final JFileChooser fc = new JFileChooser();
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            board.saveToFile(file);
        }
    }

    private ActionListener getActionListener(final int i) {
        return (actionEvent) -> spinner1.setValue(i);
    }

    private void generateGrid() {
        gridPanel.setLayout(cellLayout);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;

        /* Faux bouton qui fait toute la colonne 4 pour séparer les groupes de 9 carrés */
        addSeparator(c, 3, Orientation.VERTICAL);

        /* Faux bouton qui fait toute la colonne 8 pour séparer les groupes de 9 carrés */
        addSeparator(c, 7, Orientation.VERTICAL);

        /* Faux bouton qui fait toute la ligne 4 pour séparer les groupes de 9 carrés */
        addSeparator(c, 3, Orientation.HORIZONTAL);

        /* Faux bouton qui fait toute la ligne 8 pour séparer les groupes de 9 carrés */
        addSeparator(c, 7, Orientation.HORIZONTAL);

        /* Et là, c'est parti, on génère les 81 cases */
        c.gridy = 0;
        for (int row = 0; row < 9; ++row) {
            c.gridx = 0;

            for (int column = 0; column < 9; ++column) {

                Cell cell = new Cell(spinner1);

                c.gridwidth = 1;
                c.gridheight = 1;
                if (row == 0 && column == 0) {
                    c.anchor = GridBagConstraints.PAGE_START;
                } else if (row == 8 && column == 8) {
                    c.anchor = GridBagConstraints.PAGE_END;
                }

                board.add(cell);
                cell.addToPanel(gridPanel, c);

                /* Ajout d'un élément vide pour séparer les carrés horizontalement */
                if (column == 2 || column == 5) {
                    c.gridx++;
                }
                c.gridx++;
            }
            /* Ajout d'une ligne d'élément vide pour séparer les carrés verticalement */
            if (row == 2 || row == 5) {
                c.gridy++;
            }
            c.gridy++;
        }
        board.buildTransposedBoard();
    }

    private void addSeparator(GridBagConstraints c, int position, Orientation orientation) {
        boolean isVertical = orientation.isVertical();

        c.gridwidth = isVertical ? 1 : 11;
        c.gridheight = isVertical ? 11 : 1;

        c.gridx = isVertical ? position : 0;
        c.gridy = isVertical ? 0 : position;

        c.ipadx = 10;
        c.ipady = 10;

        JButton separator = new JButton();
        separator.setEnabled(false);
        separator.setBorder(BorderFactory.createEmptyBorder());
        gridPanel.add(separator, c);
    }

    enum Orientation {
        VERTICAL(true),
        HORIZONTAL(false);

        boolean vertical;

        Orientation(boolean v) {
            vertical = v;
        }

        public boolean isVertical() {
            return vertical;
        }
    }
}
