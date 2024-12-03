package dev.ua.ikeepcalm.solution;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ACO {

    private int numCities;
    private int numAnts;
    private int maxIterations;
    private double alpha;
    private double beta;
    private double rho;
    private double Lmin;
    private int elitistFactor;
    private boolean verbose;

    private double[][] pheromone;
    private double[][] heuristic;
    private final List<Ant> ants = new ArrayList<>();
    private final Random rand = new Random();

    private final TSP tsp;

    public double bestLength = Double.MAX_VALUE;
    public List<Integer> bestTour = new ArrayList<>();
    private final List<String> iterationData = new ArrayList<>();

    public ACO(int numCities, int numAnts, int maxIterations, double alpha, double beta, double rho, double Lmin, int elitistFactor, TSP tsp, boolean verbose) {
        this.numCities = numCities;
        this.numAnts = numAnts;
        this.maxIterations = maxIterations;
        this.alpha = alpha;
        this.beta = beta;
        this.rho = rho;
        this.Lmin = Lmin;
        this.elitistFactor = elitistFactor;
        this.tsp = tsp;
        this.verbose = verbose;

        initializeMatrices();
    }

    private void initializeMatrices() {
        pheromone = new double[numCities][numCities];
        heuristic = new double[numCities][numCities];

        for (int i = 0; i < numCities; i++) {
            for (int j = 0; j < numCities; j++) {
                pheromone[i][j] = 1.0;
                if (tsp.distanceMatrix[i][j] > 0) {
                    double distanceWeight = 0.7;
                    double costWeight = 0.3;
                    double combinedCriterion = (distanceWeight * tsp.distanceMatrix[i][j]) + (costWeight * tsp.costMatrix[i][j]);
                    heuristic[i][j] = 1.0 / combinedCriterion;
                } else {
                    heuristic[i][j] = 0;
                }
            }
        }
    }

    public void solve() {
        for (int iteration = 0; iteration < maxIterations; iteration++) {
            ants.clear();

            for (int k = 0; k < numAnts; k++) {
                int startCity = rand.nextInt(numCities);
                ants.add(new Ant(numCities, tsp, startCity));
            }

            for (Ant ant : ants) {
                while (ant.tour.size() < numCities) {
                    int nextCity = selectNextCity(ant);
                    if (nextCity == -1) {
                        break;
                    }
                    ant.visitCity(nextCity);
                }

                ant.tour.add(ant.tour.getFirst());
                ant.calculateTourAttributes();

                if (ant.tourLength < bestLength) {
                    bestLength = ant.tourLength;
                    bestTour = new ArrayList<>(ant.tour);
                }
            }

            updatePheromones();

            if (verbose) {
                System.out.println("Iteration " + (iteration + 1) + ": Best Length = " + bestLength);
                iterationData.add((iteration + 1) + ", " + bestLength);
            }
        }

//        if (verbose) {
//            System.out.println("Best Tour Length: " + bestLength);
//            System.out.println("Best Tour: " + bestTour);
//            System.out.println("Iteration,Best Length");
//            for (String data : iterationData) {
//                System.out.println(data);
//            }
//        }
    }

    private int selectNextCity(Ant ant) {
        int from = ant.currentCity();
        double[] probabilities = new double[numCities];
        double sum = 0.0;

        for (int to = 0; to < numCities; to++) {
            if (!ant.isVisited(to) && tsp.distanceMatrix[from][to] > 0) {
                double tau = Math.pow(pheromone[from][to], alpha);
                double eta = Math.pow(heuristic[from][to], beta);
                probabilities[to] = tau * eta;
                sum += probabilities[to];
            } else {
                probabilities[to] = 0.0;
            }
        }

        if (sum == 0) {
            return -1;
        }

        for (int i = 0; i < numCities; i++) {
            probabilities[i] /= sum;
        }

        double randValue = rand.nextDouble();
        double cumulative = 0.0;
        for (int i = 0; i < numCities; i++) {
            cumulative += probabilities[i];
            if (randValue <= cumulative) {
                return i;
            }
        }

        for (int i = 0; i < numCities; i++) {
            if (probabilities[i] > 0) {
                return i;
            }
        }
        return -1;
    }

    private void updatePheromones() {
        for (int i = 0; i < numCities; i++) {
            for (int j = 0; j < numCities; j++) {
                pheromone[i][j] *= (1 - rho);
            }
        }

        for (Ant ant : ants) {
            double delta = Lmin / ant.tourLength;
            for (int i = 0; i < ant.tour.size() - 1; i++) {
                int from = ant.tour.get(i);
                int to = ant.tour.get(i + 1);
                pheromone[from][to] += delta;
            }
        }

        Ant bestAnt = getBestAnt();
        double deltaElite = elitistFactor * Lmin / bestAnt.tourLength;
        for (int i = 0; i < bestAnt.tour.size() - 1; i++) {
            int from = bestAnt.tour.get(i);
            int to = bestAnt.tour.get(i + 1);
            pheromone[from][to] += deltaElite;
        }
    }

    private Ant getBestAnt() {
        Ant bestAnt = ants.getFirst();
        for (Ant ant : ants) {
            if (ant.tourLength < bestAnt.tourLength) {
                bestAnt = ant;
            }
        }
        return bestAnt;
    }
}