import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;

/**
 * Created by emmanuel on 07/07/16.
 */
public class Cell  {

    private final JButton button;
    private final JSpinner spinner;

    Set<Integer> possibleValues = new HashSet<>();
    Integer chosenValue = null;
    private boolean isLocked = false;
    private boolean isError = false;

    public Cell(JSpinner spinner) {
        button = new JButton();
        this.spinner = spinner;

        button.setMargin(new Insets(0,0,0,0));
        button.setMinimumSize(new Dimension(60,60));
        button.setPreferredSize(new Dimension(60,60));

        // à chaque clic, on récupère la valeur du spinner et on la met en label du bouton
        button.addActionListener(actionEvent -> {
            setValue((Integer) spinner.getValue());
        });

        button.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent mouseEvent) {
                if (mouseEvent.getButton() == 3){
                    addPossibleValue();
                }
            }

            // Ca sert à rien mais on est obligé de le garder :
            public void mousePressed(MouseEvent mouseEvent) {}
            public void mouseReleased(MouseEvent mouseEvent) {}
            public void mouseEntered(MouseEvent mouseEvent) {}
            public void mouseExited(MouseEvent mouseEvent) {}
        });
        updateText();
    }

    /**
     * Ajoute à la cellule une valeur que l'utilisateur pense possible pour la case
     */
    private void addPossibleValue() {
        if (isLocked && chosenValue != null) {
            return;
        }
        chosenValue = null;

        Integer value = (Integer) spinner.getValue();
        if (!possibleValues.contains(value)){
            possibleValues.add(value);
        } else {
            possibleValues.remove(value);
        }
        updateText();
    }

    /**
     * Pour mettre la valeur d'une cellule.
     * Ne pas utiliser pour des valeurs possibles, mais pour l'unique valeur qu'on pense que la cellule doit contenir
     * @param i valeur en question
     */
    public void setValue(int i) {
        if (isLocked) {
            return;
        }
        if (chosenValue == null || chosenValue != i){
            chosenValue = new Integer(i);
        } else {
            chosenValue = null;
        }
        isError = false;
        updateText();
    }

    public void updateText(){
        if (isLocked){
            button.setBackground(new Color(255,255,200));
        } else {
            button.setBackground(isError ? Color.RED : Color.WHITE);
        }

        if (chosenValue != null){
            // S'il y a une valeur choisie, on affiche celle là en gros
            button.setText("<html><b>" + chosenValue + "</b></html>");
            button.setFont(new Font(button.getFont().getFontName(), Font.PLAIN, 50));
        } else {
            // S'il y a une plusieurs valeurs possibles, on les affiche toutes en petit
            StringBuilder stringBuilder = new StringBuilder("<html><pre>");
            for (int i = 1; i < 10; ++i){
                stringBuilder.append(possibleValues.contains(new Integer(i))? i : " ");
                stringBuilder.append(i%3 == 0 ? "<br/>" : " ");
            }
            stringBuilder.append("</pre></html>");
            button.setText(stringBuilder.toString());
            button.setFont(new Font(button.getFont().getFontName(), Font.PLAIN, 12));
        }
    }

    public void addToPanel(JPanel gridPanel, GridBagConstraints c) {
        gridPanel.add(button, c);
    }

    public void lock() {
        if (chosenValue == null){
            return;
        }
        isLocked = true;
        updateText();
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void reset() {
        isLocked = false;
        chosenValue = null;
        possibleValues.clear();
    }

    public Integer getValue() {
        return chosenValue;
    }

    public void flagInError(boolean isError) {
        this.isError = isError;
        updateText();
    }
}
