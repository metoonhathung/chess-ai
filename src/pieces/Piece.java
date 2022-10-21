package pieces;

import game.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Piece implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean white;
    private String name;
    private boolean moved;

    public Piece(boolean white, String name) {
        this.white = white;
        this.name = name;
        this.moved = false;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isWhite() {
        return this.white;
    }

    public void setWhite(boolean white) {
        this.white = white;
    }

    public boolean isMoved() {
        return this.moved;
    }

    public void setMoved(boolean moved) {
        this.moved = moved;
    }

    public boolean canMove(Board board, int startX, int startY, int endX, int endY) {
        List<Tile> availableMoves = this.getAvailableMoves(board, startX, startY);
        for (Tile move : availableMoves) {
            if (move.getX() == endX && move.getY() == endY) {
                return true;
            }
        }
        return false;
    }

    public abstract List<Tile> getAvailableMoves(Board board, int startX, int startY);

    public List<Tile> getLinearMoves(Board board, int startX, int startY) {
        List<Tile> linearMoves = new ArrayList<>();
        int lastXabove = 0;
        int lastYright = 7;
        int lastXbelow = 7;
        int lastYleft = 0;

        for (int i = 0; i < startX; i++) {
            if (board.getTile(i, startY).getPiece() != null) {
                if (board.getTile(i, startY).getPiece().isWhite() != this.isWhite()) {
                    lastXabove = i;
                } else {
                    lastXabove = i + 1;
                }
            }
        }

        for (int i = 7; i > startX; i--) {
            if (board.getTile(i, startY).getPiece() != null) {
                if (board.getTile(i, startY).getPiece().isWhite() != this.isWhite()) {
                    lastXbelow = i;
                } else {
                    lastXbelow = i - 1;
                }
            }
        }

        for (int j = 0; j < startY; j++) {
            if (board.getTile(startX, j).getPiece() != null) {
                if (board.getTile(startX, j).getPiece().isWhite() != this.isWhite()) {
                    lastYleft = j;
                } else {
                    lastYleft = j + 1;
                }
            }
        }

        for (int j = 7; j > startY; j--) {
            if (board.getTile(startX, j).getPiece() != null) {
                if (board.getTile(startX, j).getPiece().isWhite() != this.isWhite()) {
                    lastYright = j;
                } else {
                    lastYright = j - 1;
                }
            }
        }
        int[] limits = {lastXabove, lastXbelow, lastYleft, lastYright};

        for (int i = limits[0]; i <= limits[1]; i++) {
            if (i != startX) {
                linearMoves.add(board.getTile(i, startY));
            }
        }

        for (int j = limits[2]; j <= limits[3]; j++) {
            if (j != startY) {
                linearMoves.add(board.getTile(startX, j));
            }
        }

        return linearMoves;
    }

    public List<Tile> getDiagonalMoves(Board board, int startX, int startY) {
        List<Tile> diagonalMoves = new ArrayList<>();
        int xNW = startX - 1;
        int yNW = startY - 1;

        int xSW = startX + 1;
        int ySW = startY - 1;

        int xNE = startX - 1;
        int yNE = startY + 1;

        int xSE = startX + 1;
        int ySE = startY + 1;

        while (xNW >= 0 && yNW >= 0) {
            if (board.getTile(xNW, yNW).getPiece() != null) {
                if (board.getTile(xNW, yNW).getPiece().isWhite() == this.isWhite()) {
                    break;
                } else {
                    diagonalMoves.add(board.getTile(xNW, yNW));
                    break;
                }
            } else {
                diagonalMoves.add(board.getTile(xNW, yNW));
                xNW--;
                yNW--;
            }
        }

        while (xSW < 8 && ySW >= 0) {
            if (board.getTile(xSW, ySW).getPiece() != null) {
                if (board.getTile(xSW, ySW).getPiece().isWhite() == this.isWhite()) {
                    break;
                } else {
                    diagonalMoves.add(board.getTile(xSW, ySW));
                    break;
                }
            } else {
                diagonalMoves.add(board.getTile(xSW, ySW));
                xSW++;
                ySW--;
            }
        }

        while (xSE < 8 && ySE < 8) {
            if (board.getTile(xSE, ySE).getPiece() != null) {
                if (board.getTile(xSE, ySE).getPiece().isWhite() == this.isWhite()) {
                    break;
                } else {
                    diagonalMoves.add(board.getTile(xSE, ySE));
                    break;
                }
            } else {
                diagonalMoves.add(board.getTile(xSE, ySE));
                xSE++;
                ySE++;
            }
        }

        while (xNE >= 0 && yNE < 8) {
            if (board.getTile(xNE, yNE).getPiece() != null) {
                if (board.getTile(xNE, yNE).getPiece().isWhite() == this.isWhite()) {
                    break;
                } else {
                    diagonalMoves.add(board.getTile(xNE, yNE));
                    break;
                }
            } else {
                diagonalMoves.add(board.getTile(xNE, yNE));
                xNE--;
                yNE++;
            }
        }

        return diagonalMoves;
    }
}
