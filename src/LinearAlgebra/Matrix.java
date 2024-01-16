package LinearAlgebra;

import java.util.Arrays;
import java.util.function.DoubleUnaryOperator;

public class Matrix {
    protected int rows, cols;
    protected double[][] elements;

    // Instantiate with 1D Array
    public Matrix(double[] e, int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.elements = new double[rows][cols];

        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < cols; ++j) {
                this.elements[i][j] = e[j + cols * i];
            }
        }
    }

    // Instantiate with 2D array
    public Matrix(double[][] e, int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.elements = e;
    }

    // Instantiate entire matrix with 1 double
    public Matrix(double e, int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.elements = new double[rows][cols];

        for (int i = 0; i < rows; ++i) {
            Arrays.fill(this.elements[i], e);
        }
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public double[][] asArray() {
        return elements;
    }

    public double getElement(int row, int col) {
        return elements[row][col];
    }

    public void setElement(int row, int col, double element) {
        elements[row][col] = element;
    }

    // Adds two matrices of the same dimension
    public Matrix add(Matrix m) {
        if (!(m.getRows() == rows && m.getCols() == cols)) {
            return new Matrix(-1, rows, cols);
        } else {
            double[][] res = new double[rows][cols];

            for (int i = 0; i < rows; ++i) {
                for (int j = 0; j < cols; ++j) {
                    // Adds corresponding elements
                    res[i][j] = elements[i][j] + m.getElement(i, j);
                }
            }

            return new Matrix(res, rows, cols);
        }
    }

    // Applies a function to each element of the array
    public Matrix apply(DoubleUnaryOperator f) {
        double[][] res = new double[rows][cols];

        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < cols; ++j) {
                res[i][j] = f.applyAsDouble(elements[i][j]);
            }
        }

        return new Matrix(res, rows, cols);
    }

    // A (m x n) . B (p x q) = C (m x q), given n == p
    public Matrix multiply(Matrix m) {
        if (cols != m.getRows()) return new Matrix(-1, rows, cols);
        else {
           double[][] res = new double[rows][m.getCols()];

           // Dot product of each of A's rows with each of B's columns
           for (int i = 0; i < rows; ++i) {
               for (int j = 0; j < m.getCols(); ++j) {
                   double productSum = 0;
                   for (int k = 0; k < cols; ++k) {
                       productSum += elements[i][k] * m.getElement(k, j);
                   }

                   res[i][j] = productSum;
               }
           }

           return new Matrix(res, rows, m.getCols());
        }
    }

    // Multiply by scalar
    public Matrix multiply(double n) {
        return apply(x -> (n * x));
    }
}
