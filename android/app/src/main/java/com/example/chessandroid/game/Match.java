package com.example.chessandroid.game;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Match implements Serializable {
    private static final long serialVersionUID = 1L;

    public List<Move> movesPlayed;
    public long millis;
    public String title;

    public Match(List<Move> movesPlayed, long millis, String title) {
        this.movesPlayed = new ArrayList<>(movesPlayed);
        this.millis = millis;
        this.title = title;
    }

    @Override
    public String toString() {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.of("America/New_York"));
        String formattedDate = localDateTime.format(DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss"));
        return title + " - " + formattedDate;
    }
}
