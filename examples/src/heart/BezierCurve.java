package heart;

import java.awt.*;
import java.util.*;

/** Given control points (x0,y0), (x1,y1), ..., (xN,yN),
    the Bezier curve is defined as follows:

    n
    x(t) = sum  xi * B(n,i,t)
    i=0

    n
    y(t) = sum  yi * B(n,i,t)
    i=0

    where B(n,i,t) is the Bernstein polynomial, defined by

    B(n,i,t) = nCr(n,i) * t^i * (1.0-t)^(n-i)

    where t is in [0,1].


    This implementation keeps the whole computation in the integers,
    by using only rational t values. When this is done, we can rewrite t as
    num/denom for some integers num and denom. We rewrite the calculation as follows:

    n
    x(num,denom) = sum  xi * B(n,i,num,denom)
    i=0
    --------------------------
    denom^n


    n
    y(num,denom) = sum  yi * B(n,i,num,denom)
    i=0
    --------------------------
    denom^n


    where B(n,i,num,denom) is defined as

    B(n,i,num,denom) = nCr(n,i) * num^i * (denom-num)^(n-i)
*/
public class BezierCurve {
   private final java.util.List<Point> points;
   private int tStep;

   public BezierCurve() {
      this.points = new ArrayList<Point>();
      this.tStep = 100;
   }

   public int getTStep() {return this.tStep;}
   public void setTStep(int t) {
      if (t <= 0)
         throw new IllegalArgumentException("t-step must be positive");
      this.tStep = t;
   }

   public void addPoint(Point p) {
      if (p == null)
         throw new NullPointerException();
      this.points.add(p);
   }
   public int getNumPoints() {return this.points.size();}
   public Point removePoint(int index) {return this.points.remove(index);}
   public Point getPoint(int index) {return this.points.get(index);}

   public Iterable<? extends Point> getPoints() {
      return Collections.unmodifiableList(this.points);
   }

   private long nCr(int n, int r) {
      long result = 1L;
      for (int i = 0; i < r; i++) {
         result *= n-i;
         result /= i+1;
      }
      return result;
   }

   private long bernstein(int n, int i, int num, int denom){
      // result = nCr(n,i) * num^i * (denom-num)^(n-i)
      long nCi = nCr(n,i);
      long NUM = num;
      long DENOM_NUM = denom-num;
      for (int j = 1; j <= n; j++) {
         if (j<=i) nCi *= NUM;
         else nCi *= DENOM_NUM;
      }
      return nCi;
   }


   private Point evaluate(int num, int denom){
      int n = points.size()-1;

      long x = 0L;
      long y = 0L;
      long denomN = 1L;
      long DENOM = denom;
      for (int i = 0; i <= n; i++) {
         Point point = points.get(i);
         long Bt = bernstein(n, i, num, denom);
         x += point.x * Bt;
         y += point.y * Bt;
         // skip one, else get denom^(n+1)
         if (i!=0)
            denomN *= DENOM;
      }
      //          n
      // now x = sum   xi * B(n,i,num,denom)
      //         i=0
      //
      //          n
      // and y = sum   yi * B(n,i,num,denom)
      //         i=0

      x /= denomN;
      y /= denomN;
      return new Point((int)x, (int)y);
   }

   public void draw(Animator a) {
      if (this.points.size() < 2)
         return;
      draw(a, 0, null);
   }

   private void draw(Animator a, int i, Point last) {
      int count = 0;
      for (; i<=this.tStep && count<10; i++,count++){
         Point p = evaluate(i,this.tStep);
         if (last != null) {
            a.drawLine(last.x, last.y, p.x, p.y);
         }
         last = p;
      }
	
      continuation(20, a, i, last);
   }

   private native void continuation(int sleep, Animator a, int i, Point last);
}
