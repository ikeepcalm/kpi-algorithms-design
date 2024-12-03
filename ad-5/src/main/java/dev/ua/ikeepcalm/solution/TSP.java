package dev.ua.ikeepcalm.solution;

import java.util.Random;

public class TSP {
    public int numCities;
    public double[][] distanceMatrix;
    public double[][] costMatrix;

    private final Random rand = new Random();

    public TSP(int numCities) {
        this.numCities = numCities;
        generateRandomGraph();
    }

    private void generateRandomGraph() {
        distanceMatrix = new double[numCities][numCities];
        costMatrix = new double[numCities][numCities];

        for (int i = 0; i < numCities; i++) {
            for (int j = i; j < numCities; j++) {
                if (i == j) {
                    distanceMatrix[i][j] = 0;
                    costMatrix[i][j] = 0;
                } else {
                    boolean isSymmetric = rand.nextBoolean();

                    double distance = 5 + (150 - 5) * rand.nextDouble();
                    double cost = 1 + (100 - 1) * rand.nextDouble();

                    distanceMatrix[i][j] = distance;
                    costMatrix[i][j] = cost;

                    if (isSymmetric) {
                        distanceMatrix[j][i] = distance;
                        costMatrix[j][i] = cost;
                    } else {
                        double distance2 = 5 + (150 - 5) * rand.nextDouble();
                        double cost2 = 1 + (100 - 1) * rand.nextDouble();

                        distanceMatrix[j][i] = distance2;
                        costMatrix[j][i] = cost2;
                    }
                }
            }
        }
    }
}