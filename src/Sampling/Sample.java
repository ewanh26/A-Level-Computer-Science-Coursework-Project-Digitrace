package Sampling;

public class Sample {
    private int label;
    private int[] pixelArray;

    public Sample(SampleImage image, int label) {
        this.label = label;
        this.pixelArray = image.getBinaryArray();
    }

    public int getLabel() {
        return label;
    }

    public int[] getPixelArray() {
        return pixelArray;
    }
}
