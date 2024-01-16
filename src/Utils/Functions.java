package Utils;

public class Functions {
    public static double sigmoid(double z) {
        return 1 / (1 + Math.exp(-z));
    }
    public static double sigmoidDiff(double z) {
        double expTerm = Math.exp(-z);
        return expTerm / Math.pow(1 + expTerm, 2);
    }
    public static double sigmoidInv(double z) {
        return Math.log(z / (1 - z));
    }
}
