package com.example.chessandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.chessandroid.game.Database;
import com.example.chessandroid.game.Game;
import com.example.chessandroid.game.Move;

public class Playback extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playback);
        setTitle("Playback");

        ChessView chessView = findViewById(R.id.chess_view_playback);
        chessView.setOnTouchListener((v, me) -> true);

        findViewById(R.id.next_button).setOnClickListener((view) -> {
            if (Database.playbackIndex < Database.currMovesPlayed.size()) {
                Move currMove = Database.currMovesPlayed.get(Database.playbackIndex);
                Game.makeMove(currMove);
                Database.playbackIndex++;
                chessView.invalidate();
            }
        });

        findViewById(R.id.prev_button).setOnClickListener((view) -> {
            if (Database.playbackIndex > 0) {
                Game.unmakeMove();
                Database.playbackIndex--;
                chessView.invalidate();
            }
        });
    }
}
