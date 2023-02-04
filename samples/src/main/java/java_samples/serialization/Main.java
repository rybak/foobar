package java_samples.serialization;

import java.io.*;

public class Main {
	public static void main(String... args) throws IOException, ClassNotFoundException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream objectsWriter = new ObjectOutputStream(out);
		X a = new A(123);
		System.out.println(a.get());
		objectsWriter.writeObject(a);

		ByteArrayInputStream second = new ByteArrayInputStream(out.toByteArray());
		ObjectInputStream reader = new ObjectInputStream(second);
		X b = (X) reader.readObject();
		System.out.println(b.get());
	}

	static class HolderOfXs implements Serializable {

	}

	interface X extends Serializable {
		int get();
	}

	static class A implements X, Serializable {
		private final int x;

		A(int x) {
			this.x = x;
		}

		@Override
		public int get() {
			return x;
		}
	}
}
