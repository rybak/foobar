package java_samples.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class FileStreamTest {
	public static void main(String[] args) {
		File file = new File("FileStreamTest.txt");
		if (file.exists()) {
			boolean delete = file.delete();
			if (!delete) {
				System.err.println("Could not delete the file.");
			}
		}
		try (FileOutputStream o = new FileOutputStream(file);
			 PrintStream printStream = new PrintStream(o)
		) {
			printStream.println("Hello, World!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
