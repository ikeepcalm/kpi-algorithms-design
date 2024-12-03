package dev.ua.ikeepcalm;

import dev.ua.ikeepcalm.solution.ACO;
import dev.ua.ikeepcalm.solution.TSP;

import java.util.List;

public class Main {

    private final static String RESET = "\u001B[0m";
    private final static String GREEN = "\u001B[32;1m";
    private final static String BACKGROUND_GREEN = "\u001B[42m";
    private final static String BRIGHT_BACKGROUND_CYAN = "\u001B[46;1m";
    private final static String BRIGHT_BACKGROUND_RED = "\u001B[41;1m";

    private static int parseTimeLimit(String timeLimitStr) {
        int timeLimit = 0;
        try {
            if (timeLimitStr.endsWith("ms")) {
                timeLimit = Integer.parseInt(timeLimitStr.replace("ms", ""));
            } else if (timeLimitStr.endsWith("s")) {
                timeLimit = Integer.parseInt(timeLimitStr.replace("s", "")) * 1000;
            } else if (timeLimitStr.endsWith("m")) {
                timeLimit = Integer.parseInt(timeLimitStr.replace("m", "")) * 1000 * 60;
            } else if (timeLimitStr.endsWith("h")) {
                timeLimit = Integer.parseInt(timeLimitStr.replace("h", "")) * 1000 * 60 * 60;
            } else {
                throw new IllegalArgumentException("Invalid time limit format");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid time limit format", e);
        }
        return timeLimit;
    }

    public static void main(String[] args) {
        boolean verbose = false;
        boolean deterministic = false;

        if (args.length == 0) {
            System.out.println("Usage: java -jar aco.jar <verbose> <deterministic> <timeLimit>");
            System.exit(1);
        }

        try {
            if (args.length > 0) {
                verbose = Boolean.parseBoolean(args[0]);
            }
        } catch (Exception e) {
            System.out.println("Invalid argument. Please provide a boolean value for verbose mode.");
            System.out.println("Usage: java -jar aco.jar <verbose>");
            System.exit(1);
        }

        try {
            if (args.length > 1) {
                deterministic = Boolean.parseBoolean(args[1]);
            }
        } catch (Exception e) {
            System.out.println("Invalid argument. Please provide a boolean value for deterministic mode.");
            System.out.println("Usage: java -jar aco.jar <verbose> <deterministic>");
            System.exit(1);
        }

        long timeLimit;
        try {
            if (args.length > 1) {
                timeLimit = parseTimeLimit(args[2]);
            } else {
                timeLimit = 60000;
            }
        } catch (Exception e) {
            System.out.println("Invalid argument. Please provide a valid time limit.");
            System.out.println("Usage: java -jar aco.jar <verbose> <deterministic> <timeLimit>");
            System.exit(1);
            return;
        }

        int numCities = 300;

        int[] numAntsArray = {10, 30, 50, 70, 100};
        int[] maxIterationsArray = {50, 100, 150};
        double[] alphaArray = {1.0, 1.5, 2.5, 4.5};
        double[] betaArray = {3.0, 5.0, 1.8, 2.0};
        double[] rhoArray = {0.2, 0.45, 0.5, 0.6};
        double[] qArray = {25, 100, 250, 300, 500};
        int[] elitistFactorArray = {0, 1, 3, 5, 10};

        TSP tsp = new TSP(numCities);

        double globalBestLength = Double.MAX_VALUE;
        List<Integer> globalBestTour = null;
        String bestParameterCombination = "";

        long startTime = System.currentTimeMillis();


        if (deterministic) {
            outerLoop:
            while (true) {
                boolean improved = false;

                for (int numAnts : numAntsArray) {
                    if (System.currentTimeMillis() - startTime > timeLimit) break outerLoop;

                    for (int maxIterations : maxIterationsArray) {
                        if (System.currentTimeMillis() - startTime > timeLimit) break outerLoop;

                        for (double alpha : alphaArray) {
                            if (System.currentTimeMillis() - startTime > timeLimit) break outerLoop;

                            for (double beta : betaArray) {
                                if (System.currentTimeMillis() - startTime > timeLimit) break outerLoop;

                                for (double rho : rhoArray) {
                                    if (System.currentTimeMillis() - startTime > timeLimit) break outerLoop;

                                    for (double Q : qArray) {
                                        if (System.currentTimeMillis() - startTime > timeLimit) break outerLoop;

                                        for (int elitistFactor : elitistFactorArray) {
                                            if (System.currentTimeMillis() - startTime > timeLimit) break outerLoop;

                                            ACO aco = new ACO(numCities, numAnts, maxIterations, alpha, beta, rho, Q, elitistFactor, tsp, verbose);

                                            aco.solve();

                                            if (aco.bestLength < globalBestLength) {
                                                globalBestLength = aco.bestLength;
                                                globalBestTour = aco.bestTour;
                                                bestParameterCombination = String.format(
                                                        "Ants=%d, Iterations=%d, Alpha=%.1f, Beta=%.1f, Rho=%.1f, Q=%.1f, Elitists=%d",
                                                        numAnts, maxIterations, alpha, beta, rho, Q, elitistFactor
                                                );

                                                improved = true;

                                                numAntsArray = new int[]{numAnts};
                                                maxIterationsArray = new int[]{maxIterations};
                                                alphaArray = new double[]{alpha};
                                                betaArray = new double[]{beta};
                                                rhoArray = new double[]{rho};
                                                qArray = new double[]{Q};
                                                elitistFactorArray = new int[]{elitistFactor};

                                                System.out.println(BRIGHT_BACKGROUND_CYAN + "Updated Parameters: " + bestParameterCombination + RESET + "\n");
                                                System.out.println("Best Length: " + globalBestLength + RESET + "\n");
                                            } else {
                                                System.out.printf("Params: Ants=%d, Iterations=%d, Alpha=%.1f, Beta=%.1f, Rho=%.1f, Q=%.1f, Elitists=%d => Best Length=%.2f%n \n", numAnts, maxIterations, alpha, beta, rho, Q, elitistFactor, aco.bestLength);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (!improved) {
                    break;
                }
            }

            System.out.println(GREEN + "Best Overall Length: " + globalBestLength + RESET + "\n");
            if (verbose) {
                System.out.println("Best Tour: " + globalBestTour);
            }
            System.out.println(GREEN + "Best Parameters: " + bestParameterCombination + RESET);

        } else {
            outerLoop:
            for (int numAnts : numAntsArray) {
                for (int maxIterations : maxIterationsArray) {
                    for (double alpha : alphaArray) {
                        for (double beta : betaArray) {
                            for (double rho : rhoArray) {
                                for (double Q : qArray) {
                                    for (int elitistFactor : elitistFactorArray) {
                                        if (System.currentTimeMillis() - startTime > timeLimit) {
                                            break outerLoop;
                                        }

                                        ACO aco = new ACO(numCities, numAnts, maxIterations, alpha, beta, rho, Q, elitistFactor, tsp, verbose);


                                        aco.solve();

                                        if (aco.bestLength < globalBestLength) {
                                            globalBestLength = aco.bestLength;
                                            globalBestTour = aco.bestTour;
                                            bestParameterCombination = String.format("Ants=%d, Iterations=%d, Alpha=%.1f, Beta=%.1f, Rho=%.1f, Q=%.1f, Elitists=%d", numAnts, maxIterations, alpha, beta, rho, Q, elitistFactor);
                                            System.out.println(BRIGHT_BACKGROUND_CYAN + bestParameterCombination + "=> Best Length=" + aco.bestLength + RESET + "\n");
                                        } else {
                                            System.out.printf("Params: Ants=%d, Iterations=%d, Alpha=%.1f, Beta=%.1f, Rho=%.1f, Q=%.1f, Elitists=%d => Best Length=%.2f%n \n", numAnts, maxIterations, alpha, beta, rho, Q, elitistFactor, aco.bestLength);

                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            System.out.println(GREEN + "Best Overall Length: " + globalBestLength + RESET + "\n");
            if (verbose) {
                System.out.println("Best Tour: " + globalBestTour);
            }
            System.out.println(GREEN + "Best Parameters: " + bestParameterCombination + RESET);
        }
    }
}