package dev.ua.ikeepcalm.solution;

import java.util.Random;

public class TSP {
    private static final int VERTICES = 200;
    private static final int MAX_DISTANCE = 50;
    private final int[][] distances;

    public TSP() {
        this.distances = new int[VERTICES][VERTICES];
        generateDistances();
    }

    private void generateDistances() {
        Random random = new Random();
        for (int i = 0; i < VERTICES; i++) {
            for (int j = i + 1; j < VERTICES; j++) {
                double distance = random.nextDouble() * MAX_DISTANCE;
                distances[i][j] = (int) distance;
                distances[j][i] = (int) distance;
            }
        }
    }

    public int[][] getDistances() {
        return distances;
    }
}
