package com.example.chessandroid.players;

import com.example.chessandroid.game.*;
import com.example.chessandroid.pieces.*;

import java.io.Serializable;
import java.util.*;

enum Flag {
    EXACT_VALUE,
    LOWER_BOUND,
    UPPER_BOUND
}

class MoveStats implements Serializable {
    private static final long serialVersionUID = 1L;

    private Move move;
    private int score;
    private int depth;
    private Flag flag;


    public MoveStats(Move move, int score, int depth, Flag flag) {
        this.move = move;
        this.score = score;
        this.depth = depth;
        this.flag = flag;
    }

    public Move getMove() {
        return move;
    }

    public void setMove(Move move) {
        this.move = move;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public Flag getFlag() {
        return flag;
    }

    public void setFlag(Flag flag) {
        this.flag = flag;
    }
}

public class MinimaxBot extends Player implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Map<Long, MoveStats> transpositionTable = new HashMap<>();

    private final Map<Character, Integer> materialScore = new HashMap<Character, Integer>() {{
        put('p', 100);
        put('N', 320);
        put('B', 330);
        put('R', 500);
        put('Q', 900);
        put('K', 20000);
    }};

    private final int[][] whitePawnTable = new int[][]{
            {0, 0, 0, 0, 0, 0, 0, 0},
            {50, 50, 50, 50, 50, 50, 50, 50},
            {10, 10, 20, 30, 30, 20, 10, 10},
            {5, 5, 10, 25, 25, 10, 5, 5},
            {0, 0, 0, 20, 20, 0, 0, 0},
            {5, -5, -10, 0, 0, -10, -5, 5},
            {5, 10, 10, -20, -20, 10, 10, 5},
            {0, 0, 0, 0, 0, 0, 0, 0}
    };

    private final int[][] blackPawnTable = mirror(whitePawnTable);

    private final int[][] whiteKnightTable = new int[][]{
            {-50, -40, -30, -30, -30, -30, -40, -50},
            {-40, -20, 0, 0, 0, 0, -20, -40},
            {-30, 0, 10, 15, 15, 10, 0, -30},
            {-30, 5, 15, 20, 20, 15, 5, -30},
            {-30, 0, 15, 20, 20, 15, 0, -30},
            {-30, 5, 10, 15, 15, 10, 5, -30},
            {-40, -20, 0, 5, 5, 0, -20, -40},
            {-50, -40, -30, -30, -30, -30, -40, -50},
    };

    private final int[][] blackKnightTable = mirror(whiteKnightTable);

    private final int[][] whiteBishopTable = new int[][]{
            {-20, -10, -10, -10, -10, -10, -10, -20},
            {-10, 0, 0, 0, 0, 0, 0, -10},
            {-10, 0, 5, 10, 10, 5, 0, -10},
            {-10, 5, 5, 10, 10, 5, 5, -10},
            {-10, 0, 10, 10, 10, 10, 0, -10},
            {-10, 10, 10, 10, 10, 10, 10, -10},
            {-10, 5, 0, 0, 0, 0, 5, -10},
            {-20, -10, -10, -10, -10, -10, -10, -20},
    };

    private final int[][] blackBishopTable = mirror(whiteBishopTable);

