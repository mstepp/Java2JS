package tetris;

public class Cell {
    public final int row;
    public final int column;
    public Cell(int _row, int _column) {
	this.row = _row;
	this.column = _column;
    }
    public boolean equals(Object o) {
	if (!(o instanceof Cell))
	    return false;
	Cell c = (Cell)o;
	return c.row == this.row && c.column == this.column;
    }
    public int hashCode() {
	return this.row * 13 + this.column * 11;
    }
    public String toString() {
	return String.format("Cell(%d,%d)", this.row, this.column);
    }
    public Cell add(Cell c) {
	return new Cell(this.row + c.row, this.column + c.column);
    }
}
