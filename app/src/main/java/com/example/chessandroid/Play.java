package com.example.chessandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.chessandroid.game.Database;
import com.example.chessandroid.game.Game;
import com.example.chessandroid.game.Match;
import com.example.chessandroid.game.Move;
import com.example.chessandroid.game.Status;
import com.example.chessandroid.players.MinimaxBot;
import com.example.chessandroid.players.Player;

public class Play extends AppCompatActivity implements ChessDelegate {

    private boolean undoClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        setTitle("Play");

        Player whitePlayer = new MinimaxBot(true, 5, 250);
        Player blackPlayer = new MinimaxBot(false, 5, 250);

        ChessView chessView = findViewById(R.id.chess_view_play);
        chessView.chessDelegate = this;

        findViewById(R.id.undo_button).setOnClickListener((view) -> {
            if (!undoClicked) {
                Game.unmakeMove();
                undoClicked = true;
                chessView.invalidate();
            }
        });

        findViewById(R.id.ai_button).setOnClickListener((view) -> {
            Move move = Game.whiteTurn ? whitePlayer.getBestMove() : blackPlayer.getBestMove();
            if (move != null) {
                Game.makeMove(move);
                undoClicked = false;
                chessView.invalidate();
                if (Game.status != Status.ACTIVE) {
                    showDialog(chessView);
                }
            }
        });

        findViewById(R.id.draw_button).setOnClickListener((view) -> {
            Game.status = Status.DRAW;
            showDialog(chessView);
        });

        findViewById(R.id.resign_button).setOnClickListener((view) -> {
            Game.status = Game.whiteTurn ? Status.BLACK_WIN : Status.WHITE_WIN;
            showDialog(chessView);
        });
    }

    public void showDialog(View view) {
        String prefix = "";
        if (Game.status == Status.WHITE_WIN) {
            prefix = "White wins. ";
        } else if (Game.status == Status.BLACK_WIN) {
            prefix = "Black wins. ";
        } else if (Game.status == Status.DRAW) {
            prefix = "Draw. ";
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(prefix + "Which name do you want to save the game?");
        EditText input = new EditText(this);
        builder.setView(input);
        builder.setPositiveButton("OK", (dialog, id) -> {
            String value = input.getText().toString().trim();
            Database.matchesPlayed.add(new Match(Game.movesPlayed, System.currentTimeMillis(), value));
            try {
                Database.writeMatchesPlayed(Database.matchesPlayed);
            } catch (Exception e) {
                e.printStackTrace();
            }
            finish();
        });
        builder.setNegativeButton("Cancel", (dialog, id) -> {
            finish();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void setUndoClicked(boolean undoClicked) {
        this.undoClicked = undoClicked;
    }
}
