package function;

import java.util.function.Function;

public abstract class Either<L, R, Z> {
	private Either() {
	}

	public abstract <T> T match(Function<L, T> a, Function<R, T> b, Function<Z, T> c);

	public static <L, R, Z> Left<L, R, Z> left(L v) {
		return new Left<>(v);
	}

	public static <L, R, Z> Right<L, R, Z> right(R v) {
		return new Right<>(v);
	}

	public static class Left<L, R, Z> extends Either<L, R, Z> {
		private final L left;

		private Left(L left) {
			this.left = left;
		}

		L getLeft() {
			return left;
		}

		@Override
		public <T> T match(Function<L, T> a, Function<R, T> b, Function<Z, T> c) {
			return a.apply(left);
		}
	}

	public static class Right<L, R, Z> extends Either<L, R, Z> {
		private final R right;

		private Right(R right) {
			this.right = right;
		}

		R getRight() {
			return right;
		}

		@Override
		public <T> T match(Function<L, T> a, Function<R, T> b, Function<Z, T> c) {
			return b.apply(right);
		}
	}

	public static class Zzzzzz<L, R, Z> extends Either<L, R, Z> {


		private final Z z;

		public Zzzzzz(Z z) {
			this.z = z;
		}

		Z getZ() {
			return z;
		}

		@Override
		public <T> T match(Function<L, T> a, Function<R, T> b, Function<Z, T> c) {
			return null;
		}
	}

	public static void main(String... args) {


	}

}
