import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class MainDialog extends JDialog {
    private JPanel contentPane;
    private JButton regle1Button;
    private JButton regle2Button;
    private JPanel gridPanel;

    GridLayout cellLayout = new GridLayout(9,9);

    ArrayList<JTextField> cells = new ArrayList<JTextField>(81);

    public MainDialog() {
        setContentPane(contentPane);
        setModal(true);

        generateGrid();

        /* Ici on enregistre les règles : */
        regle1Button.addActionListener((actionEvent) -> colorCellInBlue());
    }


    void colorCellInBlue(){
        getCell(0, 5).setBackground(Color.BLUE);
    }

    JTextField getCell(int row, int column){
        return cells.get(row*9+column);
    }

    private void generateGrid() {
        this.gridPanel.setLayout(cellLayout);
        for (int i = 0; i < 9; ++i){
            for (int j = 0; j < 9; ++j) {
                JTextField cell = new JTextField();
                cells.add(cell);
                this.gridPanel.add(cell);
            }
        }
    }

    public static void main(String[] args) {
        MainDialog dialog = new MainDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
