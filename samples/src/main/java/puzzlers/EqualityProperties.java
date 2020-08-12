package puzzlers;

/**
 * @author Andrei Rybak
 */
public class EqualityProperties {
	public static boolean isOdd(int i) {
		return i % 2 == 1;
	}

	public static void main(String[] args) {
		double x = 1.0;
		char y = 0;
		System.out.println(x == y);
		System.out.println(y == x);
		double z = Double.NaN;
		System.out.println(z == z);

		for (int i = -3; i < 3; i++) {
			System.out.println(i + " " + isOdd(i) + "    " + (i % 2));
		}
	}
}
