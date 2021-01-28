package serialization;

import java.io.*;
import java.util.function.Function;

@SuppressWarnings("unchecked")
public class LambdaSerialization {
	public static void main(String... args) throws IOException, ClassNotFoundException {
		Function<Integer, Integer> doubling = i -> i * 2;
		System.out.println(doubling.apply(123));
		System.out.println();

		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		ObjectOutputStream output = new ObjectOutputStream(bytes);
		output.writeObject(doubling);
		output.close();

		ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()));
		Object obj = input.readObject();

		System.out.println(obj.getClass().getName());
		System.out.println(((Function<Integer, Integer>) obj).apply(123));

	}
}
