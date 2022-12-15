package com.example.chessandroid.players;

import com.example.chessandroid.game.Move;

import java.io.Serializable;
import java.util.Scanner;

public class Human extends Player implements Serializable {
    private static final long serialVersionUID = 1L;

    public Human(boolean white) {
        super(white);
    }

    @Override
    public Move getBestMove() {
        return null;
    }

    @Override
    public String play() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }
}
