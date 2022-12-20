package com.example.chessandroid.players;

import com.example.chessandroid.game.*;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

public class RandomBot extends Player implements Serializable {
    private static final long serialVersionUID = 1L;

    public RandomBot(boolean white) {
        super(white);
    }

    @Override
    public Move getBestMove() {
        Random random = new Random();
        List<Move> moves = Game.getLegalMoves(isWhite());
        Move move = moves.size() > 0 ? moves.get(random.nextInt(moves.size())) : null;
        return move;
    }

    @Override
    public String play() {
        return itoa(getBestMove());
    }
}
