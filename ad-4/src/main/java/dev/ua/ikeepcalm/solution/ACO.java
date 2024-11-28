package dev.ua.ikeepcalm.solution;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class ACO {
    private static int ITERATIONS;
    private static int QUALITY;
    private static boolean VERBOSE = false;
    private static final int NUM_ANTS = 45;
    private static final double ALPHA = 3.0;
    private static final double BETA = 2.0;
    private static final double EVAPORATION_RATE = 0.3;
    private final double[][] pheromones;
    private final int[][] distances;
    private final int vertices;
    private final List<Ant> ants;
    private final Random random;

    private final String RESET = "\u001B[0m";
    private final String GREEN = "\u001B[32;1m";
    private final String BACKGROUND_GREEN = "\u001B[42m";
    private final String BRIGHT_BACKGROUND_CYAN = "\u001B[46;1m";
    private final String BRIGHT_BACKGROUND_RED = "\u001B[41;1m";

    public ACO(int[][] distances, int iterations, int quality, boolean verbose) {
        this.distances = distances;
        ITERATIONS = iterations;
        QUALITY = quality;
        VERBOSE = verbose;
        this.vertices = distances.length;
        this.pheromones = new double[vertices][vertices];
        this.ants = new ArrayList<>();
        this.random = new Random();
        initializePheromones(greedySolution());
    }

    private void initializePheromones(double Lmin) {
        double initialPheromone = (Lmin > 0) ? 1.0 / Lmin : 1.0;
        for (int i = 0; i < vertices; i++) {
            for (int j = 0; j < vertices; j++) {
                pheromones[i][j] = initialPheromone;
            }
        }
    }

    private void initializeAnts() {
        ants.clear();
        for (int i = 0; i < NUM_ANTS; i++) {
            int startVertex = random.nextInt(vertices);
            ants.add(new Ant(startVertex, vertices));
        }
    }

    private double computeHeuristic(int i, int j) {
        return 1.0 / (distances[i][j] + 1e-10);
    }

    private int selectNextVertex(Ant ant) {
        int current = ant.getCurrentVertex();
        double[] probabilities = new double[vertices];
        double sum = 0.0;

        for (int j = 0; j < vertices; j++) {
            if (ant.isNotVisited(j)) {
                double probability = Math.pow(pheromones[current][j], ALPHA) *
                                     Math.pow(computeHeuristic(current, j), BETA);
                probabilities[j] = probability;
                sum += probability;
            }
        }

        double randomPoint = random.nextDouble() * sum;
        double cumulative = 0.0;
        for (int j = 0; j < vertices; j++) {
            if (ant.isNotVisited(j)) {
                cumulative += probabilities[j];
                if (cumulative >= randomPoint) {
                    return j;
                }
            }
        }

        for (int j = 0; j < vertices; j++) {
            if (ant.isNotVisited(j)) {
                return j;
            }
        }
        return -1;
    }

    private void updatePheromones() {
        for (int i = 0; i < vertices; i++) {
            for (int j = 0; j < vertices; j++) {
                pheromones[i][j] *= (1 - EVAPORATION_RATE);
            }
        }

        ants.forEach(ant -> {
            double contribution = 1.0 / ant.getPathLength();
            List<Integer> path = ant.getPath();
            for (int i = 0; i < path.size() - 1; i++) {
                int from = path.get(i);
                int to = path.get(i + 1);
                pheromones[from][to] += contribution;
                pheromones[to][from] += contribution;
            }
        });
    }

    private double greedySolution() {
        boolean[] visited = new boolean[vertices];
        List<Integer> path = new ArrayList<>();
        int currentVertex = 0;
        path.add(currentVertex);
        visited[currentVertex] = true;
        double totalDistance = 0.0;

        for (int i = 1; i < vertices; i++) {
            double nearestDistance = Double.MAX_VALUE;
            int nearestVertex = -1;

            for (int j = 0; j < vertices; j++) {
                if (!visited[j] && distances[currentVertex][j] < nearestDistance) {
                    nearestDistance = distances[currentVertex][j];
                    nearestVertex = j;
                }
            }

            path.add(nearestVertex);
            visited[nearestVertex] = true;
            totalDistance += nearestDistance;
            currentVertex = nearestVertex;
        }

        totalDistance += distances[currentVertex][path.getFirst()];
        return totalDistance;
    }

    public void solve() {
        System.out.println(BRIGHT_BACKGROUND_RED + "Starting ACO Algorithm" + RESET);
        System.out.println(BRIGHT_BACKGROUND_RED + "200 vertices, colony of 45 ants" + RESET);
        System.out.println(BRIGHT_BACKGROUND_RED + "Greedy Lmin, a = 3, b = 2, p = 0.3" + RESET);

        String bestPath = "";

        double Lmin = greedySolution();
        System.out.println("L min (Greedy Solution): " + Lmin);

        int iteration = 0;
        double bestPathLength = Double.MAX_VALUE;
        double previousBestPathLength = Double.MAX_VALUE;

//        List<Integer> iterations = new ArrayList<>();
//        List<Double> bestPathLengths = new ArrayList<>();

        while (iteration < ITERATIONS) {
            initializeAnts();
            for (Ant ant : ants) {
                while (ant.getPath().size() < vertices) {
                    int nextVertex = selectNextVertex(ant);
                    ant.visitVertex(nextVertex, distances[ant.getCurrentVertex()][nextVertex]);
                }
            }

            updatePheromones();

            double iterationBestLength = ants.stream().mapToDouble(Ant::getPathLength).min().orElse(Double.MAX_VALUE);
            if (iterationBestLength < bestPathLength) {
                previousBestPathLength = bestPathLength;
                bestPathLength = iterationBestLength;
            }

            if (iteration % QUALITY == 0) {
//                iterations.add(iteration);
//                bestPathLengths.add(bestPathLength);
                if (bestPathLength < previousBestPathLength) {
                    System.out.printf(GREEN + "Iteration: %d, Best Path Length: %f (Improved)\n" + RESET, iteration, bestPathLength);
                    bestPath = printFullBestPath(bestPathLength);
                    previousBestPathLength = bestPathLength;
                } else {
                    if (VERBOSE) {
                        System.out.printf("Iteration: %d, Best Path Length: %f\n", iteration, bestPathLength);
                    }
                }
            }

            iteration++;
        }

        System.out.println(BRIGHT_BACKGROUND_CYAN + "\n\nReached the maximum number of iterations\n" + RESET);

        System.out.println(bestPath);

//        if (VERBOSE) {
//            System.out.println("Iterations: " + iterations.toString().replace(',', '\n'));
//            System.out.println(iterations.size());
//            System.out.println("Best Path Lengths: " + bestPathLengths.toString().replace(',', '\n'));
//            System.out.println(bestPathLengths.size());
//        }
    }

    private String printFullBestPath(double bestPathLength) {
        System.out.println(GREEN + "Best Path Length: " + bestPathLength + RESET);
        String bestPath = ants.stream().min(Comparator.comparingDouble(Ant::getPathLength)).orElse(new Ant(0, vertices)).getPath().toString();
        System.out.println(BACKGROUND_GREEN + "Best Path: " + bestPath + RESET);
        return "Best Path Length: " + bestPath;
    }

}

