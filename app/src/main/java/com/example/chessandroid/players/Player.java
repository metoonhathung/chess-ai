package com.example.chessandroid.players;

import com.example.chessandroid.game.Game;
import com.example.chessandroid.game.Move;

import java.io.Serializable;

public abstract class Player implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean white;

    public Player(boolean white) {
        this.white = white;
    }

    public abstract Move getBestMove();

    public abstract String play();

    public String itoa(Move move) {
        if (move == null) {
            return "draw?"; // stalemate
        } else {
            return Game.itoa(move.getStartX(), move.getStartY()) + " " + Game.itoa(move.getEndX(), move.getEndY());
        }
    }

    public boolean isWhite() {
        return this.white;
    }

    public void setWhite(boolean white) {
        this.white = white;
    }
}
