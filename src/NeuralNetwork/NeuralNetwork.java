package NeuralNetwork;

import LinearAlgebra.Matrix;
import LinearAlgebra.Vector;
import Sampling.Sample;
import Utils.Utils;
import static Utils.Functions.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class NeuralNetwork {
    private Layer[] layers;
    private LinkedList<Sample> samples;
    private final double ALPHA = 0.2d;

    public NeuralNetwork() {
        layers = new Layer[5];
        layers[0] = new Layer(LayerType.INPUT);
        layers[1] = new Layer(LayerType.HIDDEN);
        layers[2] = new Layer(LayerType.HIDDEN);
        layers[3] = new Layer(LayerType.PRE_OUTPUT);
        layers[4] = new Layer(LayerType.OUTPUT);
        samples = new LinkedList<>();
        randomise();
    }

    private void randomise() {
        for (Layer layer : layers) {
            if (layer.getWeights() == null) return;
            for (int i = 0; i < layer.getWeights().getRows(); ++i) {
                double rand;
                for (int j = 0; j < layer.getWeights().getCols(); ++j) {
                    // Random value between -1 and 1
                    rand = Math.random() * 2 - 1;
                    layer.alterWeight(i, j, rand);
                }
                rand = Math.random() * 2 - 1;
                layer.alterBias(i, rand);
            }
        }
    }

    public NNPrediction predict(Sample sample) {
        Layer currentLayer;
        int currentLayerIdx;

        // Set input layer to input pixel array
        double[] pixelArray = Arrays.stream(sample.getPixelArray())
                        .asDoubleStream()
                        .toArray();
        layers[0].setLayer(pixelArray);
        // Current layer is input layer
        currentLayerIdx = 0;
        currentLayer = layers[currentLayerIdx];
        // Loop if current layer is not output layer
        Matrix resultMatrix;
        double[] resultElements;
        while (currentLayerIdx < 4) {
            Layer result = new Layer(layers[currentLayerIdx+1].getType());
            resultMatrix = currentLayer.getWeights();
            // Multiply weight matrix
            resultMatrix = resultMatrix.multiply(currentLayer)
                    // Add biases
                    .add(currentLayer.getBiases());
            // Matrix -> Layer
            resultElements = new double[resultMatrix.getRows()];
            for (int i = 0; i < resultMatrix.getRows(); ++i) {
                resultElements[i] = resultMatrix.getElement(i, 0);
            }

            // Set next layer to result
            layers[++currentLayerIdx].setLayer(resultElements);
            // Activate next layer
            layers[currentLayerIdx].activate();
            // Set current layer to next layer
            currentLayer = layers[currentLayerIdx];
        }

        // Current layer is now output layer
        int prediction_digit = 0;
        double cost;

        double greatestElement = 0;
        for (int i = 0; i < 10; ++i) {
            double element = currentLayer.getElement(i);
            // Finds the greatest element,
            // the index of which is the prediction
            if (element > greatestElement) {
                greatestElement = element;
                prediction_digit = i;
            }
        }

        // y - ȳ
        Matrix error_vector_m = currentLayer.add(Utils.oneHotVector(sample.getLabel()).multiply(-1));
        Vector error_vector = new Vector(0, error_vector_m.getRows());
        // Matrix -> Vector
        for (int i = 0; i < error_vector_m.getRows(); ++i) {
            error_vector.setElement(i, error_vector_m.getElement(i, 0));
        }
        // C = |y - ȳ|^2 / 10
        cost = error_vector.square_magnitude() / 10;

        return new NNPrediction(prediction_digit, cost);
    }

    public void learn(Sample sample) {
        final int n = 4;
        double dCdw, dCdb, productChain;
        int iBound, jBound;
        for (int k = 1; k <= 4; ++k) {
            dCdw = 1.0d;
            dCdb = 1.0d;
            iBound = layers[n-k].getRows();
            if (n-k-1 >= 0) {
                jBound = layers[n - k - 1].getRows();
            } else {
                jBound = Utils.INPUT_LAYER_SIZE;
            }
            for (int i = 0; i < iBound; ++i) {
                // MIN functions used to not let indexes go out of bounds
                // Ternary expression at the end equivalent to one-hot vector
                dCdb = layers[n].getElement(Math.min(layers[n].getRows()-1, i)) - (i == sample.getLabel() ? 1 : 0);
                dCdb *= 0.2d;
                productChain = 1;
                for (int m = 0; m <= k - 2; ++m) {
                    productChain *=
                            sigmoidDiff(sigmoidInv(layers[n-m].getElement(Math.min(layers[n-m].getRows()-1, i))))
                            * layers[n-m-1].getWeights().getElement(
                                    Math.min(layers[n-m-1].getWeights().getRows()-1, i),
                                    Math.min(layers[n-m-1].getWeights().getRows()-1, i)
                            );
                }
                dCdb *= productChain
                        * sigmoidDiff(sigmoidInv(layers[n-k+1].getElement(Math.min(layers[n-k+1].getRows()-1, i))));
                layers[n-k].alterBias(Math.min(layers[n-k].getBiases().getRows()-1, i), -ALPHA*dCdb);
                for (int j = 0; j < jBound; ++j) {
                    dCdw = dCdb * layers[n-k].getElement(Math.min(layers[n-k].getRows()-1, j));
                    layers[n-k].alterWeight(
                            Math.min(layers[n-k].getWeights().getRows()-1, i),
                            Math.min(layers[n-k].getWeights().getCols()-1, j),
                            -ALPHA*dCdw);
                }
            }
        }
    }

    public void addSample(Sample sample) {
        if (Arrays.stream(sample.getPixelArray()).allMatch(x -> x == 0)) {
            System.out.println("Empty Sample, not adding");
            return;
        }
        samples.add(sample);
    }

    public void removeSample(int idx) {
        samples.remove(idx);
    }

    public Sample getSample(int idx) {
        return samples.get(idx);
    }

    public int getSampleAmount() {
        return samples.size();
    }

    /*
    Abstractions of Layer / NeuralNetwork classes
    containing only weight and bias values.
     */
    private record LayerObject(double[][] weights, double[] biases) {}
    private record NetworkObject(LayerObject[] layers) {}

    public void storeAsJSON(String fileName) {
        File file;
        NetworkObject NNO;

        // Copy all weights and biases to layer objects
        LayerObject[] layerObjs = new LayerObject[4];
        for (int i = 0; i < 4; ++i) {

            layerObjs[i] = new LayerObject(
                    this.layers[i].getWeights().asArray(),
                    this.layers[i].getBiases().as1DArray()
            );
        }
        NNO = new NetworkObject(layerObjs);

        if (!fileName.endsWith(".json")) {
            fileName = fileName.concat(".json");
        }
        // Create JSON file
        file = new File("./Networks/" + fileName);

        // ObjectMapper can write Objs as JSON
        ObjectMapper mapper = new ObjectMapper();
        // String is an error message by default, which is reset if rewritten
        String json = "ERROR: COULD NOT WRITE JSON";
        try {
            // Neural Network Object as JSON
            json = mapper.writeValueAsString(NNO);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        try {
            // Write JSON to file
            FileWriter writer = new FileWriter(file);
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

