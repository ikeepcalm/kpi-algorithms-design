package dev.ua.ikeepcalm.solution;

import java.util.Random;

public class EightQueens {

    private static final int SIZE = 8;
    private final int[][] board = new int[SIZE][SIZE];

    public EightQueens() {
        generateRandomBoard();
    }

    private void generateRandomBoard() {
        Random random = new Random();
        for (int i = 0; i < SIZE; i++) {
            int col = random.nextInt(SIZE);
            board[i][col] = 1;
        }
    }

    public int[][] getBoard() {
        return board;
    }

    public void printBoard() {
        System.out.println("  a b c d e f g h");
        System.out.println(" __________________");
        for (int row = 0; row < SIZE; row++) {
            System.out.print((row + 1) + "|");
            for (int col = 0; col < SIZE; col++) {
                if (board[row][col] == 1) {
                    System.out.print("Q ");
                } else {
                    System.out.print(". ");
                }
                if (col == SIZE - 1) {
                    System.out.print("|" + (row + 1));
                }
            }
            System.out.println();
        }

        System.out.println(" _________________");
        System.out.println("  a b c d e f g h");
    }

}
