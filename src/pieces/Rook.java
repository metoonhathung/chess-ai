package pieces;

import game.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Rook extends Piece implements Serializable {
    private static final long serialVersionUID = 1L;

    public Rook(boolean white, String name) {
        super(white, name);
    }

    public List<Tile> getAvailableMoves(Board board, int x, int y) {
        List<Tile> availableMoves = new ArrayList<>();
        availableMoves.addAll(this.getLinearMoves(board, x, y));
        return availableMoves;
    }
}
