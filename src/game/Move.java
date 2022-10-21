package game;

import pieces.*;
import players.Zobrist;

import java.io.Serializable;

enum Type {
    NORMAL,
    CASTLING,
    EN_PASSANT,
    PROMOTION
}

public class Move implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean white;
    private int startX;
    private int startY;
    private int endX;
    private int endY;
    private Piece pieceMoved;
    private Piece pieceKilled;
    private boolean prevMoved;
    private int prevWhiteKingX;
    private int prevWhiteKingY;
    private int prevBlackKingX;
    private int prevBlackKingY;
    private long prevHashKey;
    private int prevEnPassantFile;
    private boolean[][] prevCastlingRights;
    private Type type;
    private char promotionType;

    public Move(boolean white, int startX, int startY, int endX, int endY) {
        this.white = white;
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }

    public void make() {
        Tile startTile = Game.board.getTile(startX, startY);
        Tile endTile = Game.board.getTile(endX, endY);
        // store prev state
        prevWhiteKingX = Game.whiteKingX;
        prevWhiteKingY = Game.whiteKingY;
        prevBlackKingX = Game.blackKingX;
        prevBlackKingY = Game.blackKingY;
        prevHashKey = Game.hashKey;
        prevEnPassantFile = Game.enPassantFile;
        prevCastlingRights = new boolean[][]{{Game.castlingRights[0][0], Game.castlingRights[0][1]}, {Game.castlingRights[1][0], Game.castlingRights[1][1]}};
        Zobrist.updateTurn(); // flip turn
        type = Type.NORMAL;
        pieceMoved = startTile.getPiece();
        Piece endPiece = endTile.getPiece();
        if (endPiece != null && startTile != endTile) {
            pieceKilled = endPiece;
            Zobrist.updatePieceSquare(pieceKilled, endTile.getX(), endTile.getY()); // remove victim at dst
        }
        // castling?
        if (pieceMoved instanceof King && Math.abs(startTile.getY() - endTile.getY()) == 2) {
            type = Type.CASTLING;
            int direction = endTile.getY() < startTile.getY() ? -1 : 1;
            Tile rookStartTile = Game.board.getTile(startTile.getX(), direction == -1 ? 0 : 7);
            Tile rookEndTile = Game.board.getTile(startTile.getX(), startTile.getY() + direction);
            Piece rookPiece = rookStartTile.getPiece();
            rookPiece.setMoved(true);
            rookEndTile.setPiece(rookPiece);
            Zobrist.updatePieceSquare(rookPiece, rookEndTile.getX(), rookEndTile.getY()); // put rook at dst
            rookStartTile.setPiece(null);
            Zobrist.updatePieceSquare(rookPiece, rookStartTile.getX(), rookStartTile.getY()); // remove rook at src
        }
        // enpassant?
        if (pieceMoved instanceof Pawn && endPiece == null
                && Math.abs(startTile.getX() - endTile.getX()) == 1
                && Math.abs(startTile.getY() - endTile.getY()) == 1) {
            type = Type.EN_PASSANT;
            Tile killTile = Game.board.getTile(startTile.getX(), endTile.getY());
            pieceKilled = killTile.getPiece();
            killTile.setPiece(null);
            Zobrist.updatePieceSquare(pieceKilled, killTile.getX(), killTile.getY()); // remove victim at location
        }
        // promotion?
        if (pieceMoved instanceof Pawn
                && ((white && endTile.getX() == 0) || (!white && endTile.getX() == 7))) {
            type = Type.PROMOTION;
            Zobrist.updatePieceSquare(pieceMoved, startTile.getX(), startTile.getY()); // remove pawn at src
            switch (promotionType) {
                case 'R':
                    pieceMoved = new Rook(white, white ? "wR" : "bR");
                    break;
                case 'B':
                    pieceMoved = new Bishop(white, white ? "wB" : "bB");
                    break;
                case 'N':
                    pieceMoved = new Knight(white, white ? "wK" : "bK");
                    break;
                default:
                    pieceMoved = new Queen(white, white ? "wQ" : "bQ");
                    break;
            }
            pieceMoved.setMoved(true);
            Zobrist.updatePieceSquare(pieceMoved, startTile.getX(), startTile.getY()); // put queen at src
        }
        prevMoved = pieceMoved.isMoved();
        pieceMoved.setMoved(true);
        startTile.setPiece(null);
        Zobrist.updatePieceSquare(pieceMoved, startTile.getX(), startTile.getY()); // remove piece at src
        endTile.setPiece(pieceMoved);
        Zobrist.updatePieceSquare(pieceMoved, endTile.getX(), endTile.getY()); // put piece at dst

        // kings pos
        if (pieceMoved instanceof King) {
            if (pieceMoved.isWhite()) {
                Game.whiteKingX = endX;
                Game.whiteKingY = endY;
            } else {
                Game.blackKingX = endX;
                Game.blackKingY = endY;
            }
        }

        // en passant file
        Zobrist.resetEnPassantFile(); // reset
        if (pieceMoved instanceof Pawn && Math.abs(endTile.getX() - startTile.getX()) == 2) {
            Zobrist.setEnPassantFile(endTile.getY()); // set
        }

        // castling rights
        if (pieceMoved instanceof King && !prevMoved) {
            Zobrist.resetCastlingRight(pieceMoved.isWhite() ? 0 : 1, 0); // reset left
            Zobrist.resetCastlingRight(pieceMoved.isWhite() ? 0 : 1, 1); // reset right
        }
        if (pieceMoved instanceof Rook && !prevMoved) {
            Zobrist.resetCastlingRight(pieceMoved.isWhite() ? 0 : 1, startTile.getY() == 0 ? 0 : 1); // reset
        }
        if (pieceKilled instanceof Rook) {
            Zobrist.resetCastlingRight(pieceKilled.isWhite() ? 0 : 1, endTile.getY() == 0 ? 0 : 1); // reset
        }

        Game.movesPlayed.add(this);
        Game.positionCount.put(Game.hashKey, Game.positionCount.getOrDefault(Game.hashKey, 0) + 1);
    }

    public void unmake() {
        Tile startTile = Game.board.getTile(startX, startY);
        Tile endTile = Game.board.getTile(endX, endY);
        if (type == Type.CASTLING) {
            int direction = endTile.getY() < startTile.getY() ? -1 : 1;
            Tile rookStartTile = Game.board.getTile(startTile.getX(), direction == -1 ? 0 : 7);
            Tile rookEndTile = Game.board.getTile(startTile.getX(), startTile.getY() + direction);
            Piece rookPiece = rookEndTile.getPiece();
            rookPiece.setMoved(false);
            rookEndTile.setPiece(null);
            rookStartTile.setPiece(rookPiece);
        }
        if (type == Type.EN_PASSANT) {
            Tile killTile = Game.board.getTile(startTile.getX(), endTile.getY());
            killTile.setPiece(pieceKilled);
        }
        if (type == Type.PROMOTION) {
            boolean whitePawn = pieceMoved.getName().charAt(0) == 'w';
            pieceMoved = new Pawn(whitePawn, whitePawn ? "wp" : "bp");
        }
        pieceMoved.setMoved(prevMoved);
        endTile.setPiece(type == Type.EN_PASSANT ? null : pieceKilled);
        startTile.setPiece(pieceMoved);
        // restore prev state
        Game.movesPlayed.remove(Game.movesPlayed.size() - 1);
        Game.positionCount.put(Game.hashKey, Game.positionCount.getOrDefault(Game.hashKey, 0) - 1);
        Game.whiteKingX = prevWhiteKingX;
        Game.whiteKingY = prevWhiteKingY;
        Game.blackKingX = prevBlackKingX;
        Game.blackKingY = prevBlackKingY;
        Game.hashKey = prevHashKey;
        Game.enPassantFile = prevEnPassantFile;
        Game.castlingRights = new boolean[][]{{prevCastlingRights[0][0], prevCastlingRights[0][1]}, {prevCastlingRights[1][0], prevCastlingRights[1][1]}};
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Move)) {
            return false;
        }
        Move other = (Move) obj;
        return this.isWhite() == other.isWhite()
                && this.getStartX() == other.getStartX()
                && this.getStartY() == other.getStartY()
                && this.getEndX() == other.getEndX()
                && this.getEndY() == other.getEndY();
    }

    public int getStartX() {
        return startX;
    }

    public void setStartX(int startX) {
        this.startX = startX;
    }

    public int getStartY() {
        return startY;
    }

    public void setStartY(int startY) {
        this.startY = startY;
    }

    public int getEndX() {
        return endX;
    }

    public void setEndX(int endX) {
        this.endX = endX;
    }

    public int getEndY() {
        return endY;
    }

    public void setEndY(int endY) {
        this.endY = endY;
    }

    public boolean isWhite() {
        return this.white;
    }

    public void setWhite(boolean white) {
        this.white = white;
    }

    public Piece getPieceMoved() {
        return this.pieceMoved;
    }

    public void setPieceMoved(Piece pieceMoved) {
        this.pieceMoved = pieceMoved;
    }

    public Piece getPieceKilled() {
        return this.pieceKilled;
    }

    public void setPieceKilled(Piece pieceKilled) {
        this.pieceKilled = pieceKilled;
    }

    public boolean isPrevMoved() {
        return this.prevMoved;
    }

    public void setPrevMoved(boolean prevMoved) {
        this.prevMoved = prevMoved;
    }

    public Type getType() {
        return this.type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public char getPromotionType() {
        return this.promotionType;
    }

    public void setPromotionType(char promotionType) {
        this.promotionType = promotionType;
    }
}

