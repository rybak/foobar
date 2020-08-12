package puzzlers;

/**
 * @author Andrei Rybak
 */
public class LinePrinter {
	public static void main(String[] args) {
//		System.out.println(String.format("%s", null));
//		method("111", null);
//		method("111", null, "xxx");
		method("111", null);
	}

	public static void method(String single, Object... lots) {
		System.out.println(lots);
		System.out.println(lots.length);
	}
}
