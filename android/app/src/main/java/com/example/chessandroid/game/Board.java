package com.example.chessandroid.game;

import com.example.chessandroid.pieces.*;

import java.io.Serializable;

public class Board implements Serializable {
    private static final long serialVersionUID = 1L;

    private Tile[][] matrix;

    public Board() {
        this.initBoard();
    }

    public Tile[][] getMatrix() {
        return this.matrix;
    }

    public void setMatrix(Tile[][] matrix) {
        this.matrix = matrix;
    }

    public Tile getTile(int x, int y) {
        if (x < 0 || x > 7 || y < 0 || y > 7) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return matrix[x][y];
    }

    public void initBoard() {
        matrix = new Tile[8][8];
        // initialize white pieces
        matrix[0][0] = new Tile(0, 0, new Rook(false, "bR"));
        matrix[0][1] = new Tile(0, 1, new Knight(false, "bN"));
        matrix[0][2] = new Tile(0, 2, new Bishop(false, "bB"));
        matrix[0][3] = new Tile(0, 3, new Queen(false, "bQ"));
        matrix[0][4] = new Tile(0, 4, new King(false, "bK"));
        matrix[0][5] = new Tile(0, 5, new Bishop(false, "bB"));
        matrix[0][6] = new Tile(0, 6, new Knight(false, "bN"));
        matrix[0][7] = new Tile(0, 7, new Rook(false, "bR"));

        matrix[1][0] = new Tile(1, 0, new Pawn(false, "bp"));
        matrix[1][1] = new Tile(1, 1, new Pawn(false, "bp"));
        matrix[1][2] = new Tile(1, 2, new Pawn(false, "bp"));
        matrix[1][3] = new Tile(1, 3, new Pawn(false, "bp"));
        matrix[1][4] = new Tile(1, 4, new Pawn(false, "bp"));
        matrix[1][5] = new Tile(1, 5, new Pawn(false, "bp"));
        matrix[1][6] = new Tile(1, 6, new Pawn(false, "bp"));
        matrix[1][7] = new Tile(1, 7, new Pawn(false, "bp"));

        // initialize black pieces
        matrix[7][0] = new Tile(7, 0, new Rook(true, "wR"));
        matrix[7][1] = new Tile(7, 1, new Knight(true, "wN"));
        matrix[7][2] = new Tile(7, 2, new Bishop(true, "wB"));
        matrix[7][3] = new Tile(7, 3, new Queen(true, "wQ"));
        matrix[7][4] = new Tile(7, 4, new King(true, "wK"));
        matrix[7][5] = new Tile(7, 5, new Bishop(true, "wB"));
        matrix[7][6] = new Tile(7, 6, new Knight(true, "wN"));
        matrix[7][7] = new Tile(7, 7, new Rook(true, "wR"));

        matrix[6][0] = new Tile(6, 0, new Pawn(true, "wp"));
        matrix[6][1] = new Tile(6, 1, new Pawn(true, "wp"));
        matrix[6][2] = new Tile(6, 2, new Pawn(true, "wp"));
        matrix[6][3] = new Tile(6, 3, new Pawn(true, "wp"));
        matrix[6][4] = new Tile(6, 4, new Pawn(true, "wp"));
        matrix[6][5] = new Tile(6, 5, new Pawn(true, "wp"));
        matrix[6][6] = new Tile(6, 6, new Pawn(true, "wp"));
        matrix[6][7] = new Tile(6, 7, new Pawn(true, "wp"));

        // initialize remaining matrix without any piece
        for (int i = 2; i < 6; i++) {
            for (int j = 0; j < 8; j++) {
                matrix[i][j] = new Tile(i, j, null);
            }
        }
    }

    public void printBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = matrix[i][j].getPiece();
                if (piece != null) {
                    System.out.print(piece.getName());
                } else {
                    if ((i + j) % 2 == 0) {
                        System.out.print("##");
                    } else {
                        System.out.print("  ");
                    }
                }
                System.out.print(" ");
            }
            System.out.println((8 - i) + " ");
        }
        System.out.println(" a  b  c  d  e  f  g  h ");
    }
}

