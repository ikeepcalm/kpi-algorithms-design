package dev.ua.ikeepcalm.solution.algorithms;

public class LDFS {

    public static int iterations = 0;
    private static final int N = 8;

    private static final String ANSI_CLEAR = "\033[H";
    private static final String ANSI_RESET = "\033[2J";

    public static int totalNodes = 0;
    public static int maxNodesInMemory = 0;

    public static boolean solveLDFS(int[][] board, int depth, int maxDepth, boolean print) throws InterruptedException {
        if (print) {
            System.out.println(ANSI_RESET);
        }

        iterations++;
        totalNodes++;

        maxNodesInMemory = Math.max(maxNodesInMemory, depth);

        if (depth == N) {
            if (print) {
                System.out.println(ANSI_CLEAR);
                printBoard(board);
            }
            return true;
        }

        if (depth >= maxDepth) {
            return false;
        }

        int currentQueenCol = -1;
        for (int col = 0; col < N; col++) {
            if (board[depth][col] == 1) {
                currentQueenCol = col;
                break;
            }
        }

        for (int col = 0; col < N; col++) {
            if (col == currentQueenCol) {
                continue;
            }

            if (isSafe(board, depth, col)) {
                board[depth][currentQueenCol] = 0;
                board[depth][col] = 1;

                if (solveLDFS(board, depth + 1, maxDepth, print)) {
                    return true;
                }

                board[depth][col] = 0;
                board[depth][currentQueenCol] = 1;
                if (print) {
                    System.out.println(ANSI_CLEAR);
                    printBoard(board);
                    Thread.sleep(50);
                }
            }
        }

        return false;
    }

    private static void printBoard(int[][] board) {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    private static boolean isSafe(int[][] board, int row, int col) {
        totalNodes++;

        for (int i = 0; i < row; i++) {
            if (board[i][col] == 1) {
                return false;
            }
        }

        for (int i = row, j = col; i >= 0 && j >= 0; i--, j--) {
            if (board[i][j] == 1) {
                return false;
            }
        }

        for (int i = row, j = col; i >= 0 && j < N; i--, j++) {
            if (board[i][j] == 1) {
                return false;
            }
        }

        return true;
    }
}
