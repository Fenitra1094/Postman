package All ; 
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class FileChooserEditor extends AbstractCellEditor implements TableCellEditor {
    private JButton button;       // Bouton affiché dans la cellule
    private String filePath = ""; // Chemin du fichier sélectionné
    private JFileChooser fileChooser;

    public FileChooserEditor() {
        // Initialiser le bouton
        button = new JButton("Choisir un fichier...");
        button.setFocusable(false); // Empêche le focus visuel sur le bouton

        // Initialiser le JFileChooser
        fileChooser = new JFileChooser();

        // Action déclenchée lorsque le bouton est cliqué
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = fileChooser.showOpenDialog(button);
                if (result == JFileChooser.APPROVE_OPTION) {
                    // Récupérer le chemin du fichier sélectionné
                    filePath = fileChooser.getSelectedFile().getAbsolutePath();
                    stopCellEditing(); // Confirmer l'édition
                }
            }
        });
    }

    @Override
    public Object getCellEditorValue() {
        // Retourne le chemin du fichier sélectionné
        return filePath;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        // Met à jour le bouton avec la valeur actuelle de la cellule
        filePath = value == null ? "" : value.toString();
        return button;
    }
}