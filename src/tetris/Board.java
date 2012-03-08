package tetris;

public class Board {
    public static final int ROWS = 16;
    public static final int COLUMNS = 12;

    private Cell[][] cells;

    public void reset() {
	this.cells = new Cell[ROWS][COLUMNS];
    }

    public boolean canPlace(Cell c) {
	return 0 <= c.row && c.row < ROWS &&
	    0 <= c.column && c.column < COLUMNS;
    }

    public boolean canPlace(Piece p) {
	for (Cell c : p) {
	    if (!this.canPlace(c))
		return false;
	}
	return true;
    }
}
