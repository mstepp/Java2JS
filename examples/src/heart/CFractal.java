package heart;

public class CFractal {
   private static final int X_START = 250;
   private static final int Y_TOP = (500 - 128) / 2;
   private static final int Y_BOTTOM = Y_TOP + 128;
   private static final int DELAY = 20;

   private final Animator animator;
    
   public CFractal(Animator _animator) {
      this.animator = _animator;
   }

   public void run(int depth) {
      animator.setTimeout(fractal(depth, X_START, Y_TOP, X_START, Y_BOTTOM, null), DELAY);
   }

   private Runnable fractal(final int depth, final int x1, final int y1, final int x2, final int y2, final Runnable cont) {
      return new Runnable() {
         public void run() {
            if (depth == 0) {
               animator.drawLine(x1, y1, x2, y2);
               if (cont != null)
                  animator.setTimeout(cont, DELAY);
            } else {
               int midx = (x1 + x2) / 2;
               int midy = (y1 + y2) / 2;
               int newx = midx - (midy - y1);
               int newy = midy - (x1 - midx);
               Runnable cont2 = fractal(depth - 1, x1, y1, newx, newy, cont);
               Runnable cont3 = fractal(depth - 1, newx, newy, x2, y2, cont2);
               animator.setTimeout(cont3, DELAY);
            }
         }
      };
   }
}

