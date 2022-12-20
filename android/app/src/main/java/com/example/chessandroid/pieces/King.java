package com.example.chessandroid.pieces;

import com.example.chessandroid.game.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class King extends Piece implements Serializable {
    private static final long serialVersionUID = 1L;

    public King(boolean white, String name) {
        super(white, name);
    }

    public List<Tile> getAvailableMoves(Board board, int startX, int startY) {
        List<Tile> availableMoves = new ArrayList<>();
        if (canCastle(board, startX, startY, 1)) {
            availableMoves.add(board.getTile(startX, startY + 2));
        }
        if (canCastle(board, startX, startY, -1)) {
            availableMoves.add(board.getTile(startX, startY - 2));
        }
        for (int i = 1; i > -2; i--) {
            for (int j = 1; j > -2; j--) {
                if (!(i == 0 && j == 0)) {
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
        return availableMoves;
    }

    public boolean canCastle(Board board, int startX, int startY, int direction) {
        if (isMoved()) {
            return false;
        }
        int endY = direction == -1 ? 0 : 7;
        Piece endPiece = board.getTile(startX, endY).getPiece();
        if (endPiece != null
                && this.isWhite() == endPiece.isWhite()
                && endPiece instanceof Rook
                && !endPiece.isMoved()) {
            int j = startY;
            while (j != endY) {
                if (j != startY) {
                    if (board.getTile(startX, j).getPiece() != null) {
                        return false;
                    }
                }
                j += direction;
            }
            int[] pathY = new int[]{startY, startY + direction, startY + direction * 2};
            for (int pY : pathY) {
                Move move = new Move(isWhite(), startX, startY, startX, pY);
                move.make();
                if (this.isChecked(board, startX, pY)) {
                    move.unmake();
                    return false;
                }
                move.unmake();
            }
            return true;
        }
        return false;
    }

    public boolean isChecked(Board board, int kX, int kY) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = board.getTile(i, j).getPiece();
                if (piece != null && this.isWhite() != piece.isWhite()) {
                    if (piece.canMove(board, i, j, kX, kY)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isCheckmated(Board board, int kX, int kY) {
        if (!this.isChecked(board, kX, kY)) {
            return false;
        }
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece startPiece = board.getTile(i, j).getPiece();
                if (startPiece != null && this.isWhite() == startPiece.isWhite()) {
                    List<Tile> tiles = startPiece.getAvailableMoves(board, i, j);
                    for (Tile endTile : tiles) {
                        Move move = new Move(isWhite(), i, j, endTile.getX(), endTile.getY());
                        move.make();
                        int newKX = (i == kX && j == kY) ? endTile.getX() : kX;
                        int newKY = (i == kX && j == kY) ? endTile.getY() : kY;
                        if (!this.isChecked(board, newKX, newKY)) {
                            move.unmake();
                            return false;
                        }
                        move.unmake();
                    }
                }
            }
        }
        return true;
    }
}