    private final int[][] whiteRookTable = new int[][]{
            {0, 0, 0, 0, 0, 0, 0, 0},
            {5, 10, 10, 10, 10, 10, 10, 5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {0, 0, 0, 5, 5, 0, 0, 0}
    };

    private final int[][] blackRookTable = mirror(whiteRookTable);

    private final int[][] whiteQueenTable = new int[][]{
            {-20, -10, -10, -5, -5, -10, -10, -20},
            {-10, 0, 0, 0, 0, 0, 0, -10},
            {-10, 0, 5, 5, 5, 5, 0, -10},
            {-5, 0, 5, 5, 5, 5, 0, -5},
            {0, 0, 5, 5, 5, 5, 0, -5},
            {-10, 5, 5, 5, 5, 5, 0, -10},
            {-10, 0, 5, 0, 0, 0, 0, -10},
            {-20, -10, -10, -5, -5, -10, -10, -20}
    };

    private final int[][] blackQueenTable = mirror(whiteQueenTable);

    private final int[][] whiteKingMiddleTable = new int[][]{
            {-30, -40, -40, -50, -50, -40, -40, -30},
            {-30, -40, -40, -50, -50, -40, -40, -30},
            {-30, -40, -40, -50, -50, -40, -40, -30},
            {-30, -40, -40, -50, -50, -40, -40, -30},
            {-20, -30, -30, -40, -40, -30, -30, -20},
            {-10, -20, -20, -20, -20, -20, -20, -10},
            {20, 20, 0, 0, 0, 0, 20, 20},
            {20, 30, 10, 0, 0, 10, 30, 20}
    };

    private final int[][] blackKingMiddleTable = mirror(whiteKingMiddleTable);

    private final int[][] whiteKingEndTable = new int[][]{
            {-50, -40, -30, -20, -20, -30, -40, -50},
            {-30, -20, -10, 0, 0, -10, -20, -30},
            {-30, -10, 20, 30, 30, 20, -10, -30},
            {-30, -10, 30, 40, 40, 30, -10, -30},
            {-30, -10, 30, 40, 40, 30, -10, -30},
            {-30, -10, 20, 30, 30, 20, -10, -30},
            {-30, -30, 0, 0, 0, 0, -30, -30},
            {-50, -30, -30, -30, -30, -30, -30, -50}
    };

    private final int[][] blackKingEndTable = mirror(whiteKingEndTable);

    private final int maxDepth;
    private final long timeLimit;
    private long startTime;
    private boolean timeOut;

    private final Move[][] killers;

    public MinimaxBot(boolean white, int maxDepth, long timeLimit) {
        super(white);
        this.maxDepth = maxDepth;
        this.timeLimit = timeLimit;
        this.killers = new Move[maxDepth + 1][2];
    }

    public int[][] mirror(int[][] matrix) {
        int[][] mirrored = new int[matrix.length][matrix[0].length];
        for (int i = 0; i < matrix.length; i++) {
            mirrored[i] = matrix[matrix.length - i - 1];
        }
        return mirrored;
    }

    @Override
    public Move getBestMove() {
        iterativeDeepening();
        MoveStats moveStats = transpositionTable.getOrDefault(Game.hashKey, null);
        Move move = moveStats != null ? moveStats.getMove() : null;
        return move;
    }

    @Override
    public String play() {
        return itoa(getBestMove());
    }

    public void iterativeDeepening() {
        timeOut = false;
        startTime = System.currentTimeMillis();
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        int windowSize = 50; // aspiration window
        int depth = 1;
        while (depth <= maxDepth) {
            int value = minimax(depth, isWhite(), alpha, beta);
            if (timeOut) break;
            if (value <= alpha) { // fail low
                alpha = value - 100;
                continue; // redo
            }
            if (value >= beta) { // fail high
                beta = value + 100;
                continue; // redo
            }
            alpha = value - windowSize;
            beta = value + windowSize;
            depth++;
        }
    }

    public int minimax(int depth, boolean white, int alpha, int beta) {
        // time out
        if (System.currentTimeMillis() - startTime >= timeLimit) {
            timeOut = true;
            return alpha;
        }
        // transposition table lookup
        int alphaOrig = alpha;
        MoveStats moveStats = transpositionTable.getOrDefault(Game.hashKey, null);
        if (moveStats != null && moveStats.getDepth() >= depth) {
            if (moveStats.getFlag() == Flag.EXACT_VALUE) {
                return moveStats.getScore();
            } else if (moveStats.getFlag() == Flag.LOWER_BOUND) {
                alpha = Math.max(alpha, moveStats.getScore());
            } else if (moveStats.getFlag() == Flag.UPPER_BOUND) {
                beta = Math.min(beta, moveStats.getScore());
            }
            if (alpha >= beta) {
                return moveStats.getScore();
            }
        }
        // minimax
        if (depth == 0) {
            return evaluate();
        }
        List<Move> moves = getOrderedMoves(white, depth, moveStats);
        int best;
        Move bestMove = moves.size() == 0 ? null : moves.get(0);
        if (white == isWhite()) { // max player
            best = Integer.MIN_VALUE;
            for (Move move : moves) {
                if (move == null) continue;
                move.make();
                int value;
                if (Game.positionCount.getOrDefault(Game.hashKey, 0) == 1) {
                    value = minimax(depth - 1, !white, alpha, beta);
                } else {
                    value = Integer.MIN_VALUE; // acyclic
                }
                move.unmake();
                if (value > best) {
                    best = value;
                    bestMove = move;
                }
                alpha = Math.max(alpha, best);
                if (alpha >= beta) { // beta cutoff
                    // non capture killer move
                    if (move.getPieceKilled() == null) {
                        if (!move.equals(killers[depth][0])) {
                            killers[depth][1] = killers[depth][0];
                            killers[depth][0] = move;
                        }
                    }
                    break;
                }
            }
        } else { // min player
            best = Integer.MAX_VALUE;
            for (Move move : moves) {
                if (move == null) continue;
                move.make();
                int value;
                if (Game.positionCount.getOrDefault(Game.hashKey, 0) == 1) {
                    value = minimax(depth - 1, !white, alpha, beta);
                } else {
                    value = Integer.MAX_VALUE; // acyclic
                }
                move.unmake();
                if (value < best) {
                    best = value;
                    bestMove = move;
                }
                beta = Math.min(beta, best);
                if (beta <= alpha) { // alpha cutoff
                    break;
                }
            }
        }
        if (timeOut) return best; // time out
        // transition table store
        Flag flag;
        if (best <= alphaOrig) {
            flag = Flag.UPPER_BOUND; // all-node
        } else if (best >= beta) {
            flag = Flag.LOWER_BOUND; // cut-node
        } else {
            flag = Flag.EXACT_VALUE; // pv-node
        }
        // always replace
        transpositionTable.put(Game.hashKey, new MoveStats(bestMove, best, depth, flag));
        return best;
    }

    public List<Move> getOrderedMoves(boolean white, int depth, MoveStats moveStats) {
        List<Move> moves = Game.getLegalMoves(white);
        List<Move> captureMoves = new ArrayList<>();
        List<Move> killerMoves = new ArrayList<>();
        List<Move> quietMoves = new ArrayList<>();
        for (Move move : moves) {
            if (moveStats != null && move.equals(moveStats.getMove())) {
                continue;
            } else if (move.getPieceKilled() != null) {
                captureMoves.add(move);
            } else if (move.equals(killers[depth][0]) || move.equals(killers[depth][1])) {
                killerMoves.add(move);
            } else {
                quietMoves.add(move);
            }
        }
        // mvv-lva
        captureMoves.sort((m1, m2) -> {
            int victim1 = materialScore.get(m1.getPieceKilled().getName().charAt(1));
            int victim2 = materialScore.get(m2.getPieceKilled().getName().charAt(1));
            if (victim1 != victim2) {
                return victim2 - victim1;
            } else {
                int aggressor1 = materialScore.get(m1.getPieceMoved().getName().charAt(1));
                int aggressor2 = materialScore.get(m2.getPieceMoved().getName().charAt(1));
                return aggressor1 - aggressor2;
            }
        });
        moves.clear();
        if (moveStats != null) moves.add(moveStats.getMove());
        moves.addAll(captureMoves);
        moves.addAll(killerMoves);
        moves.addAll(quietMoves);
        return moves;
    }

    public int evaluate() {
        int whiteMaterial = 0;
        int whitePosition = 0;
        int blackMaterial = 0;
        int blackPosition = 0;
        int[] kingPos = new int[]{-1, -1, -1, -1};
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = Game.board.getTile(i, j).getPiece();
                if (piece != null) {
                    if (piece instanceof Pawn) {
                        if (piece.isWhite()) {
                            whiteMaterial += materialScore.get('p');
                            whitePosition += whitePawnTable[i][j];
                        } else {
                            blackMaterial += materialScore.get('p');
                            ;
                            blackPosition += blackPawnTable[i][j];
                        }
                    } else if (piece instanceof Knight) {
                        if (piece.isWhite()) {
                            whiteMaterial += materialScore.get('N');
                            ;
                            whitePosition += whiteKnightTable[i][j];
                        } else {
                            blackMaterial += materialScore.get('N');
                            ;
                            blackPosition += blackKnightTable[i][j];
                        }
                    } else if (piece instanceof Bishop) {
                        if (piece.isWhite()) {
                            whiteMaterial += materialScore.get('B');
                            whitePosition += whiteBishopTable[i][j];
                        } else {
                            blackMaterial += materialScore.get('B');
                            blackPosition += blackBishopTable[i][j];
                        }
                    } else if (piece instanceof Rook) {
                        if (piece.isWhite()) {
                            whiteMaterial += materialScore.get('R');
                            whitePosition += whiteRookTable[i][j];
                        } else {
                            blackMaterial += materialScore.get('R');
                            blackPosition += blackRookTable[i][j];
                        }
                    } else if (piece instanceof Queen) {
                        if (piece.isWhite()) {
                            whiteMaterial += materialScore.get('Q');
                            whitePosition += whiteQueenTable[i][j];
                        } else {
                            blackMaterial += materialScore.get('Q');
                            blackPosition += blackQueenTable[i][j];
                        }
                    } else if (piece instanceof King) {
                        if (piece.isWhite()) {
                            whiteMaterial += materialScore.get('K');
                            kingPos[0] = i;
                            kingPos[1] = j;
                        } else {
                            blackMaterial += materialScore.get('K');
                            kingPos[2] = i;
                            kingPos[3] = j;
                        }
                    }
                }
            }
        }
        if (whiteMaterial <= 21330 && blackMaterial <= 21330) {
            if (kingPos[0] != -1 && kingPos[1] != -1) {
                whitePosition += whiteKingEndTable[kingPos[0]][kingPos[1]];
            }
            if (kingPos[2] != -1 && kingPos[3] != -1) {
                blackPosition += blackKingEndTable[kingPos[2]][kingPos[3]];
            }
        } else {
            if (kingPos[0] != -1 && kingPos[1] != -1) {
                whitePosition += whiteKingMiddleTable[kingPos[0]][kingPos[1]];
            }
            if (kingPos[2] != -1 && kingPos[3] != -1) {
                blackPosition += blackKingMiddleTable[kingPos[2]][kingPos[3]];
            }
        }
        int whiteScore = whiteMaterial + whitePosition;
        int blackScore = blackMaterial + blackPosition;
        return isWhite() ? whiteScore - blackScore : blackScore - whiteScore;
    }
}
