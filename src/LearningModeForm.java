import NeuralNetwork.NeuralNetwork;
import NeuralNetwork.NNPrediction;
import Sampling.Sample;
import Sampling.SampleImage;
import Utils.Utils;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class LearningModeForm extends JFrame {
    private JPanel panel1;
    private JButton backButton;
    private JLabel totalSamplesLabel;
    private JTextField labelTextField;
    private JTable drawingGrid;
    private JButton deleteButton;
    private JButton addSampleButton;
    private JButton loadPreInstalledSamplesButton;
    private JLabel iterationNoValueLabel;
    private JLabel costLabel;
    private JButton trainButton;
    private JProgressBar learningProgressBar;
    private JButton saveModelButton;
    private int rowSelected;
    private int colSelected;
    private boolean penDown;
    private int iterNumber;
    private int sampleAmount;
    private NNPrediction currPred;
    private NeuralNetwork NN;

    public LearningModeForm() {
        setContentPane(panel1);
        setTitle("Learning Mode");
        setSize(1000, 900);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);

        NN = new NeuralNetwork(false);
        currPred = new NNPrediction(0, 1);
        penDown = false;
        drawingGrid.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                penDown = !penDown;
            }
        });
        backButton.addActionListener(e -> dispose());
        deleteButton.addActionListener(e -> clear());
        addSampleButton.addActionListener(e -> {
            int[] binaryArray;
            Sample sample;
            int label;

            binaryArray = Utils.tableToBinaryArray(drawingGrid);

            try { // Check if there's actually an integer inputted
                label = Integer.parseInt(labelTextField.getText().substring(0, 1));
            } catch (NumberFormatException | StringIndexOutOfBoundsException exception) {
                // Escape if invalid label
                return;
            }
            // Empty array check already implemented elsewhere
            sample = new Sample(new SampleImage(binaryArray), label);
            NN.addSample(sample);
            // If all successful clear grid
            clear();
            // Update sample count
            totalSamplesLabel.setText(String.format("%d", NN.getSampleAmount()));
            labelTextField.setText("");
        });
        loadPreInstalledSamplesButton.addActionListener(e -> {
            // Disable the button (one time use)
            Thread guiChanges = new Thread(() -> {
                loadPreInstalledSamplesButton.setText("Loading...");
                loadPreInstalledSamplesButton.setEnabled(false);
            });
            guiChanges.start();
            Thread loadingSamples = new Thread(() -> {
                loadPreInstalledSamples();
                loadPreInstalledSamplesButton.setText("Pre-Installed Samples Loaded");
            });
            loadingSamples.start();
        });
        trainButton.addActionListener(e -> {
            sampleAmount = NN.getSampleAmount();

            if (sampleAmount == 0) return;

            learningProgressBar.setMaximum(sampleAmount);

            Thread machineLearning = new Thread(this::train);
            machineLearning.start();

            Thread guiUpdates = new Thread(() -> {
                trainButton.setEnabled(false);
                while (iterNumber < sampleAmount - 1) {
                    iterationNoValueLabel.setText(String.format("%d", iterNumber + 1));
                    costLabel.setText(String.format("%f", currPred.getCost()));
                    learningProgressBar.setValue(iterNumber + 1);
                }
                saveModelButton.setEnabled(true);
            });
            guiUpdates.start();
        });
        saveModelButton.addActionListener(e -> {
            // 10 digit random number sequence
            long serialID = Math.round(Math.random() * Math.pow(10, 10));
            String serialStringID = Long.toString(serialID);
            while (serialStringID.length() < 10) {
                // Add 0's to end if not 10 digits
                serialStringID = serialStringID.concat("0");
            }
            String fileName = "NETWORK" + serialStringID;
            NN.storeAsJSON(fileName);
            saveModelButton.setText("Model stored in " + fileName + ".json");
            saveModelButton.setEnabled(false);
        });
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

    private void train() {
        Sample currSample;
        for (iterNumber = 0; iterNumber < sampleAmount; ++iterNumber) {
            currSample = NN.getSample(iterNumber);
            currPred = NN.predict(currSample);
            NN.learn(currSample);
        }
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

    private void loadPreInstalledSamples() {
        // 75000 Samples
        Stream<Path> paths;
        try {
            // Recursive walk around sample directory
            paths = Files.walk(Paths.get("./HN/CompleteImages/All data (Compressed)"));
        } catch (IOException ioE) {
            ioE.printStackTrace();
            return;
        }
        List<File> files = new java.util.ArrayList<>(
                paths
                // Gets rid of directories
                .filter(Files::isRegularFile)
                // Converts files to absolute paths
                .map(Path::toAbsolutePath)
                // Path -> File
                .map(Path::toFile)
                .toList());

        // So it can sample in a random order
        Collections.shuffle(files);

        paths.close();

        files.forEach(f -> {
            SampleImage sImg = new SampleImage(f);
            // file[0] (first char) will be the label
            Sample s = new Sample(sImg, Integer.parseInt(f.getName().substring(0, 1)));
            NN.addSample(s);
            NN.addSample(s);
            totalSamplesLabel.setText(String.format("%d", NN.getSampleAmount()));
        });
    }
}
