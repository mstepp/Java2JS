package heart;

public class CFractal {
    private static final int X_START = 250;
    private static final int Y_TOP = (500 - 128) / 2;
    private static final int Y_BOTTOM = Y_TOP + 128;

    private final Animator animator;
    
    public CFractal(Animator _animator) {
	this.animator = _animator;
    }

    public void run(int depth) {
	fractal(depth, X_START, Y_TOP, X_START, Y_BOTTOM);
    }

    private void fractal(int depth, int x1, int y1, int x2, int y2) {
	if (depth == 0) {
	    this.animator.drawLine(x1, y1, x2, y2);
	} else {
	    int midx = (x1 + x2) / 2;
	    int midy = (y1 + y2) / 2;
	    int newx = midx - (midy - y1);
	    int newy = midy - (x1 - midx);
	    fractal(depth - 1, x1, y1, newx, newy);
	    fractal(depth - 1, newx, newy, x2, y2);
	}
    }
}
