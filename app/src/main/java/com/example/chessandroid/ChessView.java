package com.example.chessandroid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.chessandroid.game.Game;
import com.example.chessandroid.game.Move;
import com.example.chessandroid.game.Status;
import com.example.chessandroid.game.Tile;
import com.example.chessandroid.pieces.Piece;

import java.util.HashMap;
import java.util.Map;

public class ChessView extends View {
    public ChessDelegate chessDelegate;
    private Paint paint = new Paint();
    private float scaler = 0.9f;
    private float orgX = 0;
    private float orgY = 0;
    private float cellSize = 0;
    private Map<String, Bitmap> map;
    private int startRow = -1;
    private int startCol = -1;
    private float movingX = -1;
    private float movingY = -1;
    private boolean prevClicked = false;

    public ChessView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initMap();
    }

    private void initMap() {
        map = new HashMap<String, Bitmap>() {{
            put("bp", BitmapFactory.decodeResource(getResources(), R.drawable.bp));
            put("bN", BitmapFactory.decodeResource(getResources(), R.drawable.bn));
            put("bB", BitmapFactory.decodeResource(getResources(), R.drawable.bb));
            put("bR", BitmapFactory.decodeResource(getResources(), R.drawable.br));
            put("bQ", BitmapFactory.decodeResource(getResources(), R.drawable.bq));
            put("bK", BitmapFactory.decodeResource(getResources(), R.drawable.bk));
            put("wp", BitmapFactory.decodeResource(getResources(), R.drawable.wp));
            put("wN", BitmapFactory.decodeResource(getResources(), R.drawable.wn));
            put("wB", BitmapFactory.decodeResource(getResources(), R.drawable.wb));
            put("wR", BitmapFactory.decodeResource(getResources(), R.drawable.wr));
            put("wQ", BitmapFactory.decodeResource(getResources(), R.drawable.wq));
            put("wK", BitmapFactory.decodeResource(getResources(), R.drawable.wk));
        }};
    }

    private void initConfig(Canvas canvas) {
        float boardSize = (float) Math.min(canvas.getWidth(), canvas.getHeight()) * scaler;
        cellSize = boardSize / 8;
        orgX = canvas.getWidth() / 2f - boardSize / 2f;
        orgY = canvas.getHeight() / 2f - boardSize / 2f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initConfig(canvas);
        drawBoard(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int smaller = Math.min(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(smaller, smaller);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int col = (int) ((event.getX() - orgX) / cellSize);
        int row = (int) ((event.getY() - orgY) / cellSize);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (row >= 0 && row < 8 && col >= 0 && col < 8) {
                    if (!prevClicked) {
                        startRow = row;
                        startCol = col;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                movingX = event.getX();
                movingY = event.getY();
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                if (row >= 0 && row < 8 && col >= 0 && col < 8) {
                    if (prevClicked || (movingX != -1 && movingY != -1)) { // move
                        Move move = new Move(Game.whiteTurn, startRow, startCol, row, col);
                        Game.makeMove(move);
                        startRow = -1;
                        startCol = -1;
                        movingX = -1;
                        movingY = -1;
                        prevClicked = false;
                        chessDelegate.setUndoClicked(false);
                        invalidate();
                        if (Game.status != Status.ACTIVE) {
                            chessDelegate.showDialog(this);
                        }
                    } else { //click
                        prevClicked = true;
                    }
                }
                break;
            default:
                ;
        }

        return true;
    }

    private void drawBoard(Canvas canvas) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                paint.setColor((i + j) % 2 == 0 ? Color.LTGRAY : Color.DKGRAY);
                canvas.drawRect(orgX + j * cellSize, orgY + i * cellSize, orgX + (j + 1) * cellSize, orgY + (i + 1) * cellSize, paint);
                Piece piece = Game.board.getTile(i, j).getPiece();
                if (piece != null && (i != startRow || j != startCol)) {
                    canvas.drawBitmap(map.get(piece.getName()), null, new RectF(orgX + j * cellSize, orgY + i * cellSize, orgX + (j + 1) * cellSize, orgY + (i + 1) * cellSize), paint);
                }
            }
        }
        if (startRow != -1 && startCol != -1 && movingX != -1 && movingY != -1) {
            Piece movingPiece = Game.board.getTile(startRow, startCol).getPiece();
            if (movingPiece != null) {
                canvas.drawBitmap(map.get(movingPiece.getName()), null, new RectF(movingX - cellSize / 2, movingY - cellSize / 2, movingX + cellSize / 2, movingY + cellSize / 2), paint);
            }
        }
    }
}
