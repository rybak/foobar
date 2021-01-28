package primitives;

import java.util.Collections;

public class Shifts {
	public static void main(String... args) {
		printBinary(1 << 28);
		printBinary(1 << 31);
		printBinary(1 << 32);
		printBinary(1 << 123);
		printBinary(1 << (123 + 32));
	}

	private static void printBinary(int i) {
		String x = Integer.toBinaryString(i);
		if (x.length() < 32) {
			String zerosPrefix = String.join("", Collections.nCopies(32 - x.length(), "0"));
			x = zerosPrefix + x;
		}
		System.out.println(x);
	}
}
