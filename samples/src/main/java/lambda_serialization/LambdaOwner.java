package lambda_serialization;

import java.io.*;
import java.util.function.Supplier;

public class LambdaOwner implements Serializable {

	public static final String TEST_FILE = "lambdauser.obj";

	public static void main(String[] args) {
		Supplier<String> lambda = (Supplier<String> & Serializable) () -> "lambda string";
		test(lambda);
		LambdaOwner x = new LambdaOwner();
		Supplier<String> methodReference = (Supplier<String> & Serializable) (x::getString);
		test(methodReference);
		test(() -> "second lambda");
		LambdaOwner y = new LambdaOwner();
		test(y::getString);


	}

	private static void test(Supplier<String> supplier) {
		LambdaUser original = new LambdaUser(42, supplier);
		System.out.println("ORIGINAL" + original.getContent());
		try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(new File(TEST_FILE)))) {
			output.writeObject(original);
		} catch (IOException e) {
			e.printStackTrace();
		}

		LambdaUser deserialized = null;
		try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(new File(TEST_FILE)))) {
			deserialized = (LambdaUser) input.readObject();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		if (deserialized != null)
			System.out.println(deserialized.getContent());
		else
			System.out.println("HAHAHA xxxxx");
	}

	public String getString() {
		return "string from class" + Integer.toHexString(hashCode());
	}
}
