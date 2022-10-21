package players;

import game.*;
import pieces.*;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.*;

public class Zobrist implements Serializable {
    private static final long serialVersionUID = 1L;

    public static Map<Character, Integer> pieceIndex = new HashMap<Character, Integer>() {{
        put('p', 0);
        put('N', 1);
        put('B', 2);
        put('R', 3);
        put('Q', 4);
        put('K', 5);
    }};
    public static long[][][][] pieceSquareTable = new long[2][6][8][8];
    public static long blackTurn;
    public static long[][] castlingRights = new long[2][2];
    public static long[] enPassantFiles = new long[8];
    public static SecureRandom secureRandom = new SecureRandom();

    public Zobrist() {
    }

    public static void updatePieceSquare(Piece piece, int x, int y) {
        Game.hashKey ^= pieceSquareTable[piece.isWhite() ? 0 : 1][pieceIndex.get(piece.getName().charAt(1))][x][y];
    }

    public static void updateTurn() {
        Game.hashKey ^= blackTurn;
    }

    public static void setCastlingRight(int color, int direction) {
        Game.castlingRights[color][direction] = true;
        Game.hashKey ^= castlingRights[color][direction];
    }

    public static void resetCastlingRight(int color, int direction) {
        if (Game.castlingRights[color][direction]) {
            Game.hashKey ^= castlingRights[color][direction];
        }
        Game.castlingRights[color][direction] = false;
    }

    public static void setEnPassantFile(int file) {
        Game.enPassantFile = file;
        Game.hashKey ^= enPassantFiles[Game.enPassantFile];
    }

    public static void resetEnPassantFile() {
        if (Game.enPassantFile != -1) {
            Game.hashKey ^= enPassantFiles[Game.enPassantFile];
        }
        Game.enPassantFile = -1;
    }


    public static void initialize() {
        for (int color = 0; color < 2; color++) {
            for (int piece = 0; piece < 6; piece++) {
                for (int row = 0; row < 8; row++) {
                    for (int column = 0; column < 8; column++) {
                        pieceSquareTable[color][piece][row][column] = secureRandom.nextLong();
                    }
                }
            }
        }

        blackTurn = secureRandom.nextLong();

        for (int color = 0; color < 2; color++) {
            for (int direction = 0; direction < 2; direction++) {
                castlingRights[color][direction] = secureRandom.nextLong();
            }
        }

        for (int column = 0; column < 8; column++) {
            enPassantFiles[column] = secureRandom.nextLong();
        }
    }

    public static void createHashKey() {
        Game.hashKey = 0;
        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 8; column++) {
                Piece piece = Game.board.getTile(row, column).getPiece();
                if (piece != null) {
                    updatePieceSquare(piece, row, column);
                }
            }
        }
        for (int color = 0; color < 2; color++) {
            for (int direction = 0; direction < 2; direction++) {
                setCastlingRight(color, direction);
            }
        }
    }
}
