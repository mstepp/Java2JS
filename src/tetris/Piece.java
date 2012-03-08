package tetris;

import java.util.*;
import java.awt.Color;

public class Piece implements Iterable<Cell> {
    private final Cell[] offsets;
    private Cell center;
    private Color color;
    private List<Cell> currentPieces = null;

    public Piece(Cell[] _offsets, Cell _center, Color _color) {
	this.offsets = _offsets.clone();
	this.center = _center;
	this.color = _color;
    }
    public Iterator<Cell> iterator() {
	if (this.currentPieces == null) {
	    List<Cell> cells = new ArrayList<Cell>();
	    for (Cell offset : this.offsets) {
		cells.add(this.center.add(offset));
	    }
	    this.currentPieces = Collections.unmodifiableList(cells);
	}
	return currentPieces.iterator();
    }
    public Cell getCenter() {return this.center;}
    public Color getColor() {return this.color;}

    public void rotate(boolean clockwise) {
	this.currentPieces = null;
	if (clockwise) {
	    for (int i = 0; i < this.offsets.length; i++) {
		this.offsets[i] = new Cell(this.offsets[i].column, -this.offsets[i].row);
	    }
	} else {
	    for (int i = 0; i < this.offsets.length; i++) {
		this.offsets[i] = new Cell(-this.offsets[i].column, this.offsets[i].row);
	    }
	}
    }

    public Piece clone() {
	return new Piece(this.offsets, this.center, this.color);
    }
}
