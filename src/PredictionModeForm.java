import LinearAlgebra.Matrix;
import Utils.Utils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

public class PredictionModeForm extends JFrame {
    private JPanel panel1;
    private JTextField textField1;
    private JTable drawingGrid;
    private JButton deleteButton;
    private JButton predictButton;
    private JLabel predictionLabelResult;
    private JButton backButton;
    private int rowSelected;
    private int colSelected;

    public PredictionModeForm() {
        setContentPane(panel1);
        setTitle("Prediction Mode");
        setSize(720, 720);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);

        drawingGrid.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                rowSelected = drawingGrid.rowAtPoint(e.getPoint());
                colSelected = drawingGrid.columnAtPoint(e.getPoint());
                if (drawingGrid.getValueAt(rowSelected, colSelected) == null) {
                    drawingGrid.setValueAt("///", rowSelected, colSelected);
                } else {
                    drawingGrid.setValueAt(null, rowSelected, colSelected);
                }
            }
        });
        backButton.addActionListener(e -> dispose());
        deleteButton.addActionListener(e -> clear());
    }

    private void createUIComponents() {
        drawingGrid = new JTable(28, 28) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void clear() {
        for (int i = 0; i < 28; ++i) {
            for (int j = 0; j < 28; ++j) {
                drawingGrid.setValueAt(null, i, j);
            }
        }
    }
}

