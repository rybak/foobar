package java_samples.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EndOfStringParsing {
	public static void main(String... args) {
		String s = "AAABBB";
		System.out.println(s);

		Pattern regex = Pattern.compile("[a-z]*");
		Matcher m = regex.matcher(s);
		System.out.println(m.find(6));
	}
}
