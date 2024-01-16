package NeuralNetwork;

public class NNPrediction {
    private int result;
    private double cost;

    public NNPrediction(int result, double cost) {
        this.result = result;
        this.cost = cost;
    }

    public int getResult() {
        return result;
    }

    public double getCost() {
        return cost;
    }
}

