package dev.ua.ikeepcalm;

import dev.ua.ikeepcalm.solution.ACO;
import dev.ua.ikeepcalm.solution.TSP;

import java.util.Optional;

public class Main {

    private static Optional<Integer> parseArgument(String[] args, int index) {
        if (args.length > index) {
            try {
                return Optional.of(Integer.parseInt(args[index]));
            } catch (NumberFormatException e) {
                System.err.println("Invalid number format for argument at index " + index);
            }
        }
        return Optional.empty();
    }

    private static Optional<Boolean> parseBooleanArgument(String[] args, int index) {
        if (args.length > index) {
            return Optional.of(Boolean.parseBoolean(args[index]));
        }
        return Optional.empty();
    }

    public static void main(String[] args) {

        if (args.length < 1) {
            System.err.println("Usage: java -jar aco.jar <iterations> <quality> <showDistances> <verbose>");
            System.exit(1);
        }

        int iterations = parseArgument(args, 0).orElse(1000);
        int quality = parseArgument(args, 1).orElse(20);
        boolean showDistances = parseBooleanArgument(args, 2).orElse(false);
        boolean verbose = parseBooleanArgument(args, 3).orElse(false);

        TSP generator = new TSP();
        int[][] distances = generator.getDistances();
        if (showDistances) {
            printDistances(distances);
        }

        for (int i = 0; i < 3; i++) {
            ACO aco = new ACO(distances, iterations, quality, verbose);
            aco.solve();
            iterations += iterations / 2;
            quality += quality / 2;
            System.out.println("\n\n");
        }

    }

    private static void printDistances(int[][] distances) {
        for (int[] row : distances) {
            for (int distance : row) {
                System.out.print(distance + " ");
            }
            System.out.println();
        }
    }
}