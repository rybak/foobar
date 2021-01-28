package primitives;

public class BooleanCasting {
	public static void main(String... args) {
		boolean b = true;
		System.out.println(b);
		long t = System.currentTimeMillis();
		b = (Boolean) (t > 100L);
		System.out.println(b);
		Boolean bb = b;
		System.out.println(bb);
		bb = (t > 100L);
		System.out.println(bb);
	}
}
