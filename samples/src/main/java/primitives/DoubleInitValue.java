package primitives;

public class DoubleInitValue {
	double x;
	int i;

	public static void main(String... args) {
		DoubleInitValue test = new DoubleInitValue();
		System.out.println("Double: " + test.x);
		System.out.println("Int: " + test.i);
		double a[] = new double[10];
		for (double v : a) {
			System.out.println(v);
		}
	}
}
