package com.example.chessandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.chessandroid.game.Database;
import com.example.chessandroid.game.Game;
import com.example.chessandroid.game.Match;
import com.example.chessandroid.game.Move;
import com.example.chessandroid.game.Status;
import com.example.chessandroid.players.MinimaxBot;
import com.example.chessandroid.players.Player;

import io.socket.client.IO;
import io.socket.client.Socket;

public class Play extends AppCompatActivity implements ChessDelegate {

    private boolean undoClicked = false;
    private String roomName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        setTitle("Play");

        Player whitePlayer = new MinimaxBot(true, 5, 250);
        Player blackPlayer = new MinimaxBot(false, 5, 250);

        TextView textView = findViewById(R.id.info_textview);
        ChessView chessView = findViewById(R.id.chess_view_play);
        chessView.chessDelegate = this;

        Intent intent = getIntent();
        if (intent.hasExtra("room_name")) {
            Bundle bundle = intent.getExtras();
            roomName = bundle.getString("room_name");
        }

        try {
            Database.mSocket = IO.socket("https://chess-socketio.onrender.com"); // http://10.0.2.2:3000
        } catch (Exception e) {
            e.printStackTrace();
        }

        Database.mSocket.connect();

        Database.mSocket.on(Socket.EVENT_CONNECT, args -> {
            Database.mSocket.emit("join", roomName);
        });

        Database.mSocket.on(Socket.EVENT_CONNECT_ERROR, args -> {
            Log.d("SOCKETIO", args[0].toString());
        });

        Database.mSocket.on("join", (args) -> {
            int total = (int)args[0];
            runOnUiThread(() -> {
                textView.setText("A player joined room "  + roomName + " (total: " + total + ")");
            });
        });

        Database.mSocket.on("leave", (args) -> {
            int total = (int)args[0];
            runOnUiThread(() -> {
                textView.setText("A player left room "  + roomName + " (total: " + total + ")");
            });
        });

        Database.mSocket.on("make_move", (args) -> {
            String[] details = ((String)args[0]).split(" ");
            boolean white = details[0].equals("white");
            int[] startPos = Game.atoi(details[1]);
            int[] endPos = Game.atoi(details[2]);
            Move move = new Move(white, startPos[0], startPos[1], endPos[0], endPos[1]);
            Game.makeMove(move);
            runOnUiThread(() -> {
                chessView.invalidate();
                if (Game.status != Status.ACTIVE) {
                    showDialog(chessView);
                }
            });
        });

        Database.mSocket.on("unmake_move", (args) -> {
            Game.unmakeMove();
            runOnUiThread(() -> {
                chessView.invalidate();
            });
        });

        Database.mSocket.on("draw", (args) -> {
            Game.status = Status.DRAW;
            runOnUiThread(() -> {
                showDialog(chessView);
            });
        });

        Database.mSocket.on("resign", (args) -> {
            Game.status = Game.whiteTurn ? Status.BLACK_WIN : Status.WHITE_WIN;
            runOnUiThread(() -> {
                showDialog(chessView);
            });
        });

        findViewById(R.id.undo_button).setOnClickListener((view) -> {
            if (!undoClicked) {
                Game.unmakeMove();
                undoClicked = true;
                chessView.invalidate();
                Database.mSocket.emit("unmake_move");
            }
        });

        findViewById(R.id.ai_button).setOnClickListener((view) -> {
            Move move = Game.whiteTurn ? whitePlayer.getBestMove() : blackPlayer.getBestMove();
            if (move != null) {
                Game.makeMove(move);
                undoClicked = false;
                chessView.invalidate();
                Database.mSocket.emit("make_move", (move.isWhite() ? "white" : "black") + " " + Game.itoa(move.getStartX(), move.getStartY()) + " " + Game.itoa(move.getEndX(), move.getEndY()));
                if (Game.status != Status.ACTIVE) {
                    showDialog(chessView);
                }
            }
        });

        findViewById(R.id.draw_button).setOnClickListener((view) -> {
            Game.status = Status.DRAW;
            Database.mSocket.emit("draw");
            showDialog(chessView);
        });

        findViewById(R.id.resign_button).setOnClickListener((view) -> {
            Game.status = Game.whiteTurn ? Status.BLACK_WIN : Status.WHITE_WIN;
            Database.mSocket.emit("resign");
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Database.mSocket.emit("leave", roomName);
        Database.mSocket.disconnect();
    }
}
