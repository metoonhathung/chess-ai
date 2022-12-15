package com.example.chessandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.chessandroid.game.Database;
import com.example.chessandroid.game.Game;
import com.example.chessandroid.game.Match;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class History extends AppCompatActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        setTitle("History");
        Database.storeDir = getFilesDir().toString();
        try {
            Database.matchesPlayed = Database.readMatchesPlayed();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Collections.sort(Database.matchesPlayed, (a, b) -> a.millis == b.millis ? a.title.compareTo(b.title) : (int) (a.millis - b.millis));
        listView = findViewById(R.id.matches_listview);
        ArrayAdapter<Match> adapter = new ArrayAdapter<>(this, R.layout.match, Database.matchesPlayed);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((p, v, pos, id) -> {
            Game.initialize();
            Database.currMovesPlayed = Database.matchesPlayed.get(pos).movesPlayed;
            Database.playbackIndex = 0;
            Intent intent = new Intent(this, Playback.class);
            startActivity(intent);
        });

        findViewById(R.id.play_button).setOnClickListener((view) -> {
            Game.initialize();
            Intent intent = new Intent(this, Play.class);
            startActivity(intent);
        });
    }
}
