package java_samples.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class FileTreesWalking {
    public static void main(String[] args) {
        try {
            String path = ".";
            test(path);
            test("src/yaxis/AFormatter.java");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void test(String path) throws IOException {
        List<Path> allFiles = Files.walk(Paths.get(path))
                .filter(Files::isRegularFile)
                .collect(Collectors.toList());
        System.out.println(allFiles);
        if (allFiles.size() == 1) {
            Path singleFile = allFiles.get(0);
            List<String> strings = Files.readAllLines(singleFile);
            System.out.println("\t" + strings.get(0));
        }
        System.out.println("=========================================================");
    }
}
