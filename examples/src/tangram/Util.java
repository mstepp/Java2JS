package tangram;

import java2js.*;

public class Util {
   public static native double floor(double d);
   public static native double sqrt(double d);
   public static native Element $(String id);
   public static native Element createElement(String tag);
   public static native Element createTextNode(String tag);
   public static native Element[] getElementsByTagName(Element document, String tag);
   public static native int parseInt(String str);

   public static double normalizeAngle(double angle) {
      while (angle < 0)
         angle = floor(angle+360);
      while (angle >= 360)
         angle = floor(angle-360);
      return angle;
   }

   public static boolean inTriangle(Point p1, Point p2, Point p3, Point test) {
      // assume that p1->p2->p3 is counterclockwise
      Point diff1 = subtract(test,p1);
      Point diff2 = subtract(test,p2);
      Point diff3 = subtract(test,p3);
      
      Point p1p2 = subtract(p2,p1);
      Point p2p3 = subtract(p3,p2);
      Point p3p1 = subtract(p1,p3);
      
      Point cross1 = cross(p1p2, diff1);
      Point cross2 = cross(p2p3, diff2);
      Point cross3 = cross(p3p1, diff3);
      
      return (dot(cross1,cross2) >= 0) && (dot(cross2,cross3) >= 0);
   }

   public static Point subtract(Point p1, Point p2) {
      // p1-p2
      int p1z = p1.hasZ() ? p1.z : 0;
      int p2z = p2.hasZ() ? p2.z : 0;
      return new Point(p1.x-p2.x, p1.y-p2.y, p1z-p2z);
   }
   
   public static Point add(Point p1, Point p2) {
      // p1+p2
      int p1z = p1.hasZ() ? p1.z : 0;
      int p2z = p2.hasZ() ? p2.z : 0;
      return new Point(p1.x+p2.x, p1.y+p2.y, p1z+p2z);
   }
   
   public static Point cross(Point p1, Point p2) {
      // {x,y,z:float}
      int p1z = p1.hasZ() ? p1.z : 0;
      int p2z = p2.hasZ() ? p2.z : 0;
      
      int x = p1.y*p2z - p1z*p2.y;
      int y = p1z*p2.x - p1.x*p2z;
      int z = p1.x*p2.y - p1.y*p2.x;
      return new Point(x, y, z);
   }

   public static int dot(Point p1, Point p2) {
      // {x,y,z:float}
      int p1z = p1.hasZ() ? p1.z : 0;
      int p2z = p2.hasZ() ? p2.z : 0;
      return p1.x*p2.x + p1.y*p2.y + p1z*p2z;
   }

   public static double magnitude(Point v) {
      return sqrt(dot(v, v));
   }
}
