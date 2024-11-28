package dev.ua.ikeepcalm.solution;

import java.util.ArrayList;
import java.util.List;

class Ant {
    private final List<Integer> path;
    private double pathLength;
    private int currentVertex;
    private final boolean[] visited;

    public Ant(int startVertex, int totalVertices) {
        this.path = new ArrayList<>();
        this.visited = new boolean[totalVertices];
        this.pathLength = 0.0;
        this.currentVertex = startVertex;
        this.path.add(startVertex);
        this.visited[startVertex] = true;
    }

    public void visitVertex(int vertex, double distance) {
        path.add(vertex);
        visited[vertex] = true;
        pathLength += distance;
        currentVertex = vertex;
    }

    public boolean isNotVisited(int vertex) {
        return !visited[vertex];
    }

    public int getCurrentVertex() {
        return currentVertex;
    }

    public List<Integer> getPath() {
        return path;
    }

    public double getPathLength() {
        return pathLength;
    }
}
