package dev.ua.ikeepcalm.solution;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class Polyphase {
    private final String inputFile;
    private final String firstAuxiliaryFile;
    private final String secondAuxiliaryFile;
    private final String thirdAuxiliaryFile;
    private final int availableMemory;

    public Polyphase(String inputFile, String firstAuxiliaryFile, String secondAuxiliaryFile, String thirdAuxiliaryFile, int availableMemory) {
        this.inputFile = inputFile;
        this.firstAuxiliaryFile = firstAuxiliaryFile;
        this.secondAuxiliaryFile = secondAuxiliaryFile;
        this.thirdAuxiliaryFile = thirdAuxiliaryFile;
        this.availableMemory = availableMemory;
    }

    public void sort() throws IOException {
        List<File> sortedChunks = splitAndSortChunks();
        distributeChunks(sortedChunks);
        performPolyphaseMerge();
    }

    private List<File> splitAndSortChunks() throws IOException {
        List<File> sortedChunks = new ArrayList<>();
        List<Integer> buffer = new ArrayList<>();
        int bufferSize = availableMemory / Integer.BYTES;
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.add(Integer.parseInt(line));
                if (buffer.size() == bufferSize) {
                    System.out.println("Sorting chunk...");
                    sortedChunks.add(sortAndWriteChunk(buffer));
                    buffer.clear();
                }
            }
            if (!buffer.isEmpty()) {
                sortedChunks.add(sortAndWriteChunk(buffer));
            }
        }

        return sortedChunks;
    }

    private File sortAndWriteChunk(List<Integer> buffer) throws IOException {
        Collections.sort(buffer);

        File tempDir = new File("temp");
        if (!tempDir.exists()) {
            tempDir.mkdir();
        }


        File chunkFile = File.createTempFile("chunk", ".txt", tempDir);
        chunkFile.deleteOnExit();

        System.out.println("Writing chunk to file: " + chunkFile.getAbsolutePath());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(chunkFile))) {
            for (int num : buffer) {
                writer.write(Integer.toString(num));
                writer.newLine();
            }
        }

        return chunkFile;
    }

    private void distributeChunks(List<File> sortedChunks) throws IOException {
        List<File> firstFileChunks = new ArrayList<>();
        List<File> secondFileChunks = new ArrayList<>();
        List<File> thirdFileChunks = new ArrayList<>();

        int file1Chunks = Math.max(1, sortedChunks.size() / 3);
        int file2Chunks = Math.max(1, sortedChunks.size() / 3);
        int file3Chunks = sortedChunks.size() - file1Chunks - file2Chunks;

        for (int i = 0; i < file1Chunks && !sortedChunks.isEmpty(); i++) {
            firstFileChunks.add(sortedChunks.removeFirst());
        }
        for (int i = 0; i < file2Chunks && !sortedChunks.isEmpty(); i++) {
            secondFileChunks.add(sortedChunks.removeFirst());
        }
        for (int i = 0; i < file3Chunks && !sortedChunks.isEmpty(); i++) {
            thirdFileChunks.add(sortedChunks.removeFirst());
        }

        writeChunksToAuxFile(firstFileChunks, firstAuxiliaryFile);
        writeChunksToAuxFile(secondFileChunks, secondAuxiliaryFile);
        writeChunksToAuxFile(thirdFileChunks, thirdAuxiliaryFile);
    }

    private void writeChunksToAuxFile(List<File> chunks, String auxFile) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(auxFile))) {
            for (File chunk : chunks) {
                try (BufferedReader reader = new BufferedReader(new FileReader(chunk))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        writer.write(line);
                        writer.newLine();
                    }
                }
            }
        }
    }

    private void performPolyphaseMerge() throws IOException {
        File file1 = new File(firstAuxiliaryFile);
        File file2 = new File(secondAuxiliaryFile);
        File file3 = new File(thirdAuxiliaryFile);

        boolean moreMergesNeeded = true;
        while (moreMergesNeeded) {
            System.out.println("Merging files...");
            moreMergesNeeded = mergeFiles(file1, file2, file3);
            if (moreMergesNeeded) {
                File temp = file1;
                file1 = file3;
                file3 = file2;
                file2 = temp;
            }
        }

        file1.renameTo(new File("sorted.txt"));

        System.out.println("Sort completed successfully.");
        System.out.println("Sorted file: " + new File("sorted.txt").getAbsolutePath());
        file2.delete();
        file3.delete();
    }

    private boolean mergeFiles(File inputFile1, File inputFile2, File outputFile) throws IOException {
        PriorityQueue<Entry> queue = new PriorityQueue<>();
        try (
                BufferedReader reader1 = new BufferedReader(new FileReader(inputFile1));
                BufferedReader reader2 = new BufferedReader(new FileReader(inputFile2));
                BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))
        ) {
            String line1 = reader1.readLine();
            String line2 = reader2.readLine();

            if (line1 != null) {
                queue.add(new Entry(Integer.parseInt(line1), 1));
            }
            if (line2 != null) {
                queue.add(new Entry(Integer.parseInt(line2), 2));
            }

            while (!queue.isEmpty()) {
                Entry smallest = queue.poll();
                writer.write(Integer.toString(smallest.value));
                writer.newLine();
                if (smallest.fileIndex == 1) {
                    line1 = reader1.readLine();
                    if (line1 != null) {
                        queue.add(new Entry(Integer.parseInt(line1), 1));
                    }
                } else {
                    line2 = reader2.readLine();
                    if (line2 != null) {
                        queue.add(new Entry(Integer.parseInt(line2), 2));
                    }
                }
            }
        }

        return !isSingleSeries(outputFile);
    }

    private boolean isSingleSeries(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String previousLine = reader.readLine();
            if (previousLine == null) {
                return true;
            }
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                int previousValue = Integer.parseInt(previousLine);
                int currentValue = Integer.parseInt(currentLine);
                if (currentValue < previousValue) {
                    return false;
                }
                previousLine = currentLine;
            }
        }
        return true;
    }

    private static class Entry implements Comparable<Entry> {
        int value;
        int fileIndex;

        public Entry(int value, int fileIndex) {
            this.value = value;
            this.fileIndex = fileIndex;
        }

        @Override
        public int compareTo(Entry other) {
            return Integer.compare(other.value, this.value);
        }
    }
}
