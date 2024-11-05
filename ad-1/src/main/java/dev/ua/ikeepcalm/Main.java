package dev.ua.ikeepcalm;

import dev.ua.ikeepcalm.solution.Generator;
import dev.ua.ikeepcalm.solution.Polyphase;

import java.io.IOException;

public class Main {

    public static void printVersion() {
        System.out.println("External Sort Version 1.0");
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: <command> [options]");
            return;
        }

        String command = args[0];

        switch (command) {
            case "version":
                printVersion();
                break;
            case "generate":
                if (args.length != 3) {
                    System.out.println("Usage: generate <filename> <size_in_mb>");
                    return;
                }
                String filename = args[1];
                int sizeInMB = Integer.parseInt(args[2]);
                try {
                    Generator.generateTestFile(filename, sizeInMB);
                } catch (IOException e) {
                    System.out.println("Error generating file: " + e.getMessage());
                }
                break;
            case "sort":
                if (args.length < 3) {
                    System.out.println("Usage: <inputFile> <memoryInMB>");
                    System.exit(1);
                }

                String inputFile = args[1];
                int memoryInMB = Integer.parseInt(args[2]);

                int availableMemory = memoryInMB * 1024 * 1024;

                long startTime = System.currentTimeMillis();

                Polyphase sorter = new Polyphase(inputFile, "tempOne.txt", "tempTwo.txt", "tempThree.txt", availableMemory);
                try {
                    sorter.sort();
                } catch (IOException e) {
                    System.out.println("Error sorting file: " + e.getMessage());
                }

                long endTime = System.currentTimeMillis();

                System.out.println("Sorting took " + (endTime - startTime) + " ms");

                break;
            default:
                System.out.println("Unknown command: " + command);
        }
    }
}

