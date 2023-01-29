package idea;

import java.util.function.Function;

class IncompatibleEqualityConstraintDemo {
	static class Foo<A, B> {
		<R> void foo(Function<A, R> a, Function<B, R> b) {
		}
	}

	static class FooCorrected<A, B> {
		<R> void foo(Function<? super A, R> a, Function<? super B, R> b) {
		}
	}

	static void bar() {
		Foo<String, Integer> fooStringInteger = new Foo<>();
		FooCorrected<String, Integer> fooCorrected = new FooCorrected<>();
		{
			Function<CharSequence, String> badFunctionA = cs -> "badFunctionA" + cs.toString();

			// doesn't compile because CharSequence != String
			fooStringInteger.foo(badFunctionA, i -> "Good function" + i.toString());

			// compiles because CharSequence satisfies constraint `? super String`
			fooCorrected.foo(badFunctionA, i -> "Good function" + i.toString());
		}
		{
			// Integer extends Number
			Function<Number, String> badFunctionB = n -> n.toString() + "g";

			// doesn't compile because Number != Integer
			fooStringInteger.foo(s -> "Good function" + s, badFunctionB);

			// compiles because Number satisfies constraint `? super Integer`
			fooCorrected.foo(s -> "Good function" + s, badFunctionB);
		}
	}
}
