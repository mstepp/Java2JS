package tetris3d;

import java.util.*;

public class Shape {
   public final double[][] points;
   public final List<Face> faces;

   public Shape(double[][] points) {
      this.points = points;
      this.faces = new ArrayList<Face>();
   }

   // points = [int], list of indexes into this points list
   public Face addFace(int[] points, Color color) {
      Face face = new Face(this, points, color!=null ? color : new Color(0,0,0));
      face.compute_Normal();
      this.faces.add(face);
      return face;
   }

   public static Shape make_RectShape(double[] bottom, double[] top, Color color) {
      {
         double[] mid = Util.newVector(3);
         double[] dist = Util.newVector(3);
         mid[0] = (bottom[0]+top[0])/2.0;
         mid[1] = (bottom[1]+top[1])/2.0;
         mid[2] = (bottom[2]+top[2])/2.0;
         
         dist[0] = Math.abs(top[0]-bottom[0])/2.0;
         dist[1] = Math.abs(top[1]-bottom[1])/2.0;
         dist[2] = Math.abs(top[2]-bottom[2])/2.0;
         
         bottom[0] = mid[0]-dist[0];
         bottom[1] = mid[1]-dist[1];
         bottom[2] = mid[2]-dist[2];
         
         top[0] = mid[0]+dist[0];
         top[1] = mid[1]+dist[1];
         top[2] = mid[2]+dist[2];
      }
      
      double[][] data = new double[8][];
      for (int i = 0; i < 8; i++)
         data[i] = Util.newVector(4);
      Shape result = new Shape(data);
      
      // 0
      Util.copy(result.points[0], bottom);
      
      // 1
      Util.copy(result.points[1], bottom);
      result.points[1][1] = top[1];
      
      // 2
      Util.copy(result.points[2], result.points[1]);
      result.points[2][0] = top[0];
      
      // 3
      Util.copy(result.points[3], bottom);
      result.points[3][0] = top[0];
      
      // 4
      Util.copy(result.points[4], bottom);
      result.points[4][2] = top[2];
      
      // 5
      Util.copy(result.points[5], top);
      result.points[5][0] = bottom[0];
      
      // 6
      Util.copy(result.points[6], top);
      
      // 7
      Util.copy(result.points[7], top);
      result.points[7][1] = bottom[1];
      
      {// make the faces
         // bottom
         result.addFace(new int[]{4, 7, 3, 0}, color);
         // top
         result.addFace(new int[]{1, 2, 6, 5}, color);
         // right
         result.addFace(new int[]{6, 2, 3, 7}, color);
         // left
         result.addFace(new int[]{1, 5, 4, 0}, color);
         // front
         result.addFace(new int[]{4, 5, 6, 7}, color);
         // back
         result.addFace(new int[]{0, 3, 2, 1}, color);
      }
      return result;
   }
}
