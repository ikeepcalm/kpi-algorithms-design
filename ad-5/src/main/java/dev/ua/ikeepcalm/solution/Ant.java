package dev.ua.ikeepcalm.solution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Ant {
    public List<Integer> tour = new ArrayList<>();
    public boolean[] visited;
    public double tourLength = 0;
    public double tourCost = 0;

    private final TSP tsp;

    public Ant(int numCities, TSP tsp, int startCity) {
        this.tsp = tsp;
        visited = new boolean[numCities];
        Arrays.fill(visited, false);
        tour.add(startCity);
        visited[startCity] = true;
    }

    public void visitCity(int city) {
        tour.add(city);
        visited[city] = true;
    }

    public boolean isVisited(int city) {
        return visited[city];
    }

    public int currentCity() {
        return tour.getLast();
    }

    public void calculateTourAttributes() {
        tourLength = 0;
        tourCost = 0;
        for (int i = 0; i < tour.size() - 1; i++) {
            int from = tour.get(i);
            int to = tour.get(i + 1);
            tourLength += tsp.distanceMatrix[from][to];
            tourCost += tsp.costMatrix[from][to];
        }

        int lastCity = tour.getLast();
        int firstCity = tour.getFirst();
        tourLength += tsp.distanceMatrix[lastCity][firstCity];
        tourCost += tsp.costMatrix[lastCity][firstCity];
    }
}