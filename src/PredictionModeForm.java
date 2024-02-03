import NeuralNetwork.NeuralNetwork;
import NeuralNetwork.NNPrediction;
import Sampling.Sample;
import Sampling.SampleImage;
import Utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PredictionModeForm extends JFrame {
    private JPanel panel1;
    private JTextField fileNameTextField;
    private JTable drawingGrid;
    private JButton deleteButton;
    private JButton predictButton;
    private JLabel predictionLabelResult;
    private JButton backButton;
    private JButton confirmFileButton;
    private int rowSelected;
    private int colSelected;
    private boolean penDown;

    public PredictionModeForm() {
        setContentPane(panel1);
        setTitle("Prediction Mode");
        setSize(720, 720);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);

        predictButton.setEnabled(false);
        deleteButton.setEnabled(false);
        NeuralNetwork NN = new NeuralNetwork(true);
        penDown = false;

        confirmFileButton.addActionListener(e -> {
            String fileName = fileNameTextField.getText();
            int loadStatus = NN.loadFromJSON(fileName);

            // File could not load
            if (loadStatus == 1) {
                fileNameTextField.setForeground(Color.RED);
                predictButton.setEnabled(false);
                deleteButton.setEnabled(false);
                return;
            }

            // Could load
            fileNameTextField.setForeground(Color.GREEN);
            predictButton.setEnabled(true);
            deleteButton.setEnabled(true);
        });
        predictButton.addActionListener(e -> {
            int[] binaryArray;
            NNPrediction prediction;
            Sample sampleInput;

            binaryArray = Utils.tableToBinaryArray(drawingGrid);
            // We do not care about the label here
            sampleInput = new Sample(new SampleImage(binaryArray), 0);
            prediction = NN.predict(sampleInput);
            predictionLabelResult.setText(String.format("%d", prediction.getResult()));
        });
        drawingGrid.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                penDown = !penDown;
            }
        });
        backButton.addActionListener(e -> dispose());
        deleteButton.addActionListener(e -> clear());
        drawingGrid.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
                if (penDown) {
                    rowSelected = drawingGrid.rowAtPoint(e.getPoint());
                    colSelected = drawingGrid.columnAtPoint(e.getPoint());
                    if (drawingGrid.getValueAt(rowSelected, colSelected) == null) {
                        drawingGrid.setValueAt("///", rowSelected, colSelected);
                    }
                }
            }
        });
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

