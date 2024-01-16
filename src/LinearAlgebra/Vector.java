package LinearAlgebra;

import java.util.Arrays;

public class Vector extends Matrix {
    public Vector(double[] e, int rows) {
        super(e, rows, 1);
    }

    public Vector(double e, int rows) {
        super(e, rows, 1);
    }

    public double square_magnitude() {
        return Arrays.stream(as1DArray())
                .map(e -> Math.pow(e, 2))
                .sum();
    }

    public double magnitude() {
        return Math.sqrt(square_magnitude());
    }

    public double[] as1DArray() {
        double[] elems = new double[rows];
        for (int i = 0; i < rows; ++i) {
            elems[i] = elements[i][0];
        }

        return elems;
    }

    public double getElement(int row) {
        return super.getElement(row, 0);
    }

    public void setElement(int row, double element) {
        super.setElement(row, 0, element);
    }
}
