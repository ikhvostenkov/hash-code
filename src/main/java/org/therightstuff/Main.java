package org.therightstuff;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) {
        if (args.length < 1) {
            throw new IllegalArgumentException("No file is provided!");
        }

        Path path = Paths.get(args[0]);

        try {
            Files.lines(path)
                 .filter(line -> line.startsWith("1"))
                 .forEach(System.out::println);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
