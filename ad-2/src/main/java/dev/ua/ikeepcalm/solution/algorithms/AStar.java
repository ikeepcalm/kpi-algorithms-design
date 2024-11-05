package dev.ua.ikeepcalm.solution.algorithms;

import java.util.*;

public class AStar {

    public static int iterations = 0;
    private static final int SIZE = 8;
    private static final String ANSI_CLEAR = "\033[H";
    private static final String ANSI_RESET = "\033[2J";

    public static int totalNodes = 0;
    public static int maxNodesInMemory = 0;

    private static class Node {
        int[][] board;
        int g;
        int h;
        int f;

        Node(int[][] board, int g, int h) {
            this.board = board;
            this.g = g;
            this.h = h;
            this.f = g + h;
        }
    }

    public static boolean solveAStar(int[][] initialBoard, boolean verbose) throws InterruptedException {
        if (verbose) {
            System.out.println(ANSI_RESET);
        }

        PriorityQueue<Node> openList = new PriorityQueue<>(Comparator.comparingInt(n -> n.f));
        Set<String> closedList = new HashSet<>();

        Node startNode = new Node(initialBoard, 0, heuristic(initialBoard));
        openList.add(startNode);
        totalNodes++;

        while (!openList.isEmpty()) {
            iterations++;
            maxNodesInMemory = Math.max(maxNodesInMemory, openList.size() + closedList.size());

            Node currentNode = openList.poll();
            if (currentNode.h == 0) {
                copyBoard(currentNode.board, initialBoard);
                if (verbose) {
                    System.out.println(ANSI_CLEAR);
                    printBoard(currentNode.board);
                }
                return true;
            }

            closedList.add(boardToString(currentNode.board));

            for (int[][] neighbor : generateNeighbors(currentNode.board)) {
                if (closedList.contains(boardToString(neighbor))) {
                    continue;
                }

                int g = currentNode.g + 1;
                int h = heuristic(neighbor);
                Node neighborNode = new Node(neighbor, g, h);
                openList.add(neighborNode);
                totalNodes++;

                if (verbose) {
                    System.out.println(ANSI_CLEAR);
                    printBoard(currentNode.board);
                }

            }
        }

        return false;
    }

    private static int heuristic(int[][] board) {
        int h = 0;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == 1) {
                    for (int k = i + 1; k < SIZE; k++) {
                        if (board[k][j] == 1) {
                            h++;
                        }
                        int diag1 = j - (k - i);
                        int diag2 = j + (k - i);
                        if (diag1 >= 0 && board[k][diag1] == 1) {
                            h++;
                        }
                        if (diag2 < SIZE && board[k][diag2] == 1) {
                            h++;
                        }
                    }
                }
            }
        }
        return h;
    }

    private static List<int[][]> generateNeighbors(int[][] board) {
        List<int[][]> neighbors = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == 1) {
                    for (int k = 0; k < SIZE; k++) {
                        if (k != j) {
                            int[][] newBoard = copyBoard(board);
                            newBoard[i][j] = 0;
                            newBoard[i][k] = 1;
                            neighbors.add(newBoard);
                        }
                    }
                }
            }
        }
        return neighbors;
    }

    private static int[][] copyBoard(int[][] board) {
        int[][] newBoard = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            System.arraycopy(board[i], 0, newBoard[i], 0, SIZE);
        }
        return newBoard;
    }

    private static void copyBoard(int[][] source, int[][] destination) {
        for (int i = 0; i < SIZE; i++) {
            System.arraycopy(source[i], 0, destination[i], 0, SIZE);
        }
    }

    private static String boardToString(int[][] board) {
        StringBuilder sb = new StringBuilder();
        for (int[] row : board) {
            for (int cell : row) {
                sb.append(cell);
            }
        }
        return sb.toString();
    }

    private static void printBoard(int[][] board) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
}
