package puzzlers;

/**
 * @author Andrei Rybak
 */
public class Puzzlers {
	public static void main(String[] args) {
		System.out.println(Long.toHexString(0x100000000L + 0xcafebabe));
		System.out.println(Long.toHexString(0x100000000L + 0xcafebabeL));
		System.out.println(Long.toHexString(Long.MAX_VALUE));

		System.out.println((int) (char) (byte) (-1));
		System.out.println((char) (byte) (-1));
		System.out.println((byte) (-1));
		System.out.println(Short.MAX_VALUE);

		{
			int x = 1984;
			int y = 2018;
			x ^= (y ^= (x ^= y));
			System.out.println("x = " + x);
			System.out.println("y = " + y);
		}

		{
			char x = 'X';
			int i = 0;
			System.out.print(true ? x : 0);
			System.out.print(false ? i : x);
		}

	}
}

