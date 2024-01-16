import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainForm extends JFrame {
    private JButton loadModelButton;
    private JButton trainNewModelButton;
    private JButton exitButton;
    private JPanel panel1;

    public MainForm() {
        setContentPane(panel1);
        setTitle("DigiTrace");
        setSize(720, 480);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);


        exitButton.addActionListener(e -> dispose());
        loadModelButton.addActionListener(e -> {
            PredictionModeForm predictionModeForm = new PredictionModeForm();
        });
        trainNewModelButton.addActionListener(e -> {
            LearningModeForm learningModeForm = new LearningModeForm();
        });
    }
}
