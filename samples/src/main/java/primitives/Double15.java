package primitives;

public class Double15 {
    public strictfp static void main(String[] args) {
        double d = Double.parseDouble("15.04");
        System.out.println(d);
        System.out.println(String.valueOf(d));
        System.out.println(Double.toString(d));
        System.out.println(String.format("%f", d));
        float f = Float.parseFloat("15.04");
        System.out.println(f);
        System.out.println(String.valueOf(f));
        System.out.println(Float.toString(f));
        System.out.println(String.format("%f", f));
        double hundred = 100;
        System.out.println(hundred);
        System.out.println(4.0 / 100);
        System.out.println(1504.0 / 100.0);
        System.out.println((1 * 1000.0 + 5.0 * 100 + 4.0) / 100);
        //
        System.out.println(15.04 / 100);
        System.out.println(15.04 / 100.0);
        System.out.println("=============================== y");
        double y = 15.04 / 10.0;
        System.out.println(y);
        y = y / 10.0;
        System.out.println(y);
        System.out.println(Double.parseDouble("0.1504"));

        System.out.println("=============================== z");
        double sqrt = Math.sqrt(100.0);
        double z = 15.04 / sqrt;
        System.out.println(z);
        System.out.println(z / sqrt);

        System.out.println((int) -1.3);
        System.out.println((int) 4.9);

        System.out.println(roundValueBetween(10.1241, 10.1344));
    }

    private static double roundValueBetween(double min, double max) {
        return min + Math.pow(10, (int)Math.log10(max - min));
    }
}
