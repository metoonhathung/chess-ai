package com.example.chessandroid;

import android.view.View;

public interface ChessDelegate {
    public void showDialog(View view);

    public void setUndoClicked(boolean undoClicked);
}
