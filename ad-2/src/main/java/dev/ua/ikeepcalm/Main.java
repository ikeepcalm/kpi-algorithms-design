package dev.ua.ikeepcalm;

import dev.ua.ikeepcalm.solution.EightQueens;
import dev.ua.ikeepcalm.solution.algorithms.AStar;
import dev.ua.ikeepcalm.solution.algorithms.LDFS;

public class Main {

    private static String ANSI_GREEN = "\u001B[32m";
    private static String ANSI_RESET = "\u001B[0m";
    private static String ANSI_RED = "\u001B[31m";

    public static void main(String[] args) throws InterruptedException {
        System.setProperty("sun.jnu.encoding", "UTF-8");

        if (args.length == 0) {
            System.out.println(ANSI_RED + "Please specify the algorithm to use: LDFS or AStar");
            return;
        }

        clearConsole();

        String algorithm = args[0].toUpperCase();
        boolean verbose = Boolean.parseBoolean(args.length > 1 ? args[1] : "false");
        EightQueens eightQueens = new EightQueens();
        eightQueens.printBoard();

        System.out.println("\n\n The original board is shown above. \n\n");

        Thread.sleep(2000);

        switch (algorithm) {
            case "LDFS":
                System.out.println("\n\n");
                System.out.println("Solving the problem using LDFS algorithm...");
                System.out.println("\n\n");

                if (LDFS.solveLDFS(eightQueens.getBoard(), 0, 8, verbose)) {
                    System.out.println(ANSI_GREEN);
                    System.out.println("----------------");
                    System.out.println("| S O L V E D! |");
                    System.out.println("----------------");
                    System.out.println(ANSI_RESET + "\n");
                    eightQueens.printBoard();
                    System.out.println("\nIterations: " + LDFS.iterations);
//                    System.out.println("Total nodes: " + LDFS.totalNodes);
//                    System.out.println("Max nodes in memory: " + LDFS.maxNodesInMemory);
                } else {
                    System.out.println(ANSI_RED);
                    System.out.println("----------------");
                    System.out.println("|<!> ERROR! <!> |");
                    System.out.println("----------------");
                }
                break;

            case "ASTAR":
                System.out.println("\n\n");
                System.out.println("Solving the problem using A* algorithm...");
                System.out.println("\n\n");


                if (AStar.solveAStar(eightQueens.getBoard(), verbose)) {
                    System.out.printf(ANSI_GREEN);
                    System.out.println("----------------");
                    System.out.println("| S O L V E D! |");
                    System.out.println("----------------");
                    System.out.println(ANSI_RESET + "\n");
                    eightQueens.printBoard();
                    System.out.println("\nIterations: " + AStar.iterations);
//                    System.out.println("Total nodes: " + AStar.totalNodes);
//                    System.out.println("Max nodes in memory: " + AStar.maxNodesInMemory);
                } else {
                    System.out.println(ANSI_RED);
                    System.out.println("----------------");
                    System.out.println("|<!> ERROR! <!> |");
                    System.out.println("----------------");
                }
                break;

            default:
                System.out.println("Unknown algorithm specified. Please use LDFS or AStar.");
                break;
        }

        System.out.println(ANSI_RESET);
    }

    private static void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}