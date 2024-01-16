package Utils;

import LinearAlgebra.Matrix;
import LinearAlgebra.Vector;

import javax.swing.*;
import java.util.Arrays;

public class Utils {
    public static final int INPUT_LAYER_SIZE = 784;
    public static final int HIDDEN_LAYER_SIZE = 16;
    public static final int OUTPUT_LAYER_SIZE = 10;

    // Prints matrix row-wise
    public static void printMatrix(Matrix m) {
        for (int i = 0; i < m.getRows(); ++i) {
            System.out.print("[ ");
            for (int j = 0; j < m.getCols(); ++j) {
                System.out.print(m.getElement(i, j) + " ");
            }
            System.out.print("]\n");
        }
    }

    public static Vector oneHotVector(int digit) {
        double[] elements = new double[10];
        // All elements 0,
        // except the one with the correct digit, which is 1
        elements[digit] = 1;
        return new Vector(elements, 10);
    }

    public static int[] tableToBinaryArray(JTable table) {
        int[] binaryArray = new int[784];
        for (int i = 0; i < 28; ++i) {
            for (int j = 0; j < 28; ++j) {
                binaryArray[i * 28 + j] = table.getValueAt(i, j) == null ? 0 : 1;
            }
        }
        return binaryArray;
    }
}
