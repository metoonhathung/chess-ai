package com.example.chessandroid.pieces;

import com.example.chessandroid.game.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece implements Serializable {
    private static final long serialVersionUID = 1L;

    public Pawn(boolean white, String name) {
        super(white, name);
    }

    public Tile canEnPassant(Board board, int startX, int startY) {
        if (Game.movesPlayed.size() == 0) {
            return null;
        }
        Move lastMove = Game.movesPlayed.get(Game.movesPlayed.size() - 1);
        if (isWhite()
                && lastMove.getStartX() == 1
                && lastMove.getEndX() == 3
                && lastMove.getPieceMoved().getName().equals("bp")
                && startX == lastMove.getEndX()
                && Math.abs(startY - lastMove.getEndY()) == 1
        ) {
            return board.getTile(startX - 1, lastMove.getEndY());
        }
        if (!isWhite()
                && lastMove.getStartX() == 6
                && lastMove.getEndX() == 4
                && lastMove.getPieceMoved().getName().equals("wp")
                && startX == lastMove.getEndX()
                && Math.abs(startY - lastMove.getEndY()) == 1
        ) {
            return board.getTile(startX + 1, lastMove.getEndY());
        }
        return null;
    }

    public List<Tile> getAvailableMoves(Board board, int startX, int startY) {
        List<Tile> availableMoves = new ArrayList<>();
        if (!isWhite()) {
            if (!isMoved()) {
                if (board.getTile(startX + 2, startY).getPiece() == null && board.getTile(startX + 1, startY).getPiece() == null) {
                    availableMoves.add(board.getTile(startX + 2, startY));
                }
            }

            if (startX + 1 < 8) {
                if (board.getTile(startX + 1, startY).getPiece() == null) {
                    availableMoves.add(board.getTile(startX + 1, startY));
                }
            }

            if (startX + 1 < 8 && startY + 1 < 8) {
                if (board.getTile(startX + 1, startY + 1).getPiece() != null && board.getTile(startX + 1, startY + 1).getPiece().isWhite() != this.isWhite()) {
                    availableMoves.add(board.getTile(startX + 1, startY + 1));
                }
            }

            if (startX + 1 < 8 && startY - 1 >= 0) {
                if (board.getTile(startX + 1, startY - 1).getPiece() != null && board.getTile(startX + 1, startY - 1).getPiece().isWhite() != this.isWhite()) {
                    availableMoves.add(board.getTile(startX + 1, startY - 1));
                }
            }
        }

        if (isWhite()) {
            if (!isMoved()) {
                if (board.getTile(startX - 2, startY).getPiece() == null && board.getTile(startX - 1, startY).getPiece() == null) {
                    availableMoves.add(board.getTile(startX - 2, startY));
                }
            }

            if (startX - 1 >= 0) {
                if (board.getTile(startX - 1, startY).getPiece() == null) {
                    availableMoves.add(board.getTile(startX - 1, startY));
                }
            }

            if (startX - 1 >= 0 && startY + 1 < 8) {
                if (board.getTile(startX - 1, startY + 1).getPiece() != null && board.getTile(startX - 1, startY + 1).getPiece().isWhite() != this.isWhite()) {
                    availableMoves.add(board.getTile(startX - 1, startY + 1));
                }
            }

            if (startX - 1 >= 0 && startY - 1 >= 0) {
                if (board.getTile(startX - 1, startY - 1).getPiece() != null && board.getTile(startX - 1, startY - 1).getPiece().isWhite() != this.isWhite()) {
                    availableMoves.add(board.getTile(startX - 1, startY - 1));
                }
            }
        }

        Tile enPassantTile = canEnPassant(board, startX, startY);
        if (enPassantTile != null) {
            availableMoves.add(enPassantTile);
        }
        return availableMoves;
    }
}
