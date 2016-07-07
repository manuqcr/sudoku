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

    GridBagLayout cellLayout = new GridBagLayout();

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
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;

        c.gridwidth = 1;
        c.gridheight = 11;
        c.gridx = 3;
        c.gridy = 0;
        JButton separatorC4 = new JButton();
        separatorC4.setEnabled(false);
        gridPanel.add(separatorC4, c);

        c.gridx = 7;
        c.gridy = 0;
        JButton separatorC8 = new JButton();
        separatorC8.setEnabled(false);
        gridPanel.add(separatorC8, c);

        c.gridwidth = 11;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 3;
        JButton separatorL4 = new JButton();
        separatorL4.setText(" ");
        separatorL4.setEnabled(false);
        gridPanel.add(separatorL4, c);

        c.gridx = 0;
        c.gridy = 7;
        JButton separatorL8 = new JButton();
        separatorL8.setText(" ");
        separatorL8.setEnabled(false);
        gridPanel.add(separatorL8, c);



        c.gridy=0;
        for (int row = 0; row < 9; ++row) {
            c.gridx=0;

            for (int column = 0; column < 9; ++column) {

                Cell cell = new Cell(spinner1);
                cell.setText("-");
                cell.setMargin(new Insets(0,0,0,0));

                cell.setMinimumSize(new Dimension(60,60));
                cell.setPreferredSize(new Dimension(60,60));

                c.gridwidth = 1;
                c.gridheight = 1;
                if (cells.isEmpty()){
                    c.anchor = GridBagConstraints.PAGE_START;
                } else if (cells.size() == 80){
                    c.anchor = GridBagConstraints.PAGE_END;
                }

                cells.add(cell);
                gridPanel.add(cell,c);

                /* Ajout d'un élément vide pour séparer les carrés horizontalement */
                if (column == 2 || column == 5) {
                    c.gridx ++;
                }
                c.gridx++;
            }
            /* Ajout d'une ligne d'élément vide pour séparer les carrés verticalement */
            if (row == 2 || row == 5) {
                c.gridy++;
            }
            c.gridy ++;
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
