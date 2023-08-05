package idea;

import java.util.List;

public class SwitchCreateMissingBranchesGenerics {
	public static void main(String[] args) {
		List<Example<String, Integer>> examples = List.of(
				new Foo<>("hello", 10),
				new Bar<>(),
				new Foo<>("world", 42),
				new Bar<>()
		);

		for (Example<String, Integer> example : examples) {
			/*
			 * 1. Starting point:
			 * 			String res = switch (example) {
			 * 			};
			 * 2. Wait a bit
			 * 3. Press "Alt+Enter"
			 * 4. Select "Create missing branches:..."
			 *
			 * 5. Actual result:
			 * 			String res = switch (example) {
			 * 				case Foo foo -> null;
			 * 				case Bar bar -> null;
			 * 			};
			 */

			/*
			 * Expected result with type parameters:
			 */
			String res = switch (example) {
				case Foo<String, Integer, ?> foo -> "Foo of " + foo.getA() + " and " + foo.getC();
				case Bar<Integer> bar -> "a Bar: " + bar;
			};
			System.out.println(res);
		}
	}

	sealed interface Example<A, B> permits Foo, Bar {
	}

	static final class Foo<A, B, C> implements Example<A, B> {
		private final A a;
		private final C c;

		Foo(A a, C c) {
			this.a = a;
			this.c = c;
		}

		A getA() {
			return a;
		}

		C getC() {
			return c;
		}
	}

	static final class Bar<B> implements Example<String, B> {
	}
}
