package primitives;

public class DoubleNanPuzzler {
	public static void main(String[] args) {
		double x = Double.NaN;
		System.out.println(x == Double.NaN);
		System.out.println(Double.NaN == x);
		System.out.println(Double.isNaN(x));
		System.out.println(Double.NaN == Double.NaN);
		System.out.println(0.0 / 0.0);

		System.out.println("---------------");
		/* != always returns true for NaN
		 * This is done so that "a != b" is always equivalent to "!(a == b)".
		 * And == always returns false for NaN */
		System.out.println(Double.NaN != 1.0);
	}
}
