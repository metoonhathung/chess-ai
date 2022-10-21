package game;

import pieces.*;
import players.*;

import java.io.Serializable;
import java.util.*;

public class Game implements Serializable {
    private static final long serialVersionUID = 1L;

    public static Board board;
    public static King whiteKingPiece;
    public static King blackKingPiece;
    public static int whiteKingX;
    public static int whiteKingY;
    public static int blackKingX;
    public static int blackKingY;
    public static long hashKey;
    public static int enPassantFile;
    public static boolean[][] castlingRights;
    public static Map<Long, Integer> positionCount;
    public static List<Move> movesPlayed;

    public static Player[] players;
    public static Player currentPlayer;
    public static boolean whiteTurn;
    public static Status status;
    public static int turn;

    public Game() {
        this.initialize();
    }

    public static void initialize() {
        board = new Board();
        whiteKingX = 7;
        whiteKingY = 4;
        blackKingX = 0;
        blackKingY = 4;
        whiteKingPiece = (King) board.getTile(whiteKingX, whiteKingY).getPiece();
        blackKingPiece = (King) board.getTile(blackKingX, blackKingY).getPiece();
        hashKey = 0;
        enPassantFile = -1;
        castlingRights = new boolean[][]{{true, true}, {true, true}};
        Zobrist.initialize();
        Zobrist.createHashKey();
        positionCount = new HashMap<>();
        positionCount.put(hashKey, 1);
        players = new Player[2];
        players[0] = choosePlayer(true);
        players[1] = choosePlayer(false);
        currentPlayer = players[0];
        whiteTurn = true;
        status = Status.ACTIVE;
        movesPlayed = new ArrayList<>();
        turn = 0;
    }

    public static boolean playerMove(String line) {
        if (line.equals("resign")) {
            status = whiteTurn ? Status.BLACK_WIN : Status.WHITE_WIN;
            System.out.println((whiteTurn ? "Black" : "White") + " wins");
            return true;
        }
        if (line.equals("draw?")) {
            status = Status.DRAW;
            System.out.println("Draw");
            return true;
        }
        String[] words = line.split(" ");
        int[] startPair = atoi(words[0]);
        int startX = startPair[0];
        int startY = startPair[1];
        int[] endPair = atoi(words[1]);
        int endX = endPair[0];
        int endY = endPair[1];
        Move move = new Move(whiteTurn, startX, startY, endX, endY);
        if (words.length == 3) {
            move.setPromotionType(words[2].charAt(0));
        }
        return makeMove(move);
    }

    public static boolean makeMove(Move move) {
        Tile startTile = board.getTile(move.getStartX(), move.getStartY());
        Tile endTile = board.getTile(move.getEndX(), move.getEndY());

        // empty start
        if (startTile.getPiece() == null) {
            return false;
        }

        // valid player
        if (move.isWhite() != whiteTurn) {
            return false;
        }

        // valid start color
        if (startTile.getPiece().isWhite() != whiteTurn) {
            return false;
        }

        // valid move?
        if (!startTile.getPiece().canMove(board, startTile.getX(), startTile.getY(), endTile.getX(), endTile.getY())) {
            return false;
        }

        // under check after move?
        move.make();
        King myKingPiece = whiteTurn ? whiteKingPiece : blackKingPiece;
        int myKingX = whiteTurn ? whiteKingX : blackKingX;
        int myKingY = whiteTurn ? whiteKingY : blackKingY;
        if (myKingPiece.isChecked(board, myKingX, myKingY)) {
            move.unmake();
            return false;
        }
        move.unmake();

        // store the move
        move.make();

        King opponentKingPiece = whiteTurn ? blackKingPiece : whiteKingPiece;
        int opponentKingX = whiteTurn ? blackKingX : whiteKingX;
        int opponentKingY = whiteTurn ? blackKingY : whiteKingY;
        if (opponentKingPiece.isCheckmated(board, opponentKingX, opponentKingY)) { // checkmate
            System.out.println("Checkmate");
            if (whiteTurn) {
                status = Status.WHITE_WIN;
                System.out.println("White wins");
            } else {
                status = Status.BLACK_WIN;
                System.out.println("Black wins");
            }
        } else if (opponentKingPiece.isChecked(board, opponentKingX, opponentKingY)) { // check
            System.out.println("Check");
        } else if (getLegalMoves(!whiteTurn).size() == 0) { // stalemate
            System.out.println("Stalemate");
            status = Status.DRAW;
            System.out.println("Draw");
        }

        // check threefold repetition
        if (positionCount.getOrDefault(hashKey, 0) >= 3) {
            System.out.println("Threefold repetition");
            status = Status.DRAW;
            System.out.println("Draw");
        }

        // check fifty move
        if (checkFiftyMove()) {
            System.out.println("Fifty-move");
            status = Status.DRAW;
            System.out.println("Draw");
        }

        // set the current turn to the other player
        whiteTurn = !whiteTurn;
        currentPlayer = whiteTurn ? players[0] : players[1];

        return true;
    }

