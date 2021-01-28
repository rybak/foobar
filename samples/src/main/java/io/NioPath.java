package io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class NioPath {
    public static void main(String[] args) {
        Path simple = Paths.get("foobar.iml");
        test(simple);
        Path complex = Paths.get("", "foobar.iml");
        test(complex);
    }

    private static void test(Path p) {
        try {
            System.out.println(p);
            System.out.println(Files.readAllLines(p).size());
            System.out.println(p.getParent());
            System.out.println(p.toAbsolutePath());
            System.out.println(p.toAbsolutePath().getParent());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
