package heart;

import java.awt.*;
import java.util.*;
import java.math.*;

public class bezier {
   private static final int WIDTH = 500;
   private static final int HEIGHT = 500;
   private final java.util.List<BezierCurve> curves;
   private boolean drawing;
   private final Animator animator;

   public bezier(String id) {
      this.drawing = false;
      this.curves = new ArrayList<BezierCurve>();

      int by = 8*HEIGHT/10;
      int mx = WIDTH/2;

      this.animator = new Animator(id);

      buildSierpinski(6, new Point(1*WIDTH/10, by), new Point(WIDTH/2, 2*HEIGHT/10), new Point(9*WIDTH/10, by));
   }

   private void buildSierpinski(int depth, Point left, Point top, Point right) {
      if (depth==0)
         return;

      Point midL = new Point((left.x+top.x)/2,
                             (left.y+top.y)/2);
      Point midR = new Point((right.x+top.x)/2,
                             (right.y+top.y)/2);
      Point bot = new Point((left.x+right.x)/2,
                            (left.y+right.y)/2);

      BezierCurve lc = new BezierCurve();
      BezierCurve rc = new BezierCurve();

      double dist = (double)(bot.y-top.y);

      makeHeartHalf(bot.x, bot.y, dist*0.027, true, false, lc);
      makeHeartHalf(bot.x, bot.y, dist*0.027, false, false, rc);

      lc.setTStep(100);
      rc.setTStep(100);

      this.curves.add(lc);
      this.curves.add(rc);

      buildSierpinski(depth-1, left, midL, bot);
      buildSierpinski(depth-1, midL, top, midR);
      buildSierpinski(depth-1, bot, midR, right);
   }

   private void makeHeartHalf(int mx, int by, double size, boolean left, boolean upsideDown, BezierCurve curve) {
      double hmult = (left ? -1.0 : 1.0);
      double vmult = (upsideDown ? 1.0 : -1.0);

      curve.addPoint(new Point(mx, by));
      curve.addPoint(new Point((int)(mx + hmult*9.0*size), (int)(by + vmult*15.0*size)));
      curve.addPoint(new Point((int)(mx + hmult*9.0*size), (int)(by + vmult*17.0*size)));
      curve.addPoint(new Point((int)(mx + hmult*4.0*size), (int)(by + vmult*22.0*size)));
      curve.addPoint(new Point(mx, (int)(by + vmult*17.0*size)));
      curve.addPoint(new Point(mx, (int)(by + vmult*14.0*size)));
   }

   public void redraw() {
      animator.clear();
      animator.setColor(255, 0, 0);
       
      for (BezierCurve curve : curves) {
         curve.draw(animator);
      }
   }
}
