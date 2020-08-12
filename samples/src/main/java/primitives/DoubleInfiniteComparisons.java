package primitives;

public class DoubleInfiniteComparisons {
    public static void main(String[] args) {
        System.out.println(Double.POSITIVE_INFINITY > 0.0);
        System.out.println(Double.NEGATIVE_INFINITY > 0.0);
        System.out.println(Double.NEGATIVE_INFINITY < Double.parseDouble("-2000.0"));
    }
}
