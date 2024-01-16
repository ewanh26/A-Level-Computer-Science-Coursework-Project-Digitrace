package Sampling;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class SampleImage {
    private BufferedImage image;
    private int[] binaryArray;

    public SampleImage(File file) {
        binaryArray = null;
        try {
            image = ImageIO.read(file);
        } catch (IOException ioE) {
            System.err.println(Arrays.toString(ioE.getStackTrace()));
        }
    }

    public SampleImage(int[] pixelArray) {
        image = null;
        if (pixelArray.length != 784) {
            throw new IndexOutOfBoundsException("Attempted to make sample of wrong size");
        }
        binaryArray = pixelArray;
    }

    public int[] getBinaryArray() {
        if (image != null) {
            binaryArray = new int[784];
            for (int i = 0; i < 28; ++i) {
                for (int j = 0; j < 28; ++j) {
                    binaryArray[j + 28 * i] = image.getRGB(j, i) == 0xFF000000 ? 1 : 0;
                }
            }
        }
        return binaryArray;
    }
}