    public static Move unmakeMove() {
        if (movesPlayed.size() == 0) {
            return null;
        }
        Move move = movesPlayed.get(movesPlayed.size() - 1);
        move.unmake();
        whiteTurn = !whiteTurn;
        currentPlayer = whiteTurn ? players[0] : players[1];
        return move;
    }

    public static List<Move> getLegalMoves(boolean white) {
        List<Move> moves = new ArrayList<>();
        King myKingPiece = white ? whiteKingPiece : blackKingPiece;
        int myKingX = white ? whiteKingX : blackKingX;
        int myKingY = white ? whiteKingY : blackKingY;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece startPiece = board.getTile(i, j).getPiece();
                if (startPiece != null && startPiece.isWhite() == white) {
                    List<Tile> tiles = startPiece.getAvailableMoves(board, i, j);
                    for (Tile endTile : tiles) {
                        Move move = new Move(white, i, j, endTile.getX(), endTile.getY());
                        move.make();
                        int newKX = (i == myKingX && j == myKingY) ? endTile.getX() : myKingX;
                        int newKY = (i == myKingX && j == myKingY) ? endTile.getY() : myKingY;
                        if (!myKingPiece.isChecked(board, newKX, newKY)) {
                            moves.add(move);
                        }
                        move.unmake();
                    }
                }
            }
        }
        return moves;
    }

    public static boolean checkFiftyMove() {
        Move lastMove = movesPlayed.get(movesPlayed.size() - 1);
        if (lastMove.getPieceKilled() != null || lastMove.getPieceMoved() instanceof Pawn) {
            turn = 0;
        } else {
            turn++;
        }
        return turn >= 100;
    }

    public static void start() {
        while (status == Status.ACTIVE) {
            board.printBoard();
            System.out.println();
            System.out.print((whiteTurn ? "White" : "Black") + "'s move: ");
            while (true) {
                String input = currentPlayer.play();
                if (!(currentPlayer instanceof Human)) {
                    System.out.println(input);
                }
                if (playerMove(input)) {
                    break;
                }
                System.out.println("Illegal move, try again");
                System.out.print((whiteTurn ? "White" : "Black") + "'s move: ");
            }
            System.out.println();
        }
    }

    public static Player choosePlayer(boolean white) {
        System.out.print("Types: Human (0), Random bot (1), Minimax bot (2). Choose " + (white ? "white" : "black") + " player: ");
        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        if (choice == 0) {
            return new Human(white);
        } else if (choice == 1) {
            return new RandomBot(white);
        } else if (choice == 2) {
            System.out.print("Choose max depth: ");
            int maxDepth = scanner.nextInt();
            System.out.print("Choose time limit: ");
            long timeLimit = scanner.nextLong();
            return new MinimaxBot(white, maxDepth, timeLimit);
        } else {
            return null;
        }
    }

    public static String itoa(int x, int y) {
        return (char) (y + 'a') + "" + (8 - x);
    }

    public static int[] atoi(String pos) {
        return new int[]{8 - (pos.charAt(1) - '0'), pos.charAt(0) - 'a'};
    }
}

