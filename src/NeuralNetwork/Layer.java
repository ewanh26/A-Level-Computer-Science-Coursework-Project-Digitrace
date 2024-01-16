package NeuralNetwork;

import LinearAlgebra.Matrix;
import LinearAlgebra.Vector;
import Utils.Functions;
import Utils.Utils;

import java.util.Arrays;

public class Layer extends Vector {
    private Matrix weights;
    private Vector biases;
    private LayerType type;

    public Layer(LayerType type) {
        super(
                0,
                type == LayerType.INPUT ? Utils.INPUT_LAYER_SIZE :
                type == LayerType.HIDDEN || type == LayerType.PRE_OUTPUT ? Utils.HIDDEN_LAYER_SIZE :
                type == LayerType.OUTPUT ? Utils.OUTPUT_LAYER_SIZE : 0
                );
        weights =
                // Weight matrix dimensions in form LayerType x LayerType
                // (Where the type indicates the size of that layer type)
                type == LayerType.INPUT
                // INPUT : hidden x input
                ? new Matrix(0, Utils.HIDDEN_LAYER_SIZE, Utils.INPUT_LAYER_SIZE) :
                type == LayerType.HIDDEN
                // HIDDEN : hidden x hidden
                ? new Matrix(0, Utils.HIDDEN_LAYER_SIZE, Utils.HIDDEN_LAYER_SIZE) :
                type == LayerType.PRE_OUTPUT
                // PRE-OUTPUT : output x hidden
                ? new Matrix(0, Utils.OUTPUT_LAYER_SIZE, Utils.HIDDEN_LAYER_SIZE)
                // OUTPUT : null (needs no weights)
                : null;
        biases =
                // Bias vector has the dimensions of the NEXT layer
                type == LayerType.INPUT ? new Vector(0, Utils.HIDDEN_LAYER_SIZE) :
                type == LayerType.HIDDEN ? new Vector(0 , Utils.HIDDEN_LAYER_SIZE) :
                type == LayerType.PRE_OUTPUT ? new Vector(0, Utils.OUTPUT_LAYER_SIZE) : null;

        this.type = type;
    }

    public void setLayer(double[] array) {
        if (array.length == rows) {
            for (int i = 0; i < rows; ++i) {
                setElement(i, 0, array[i]);
            }
        } else {
            throw new IndexOutOfBoundsException("Attempted to set layer to mismatched array");
        }
    }

    public void activate() {
        Matrix result = apply(Functions::sigmoid);
        double[] elems = new double[rows];
        // Resets the layer directly from matrix
        // (Explicit cast didn't work)
        for (int i = 0; i < rows; ++i) {
            elems[i] = result.getElement(i, 0);
        }
        setLayer(elems);
    }

    public Matrix getWeights() {
        return weights;
    }

    public void alterWeight(int row, int col, double value) {
        weights.setElement(row, col, weights.getElement(row, col) + value);
    }

    public Vector getBiases() {
        return biases;
    }

    public void alterBias(int row, double value) {
        biases.setElement(row, biases.getElement(row) + value);
    }

    public LayerType getType() {
        return type;
    }
}
