import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

public class MainDialog extends JDialog {
    GridBagLayout cellLayout = new GridBagLayout();
    Board board = new Board();
    private JPanel contentPane;
    private JButton lockButton;
    private JButton regleVerifierButton;
    private JPanel gridPanel;
    private JLabel mainLabel;
    private JSpinner spinner1;
    private JButton ouvrirButton;
    private JButton sauverButton;

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
        lockButton.addActionListener((actionEvent) -> board.lockEverything());
        sauverButton.addActionListener((actionEvent -> saveBoard()));
        ouvrirButton.addActionListener((actionEvent -> loadBoard()));
        regleVerifierButton.addActionListener((actionEvent -> board.findErrors()));

    }

    public static void main(String[] args) {
        MainDialog dialog = new MainDialog();
        dialog.setMinimumSize(new Dimension(400, 400));
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
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
        return (actionEvent) -> {
            spinner1.setValue(i);
        };
    }

    private void generateGrid() {
        gridPanel.setLayout(cellLayout);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;

        /* Faux bouton qui fait toute la colonne 4 pour séparer les groupes de 9 carrés */
        c.gridwidth = 1;
        c.gridheight = 11;
        c.gridx = 3;
        c.gridy = 0;
        JButton separatorC4 = new JButton();
        separatorC4.setEnabled(false);
        gridPanel.add(separatorC4, c);

        /* Faux bouton qui fait toute la colonne 8 pour séparer les groupes de 9 carrés */
        c.gridx = 7;
        c.gridy = 0;
        JButton separatorC8 = new JButton();
        separatorC8.setEnabled(false);
        gridPanel.add(separatorC8, c);

        /* Faux bouton qui fait toute la ligne 4 pour séparer les groupes de 9 carrés */
        c.gridwidth = 11;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 3;
        JButton separatorL4 = new JButton();
        separatorL4.setText(" ");
        separatorL4.setEnabled(false);
        gridPanel.add(separatorL4, c);

        /* Faux bouton qui fait toute la ligne 8 pour séparer les groupes de 9 carrés */
        c.gridx = 0;
        c.gridy = 7;
        JButton separatorL8 = new JButton();
        separatorL8.setText(" ");
        separatorL8.setEnabled(false);
        gridPanel.add(separatorL8, c);

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
    }
}
