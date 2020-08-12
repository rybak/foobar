package puzzlers;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class StringConcatStream {
	public static void main(String[] args) {
		Collection<String> c = Arrays.asList("1", "2", "3", "4", "5");
		String result = c.stream().collect(() -> "", String::concat, String::concat);
		System.out.println(result);
		System.out.println(String.join("", c));
	}
}
