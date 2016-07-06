import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class MainDialog extends JDialog {
    private JPanel contentPane;
    GridLayout gridManager = new GridLayout(9,9);

    ArrayList<JButton> buttons = new ArrayList<JButton>(81);

    public MainDialog() {

        setContentPane(contentPane);
        setModal(true);

        setLayout(gridManager);
        generateGrid();

        getButton(0,5).setBackground(Color.BLACK);
    }

    JButton getButton(int row, int column){
        return buttons.get(row*9+column);
    }

    private void generateGrid() {
        for (int i = 0; i < 9; ++i){
            for (int j = 0; j < 9; ++j) {
                JButton button = new JButton(i + " " + j);
                buttons.add(button);
                this.contentPane.add(button);
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
