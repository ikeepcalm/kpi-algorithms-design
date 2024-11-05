package dev.ua.ikeepcalm.solution;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class Generator {

    public static void generateTestFile(String filename, int sizeInMB) throws IOException {
        File file = new File(filename);
        Random random = new Random();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            long sizeInBytes = sizeInMB * 1024L * 1024L;
            long writtenBytes = 0;

            while (writtenBytes < sizeInBytes) {
                int number = random.nextInt(1000000);
                writer.write(String.valueOf(number));
                writer.newLine();
                writtenBytes += String.valueOf(number).length() + 1;
            }
        }

        System.out.println("Test file " + filename + " generated successfully.");
    }
}
