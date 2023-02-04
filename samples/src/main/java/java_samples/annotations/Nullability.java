package java_samples.annotations;

import org.jetbrains.annotations.NotNull;

public class Nullability {
	public static void main(String... args) {
		methodName(null);
		methodName("String");
		methodName(123);
		methodName(456.0);
		methodName(789L);

	}

	static void methodName(@NotNull Object argument) {
		System.out.println("Value = " + argument);
	}
}
