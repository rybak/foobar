package java_samples.records;

public class RecordExamples {
	static sealed class Sealed {
		static final class Nested extends Sealed {
		}
	}

	static abstract sealed class WithPermits permits WithPermits.Permitted1, WithPermits.Permitted2 {
		private non-sealed class Permitted1 extends WithPermits {
		}

		private final class Permitted2 extends WithPermits {
		}
	}

	public record EmptySameLine() {
	}

	public record Empty() {
	}

	public record EmptyBodyParameters<A, N extends Number>(A param1, N param2) {
	}

	public record EmptyBody(int param1, String param2) {
	}

	public record EmptyBody2(int param1, String param2) {
	}

	public record ExampleRecord(int field1, String field2) {
	}

	public class WithGenerics<A> {
		private A field;
	}

	non-sealed class NonSealed extends Sealed {
	}

	final class Final extends Sealed {
	}

	public sealed abstract class RIGHT {
		static int ONE;
		static int TWO;
		static int THREE;

		public final class ChangeMe extends RIGHT {
		}
	}

	public class WithGenericsExtends<N extends Number> {
		private N field;
	}

	private final class PermittedA extends RIGHT {
	}

	private final class PermittedB extends RIGHT {
	}
}

