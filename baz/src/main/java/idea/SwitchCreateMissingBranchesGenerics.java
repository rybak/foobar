package idea;

import java.util.List;

public class SwitchCreateMissingBranchesGenerics {
	public static void main(String[] args) {
		List<Example<String, Integer>> examples = List.of(
				new Foo<String, Integer, Integer>("hello", 10),
				new Bar<>(),
				new Foo<String, Integer, String>("world", "different C type"),
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
			 * 		String res = switch (example) {
			 * 			case Foo foo -> null;
			 * 			case Bar bar -> null;
			 * 		};
			 */

			/*
			 * Expected result with type parameters:
			 * 		String res = switch (example) {
			 * 			case Foo<String, Integer, ?> foo -> null;
			 * 			case Bar<Integer> bar -> null;
			 * 		};
			 */

			// which after replacing the `null`s turns into:
			String res = switch (example) {
				case Foo<String, Integer, ?> foo -> "Foo of " + foo.a() + " and " + foo.c();
				case Bar<Integer> bar -> "a Bar: " + bar;
			};
			System.out.println(res);
		}
	}

	sealed interface Example<A, B> permits Foo, Bar {
	}

	record Foo<A, B, C>(A a, C c) implements Example<A, B> {
	}

	static final class Bar<B> implements Example<String, B> {
	}
}
