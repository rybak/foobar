package regex;

import java.util.regex.Pattern;

public class LineFeedMatching {
	public static void main(String... args) {
		String text = "aaa\nccc";
		char c = '\u00a0';
		System.out.println("int(char) = " + ((int) c));
		System.out.println("int(\\n) = " + ((int) '\n'));
		Pattern pattern = Pattern.compile("\u00a0");
		Pattern pattern2 = Pattern.compile("\n", Pattern.LITERAL);
		test(text, pattern);
		test(text, pattern2);
		test("aaa\u00a0ccc", pattern);
		test("aaa\u00a0ccc", pattern2);

	}

	private static void test(String text, Pattern pattern) {
		System.out.println(pattern.matcher(text).replaceAll("bbb"));
	}
}
