package com.example.chessandroid.pieces;

import com.example.chessandroid.game.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece implements Serializable {
    private static final long serialVersionUID = 1L;

    public Knight(boolean white, String name) {
        super(white, name);
    }

    public List<Tile> getAvailableMoves(Board board, int startX, int startY) {
        List<Tile> availableMoves = new ArrayList<>();

        for (int i = 2; i > -3; i--) {
            for (int j = 2; j > -3; j--) {
                if (Math.abs(i) == 2 ^ Math.abs(j) == 2) {
                    if (i != 0 && j != 0) {
                        try {
                            if (board.getTile(startX + i, startY + j).getPiece() == null || board.getTile(startX + i, startY + j).getPiece().isWhite() != this.isWhite()) {
                                availableMoves.add(board.getTile(startX + i, startY + j));
                            }
                        } catch (ArrayIndexOutOfBoundsException e) {
                            continue;
                        }
                    }
                }
            }
        }
        return availableMoves;
    }
}
