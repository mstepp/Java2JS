package tetris3d;

/* 
   Assumption is that all points are in clockwise orientation!!
   and that face is convex
*/
// parent = parent shape
// points = [int]xN for N>=3, indexes into parent shape points list
// color = {red:float, green:float, blue:float}

public class Face {
   public final Shape parent;
   public final int[] points;
   public Color color;
   public final double[] normal;
   public final double[][] transformed;

   public Face(Shape parent, int[] points, Color color) {
      this.parent = parent;
      this.points = points;
      this.normal = Util.newVector(3);
      this.color = color;
      this.transformed = new double[this.points.length][];
      for (int i = 0; i < this.points.length; i++) {
         this.transformed[i] = Util.newVector(4);
      }
   }

   /**
    * This function assumes the shape's points are un-homo'ed.
    */
   public void compute_Normal() {
      double[] v1 = Util.newVector(3);
      double[] v2 = Util.newVector(3);
      
      Util.subtract(this.parent.points[this.points[0]], this.parent.points[this.points[1]], v1);
      Util.subtract(this.parent.points[this.points[2]], this.parent.points[this.points[1]], v2);
      
      Util.cross(v1, v2, this.normal);
      Util.normalize(this.normal, this.normal);
   }

   /**
    * Returns a list of references to this face's points, taken from the
    * parent shape's list of points.
    */
   public double[][] makePointList() {
      double[][] result = new double[this.points.length][];
      for (int i = 0; i < this.points.length; i++) {
         result[i] = this.parent.points[this.points[i]];
      }
      return result;
   }
}
   