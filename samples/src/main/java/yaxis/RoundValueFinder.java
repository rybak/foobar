package yaxis;

public class RoundValueFinder {
    public static void main(String[] args) {
        System.out.println("copySpecialDependencyToscommonCore".hashCode());
        System.out.println("copySpecialDependencySourceJars".hashCode());
        test(1.0, 2.0);
        test(1.4, 2.0);
        test(1.5, 2.0);
        test(1.6, 2.0);
        test(1.232, 1.234);
        test(1.228, 1.234);
        test(-1.0, 1.0);
        test(1.0, 1.0);
        test(0.9, 1.0);
        test(-1.5, -1.0);
        test(-1.5, -0.5);
        test(-1.5, -0.1);
        test(-2.5, -2.4);
        test(-2.5, -2.45);
        for (double y = -10.1; y <= 10.1; y += 1.0) {
            test(y, y + 0.5);
        }
    }

    private static void test(double min, double max) {
        double roundValue = roundValueBetween(min, max);
        System.out.print(min <= roundValue && roundValue <= max ? "GOOD " : "!!!!!!! BAD ");
        System.out.println(roundValue);
    }

    private static double roundValueBetween(double min, double max) {
        if (min > max)
            throw new IllegalArgumentException();
        if (min == max)
            return min;
        int spanLog10 = (int) Math.log10(max - min);
        double roundTo = Math.pow(10.0, spanLog10 - 1);
        double avg = (min + max) / 2;
//        System.err.println("\t" + avg + "\t" + roundTo + "\t" + (avg % roundTo));
        return avg - avg % roundTo;
//        double minRounded = min - min % roundTo;
//        double maxRounded = max - max % roundTo;
//        System.err.println(roundTo + " " + minRounded + " " + maxRounded);
//        return (minRounded + maxRounded) / 2;
    }
}
