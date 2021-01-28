package regex;

import java.util.regex.Pattern;

public class EscapedQuoteParser {
	private static final Pattern P = Pattern.compile("\"([^\"]|\\\\\")*\"");

	public static void main(String... args) {
		testParses("\"\"");
		testParses("\"aaaa\"");
		testParses("\"aa\\\"bb\"");
		testParses("\"cc\\\"dd\\\"ee\"");

		testDoesNotParse("\"");
		testDoesNotParse("\"aaaa");
		testDoesNotParse("\"bb\\\"cc");
		testDoesNotParse("\"dd\\\"ee\\\"ff");
	}

	private static void testDoesNotParse(String s) {
		test(s, "Bad: ", "Good: ");
	}

	private static void testParses(String s) {
		test(s, "Good: ", "Bad: ");
	}

	private static void test(String s, String parsed, String notParsed) {
		if (P.matcher(s).matches()) {
			System.out.println(parsed + s);
		} else {
			System.out.println(notParsed + s);
		}
	}
}
