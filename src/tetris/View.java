package tetris;

import java.awt.Color;

public interface View {
    void drawCell(Cell cell, Color color);
    void clearCell(Cell cell);
    void showScore(int score);
    void showPreviewPiece(Piece piece);
    void beginFlashingLines(int[] rows);
    void endFlashingLines();
}
