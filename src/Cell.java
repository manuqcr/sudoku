import javax.swing.*;

/**
 * Created by emmanuel on 07/07/16.
 */
public class Cell extends JButton {

    private final JSpinner spinner;

    public Cell(JSpinner spinner) {
        this.spinner = spinner;
        // à chaque clic, on récupère la valeur du spinner et on la met en label du bouton
        addActionListener(actionEvent -> {setText(""+spinner.getValue());});
    }


}
