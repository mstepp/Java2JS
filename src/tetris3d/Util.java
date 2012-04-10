package tetris3d;

public class Util {
   public static final double PI = 3.1415926535897932384626;

   // a and b are [float]x3 (points)
   public static double distance(double a[], double b[]) {
      double[] diff = {0,0,0};
      Util.subtract(a,b,diff);
      return Math.sqrt(Util.dot(diff,diff));
   }

   public static double[] newVector() {
      return newVector(3);
   }
   
   public static double[] newVector(int length) {
      double[] result = new double[length];
      for (int i = length-1; i>=0; i--)
         result[i] = 0.0;
      return result;
   }

   // scalar, vector3, scalar, vector3, vector3
   public static void ax_plus_by(double a, double[] x, double b, double[] y, double[] result) {
      result[0] = a*x[0] + b*y[0];
      result[1] = a*x[1] + b*y[1];
      result[2] = a*x[2] + b*y[2];
   }

   public static void unhomo(double[] vec) {
      double last = vec[3];
      vec[0] /= last;
      vec[1] /= last;
      vec[2] /= last;
      vec[3] = 1.0;
   }

   // vecs is a list of 4d vectors
   public static void unhomoList(double[][] vecs) {
      for (int i = 0; i < vecs.length; i++) {
         Util.unhomo(vecs[i]);
      }
   }

   public static double dot(double[] a, double[] b) {
      return (a[0]*b[0])+(a[1]*b[1])+(a[2]*b[2]);
   }

   public static void copy(double[] a, double[] b) {
      a[0] = b[0];
      a[1] = b[1];
      a[2] = b[2];
   }

   public static void project(double[] from, double[] onto, double[] result) {
      // result = (from*onto)/(onto*onto) * onto
      double mag = Util.dot(onto, onto);
      if (mag==0.0) {
         result[0] = result[1] = result[2] = 0.0;
      } else {
         double v0,v1,v2;
         mag = Util.dot(from, onto)/mag;
         v0 = mag*onto[0];
         v1 = mag*onto[1];
         v2 = mag*onto[2];

         result[0] = v0;
         result[1] = v1;
         result[2] = v2;
      }
   }
   
   // operand-safe
   public static void cross(double[] a, double[] b, double[] result) {
      double v0, v1, v2;

      v0 = a[1]*b[2] - a[2]*b[1];
      v1 = a[2]*b[0] - a[0]*b[2];
      v2 = a[0]*b[1] - a[1]*b[0];

      result[0] = v0;
      result[1] = v1;
      result[2] = v2;
   }

   // operand-safe
   public static void normalize(double[] old, double[] result) {
      double mag = Math.sqrt(Util.dot(old,old));
      if (mag == 0.0) {
         result[0] = 0.0;
         result[1] = 0.0;
         result[2] = 0.0;
      } else {
         result[0] = old[0]/mag;
         result[1] = old[1]/mag;
         result[2] = old[2]/mag;
      }
   }

   // operand-safe
   public static void add(double[] a, double[] b, double[] result) {
      result[0] = a[0]+b[0];
      result[1] = a[1]+b[1];
      result[2] = a[2]+b[2];
   }

   // operand-safe
   public static void subtract(double[] a, double[] b, double[] result) {
      result[0] = a[0]-b[0];
      result[1] = a[1]-b[1];
      result[2] = a[2]-b[2];
   }

   // operand-safe
   public static void mult4x4(double[][] a, double[][] b, double[][] c) {
      double[][] temp = Util.make4x4();
      for (int i = 0; i < 4; i++) {
         for (int j = 0; j < 4; j++) {
            temp[i][j] =
               a[i][0]*b[0][j] + a[i][1]*b[1][j] +
               a[i][2]*b[2][j] + a[i][3]*b[3][j];
         }
      }
      for (int i = 0; i < 4; i++)
         for (int j = 0; j < 4; j++)
            c[i][j] = temp[i][j];
   }

   public static void mult(double[][] m, double[] vector4, double[] result4) {
      double[] temp = Util.newVector(4);

      for (int i = 0; i < 4; i++) {
         temp[i] =
            m[i][0]*vector4[0] + m[i][1]*vector4[1] +
            m[i][2]*vector4[2] + m[i][3]*vector4[3];
      }

      for (int i = 0; i < 4; i++)
         result4[i] = temp[i];
   }

   public static double[][] make4x4() {
      return new double[][]{Util.newVector(4), 
                            Util.newVector(4), 
                            Util.newVector(4), 
                            Util.newVector(4)};
   }

   public static void make_Identity(double[][] result) {
      result[0][0] = 1.0; result[0][1] = 0.0; result[0][2] = 0.0; result[0][3] = 0.0;
      result[1][0] = 0.0; result[1][1] = 1.0; result[1][2] = 0.0; result[1][3] = 0.0;
      result[2][0] = 0.0; result[2][1] = 0.0; result[2][2] = 1.0; result[2][3] = 0.0;
      result[3][0] = 0.0; result[3][1] = 0.0; result[3][2] = 0.0; result[3][3] = 1.0;
   }

   public static void make_Rotate(double[][] result, double[] about, double angle) {
      double[][] rotateInv = Util.make4x4(), rotateZ = Util.make4x4();
      double[] newx = Util.newVector(3);
      double[] newy = Util.newVector(3);
      double[] tempVec = {0.0,1.0,0.0};
      double cosangle, sinangle;

      if (about[0]==0.0 && about[1]==0.0 && about[2]==0.0) {
         Util.make_Identity(result);
         return;
      }

      if (about[0]==0.0 && about[2]==0.0) {
         // about==Y
         newx[0] = 0;
         newx[1] = 0;
         newx[2] = 1;

         newy[0] = 1;
         newy[1] = 0;
         newy[2] = 0;

         Util.normalize(about, about);
      } else {
         Util.cross(tempVec, about, newx);
         Util.normalize(newx, newx);

         Util.cross(about, newx, newy);
         Util.normalize(newy, newy);
         Util.normalize(about,about);
      }

      cosangle = Math.cos(angle);
      sinangle = Math.sin(angle);

      Util.make_Identity(rotateZ);
      rotateZ[0][0] = cosangle;
      rotateZ[0][1] = -sinangle;
      rotateZ[1][0] = sinangle;
      rotateZ[1][1] = cosangle;

      Util.make_Identity(result);
      Util.copy(result[0], newx);
      Util.copy(result[1], newy);
      Util.copy(result[2], about);

      Util.make_Identity(rotateInv);
      rotateInv[0][0] = newx[0]; rotateInv[0][1] = newy[0]; rotateInv[0][2] = about[0];
      rotateInv[1][0] = newx[1]; rotateInv[1][1] = newy[1]; rotateInv[1][2] = about[1];
      rotateInv[2][0] = newx[2]; rotateInv[2][1] = newy[2]; rotateInv[2][2] = about[2];

      Util.mult4x4(rotateZ, result, result);
      Util.mult4x4(rotateInv, result, result);
   }

   public static void transpose(double[][] input, double[][] result) {
      double[][] temp = Util.make4x4();
      int i,j;

      for (i = 0; i < 4; i++) {
         for (j = 0; j < 4; j++) {
            temp[i][j] = input[j][i];
         }
      }

      for (i = 0; i < 4; i++) {
         for (j = 0; j < 4; j++) {
            result[i][j] = temp[i][j];
         }
      }
   }

   public static void make_Translate(double[][] result, double[] vector) {
      Util.make_Identity(result);
      result[0][3] = vector[0];
      result[1][3] = vector[1];
      result[2][3] = vector[2];
   }

   public static void make_Translate3(double[][] result, double x, double y, double z) {
      double[] point = {x,y,z};
      Util.make_Translate(result, point);
   }

   public static void make_RotateXY(double[][] result, double angle) {
      double cosA = Math.cos(angle);
      double sinA = Math.sin(angle);

      Util.make_Identity(result);
      result[0][0] = cosA;
      result[0][1] = -sinA;
      result[1][0] = sinA;
      result[1][1] = cosA;
   }

   public static void make_RotateXZ(double[][] result, double angle) {
      double cosA = Math.cos(angle);
      double sinA = Math.sin(angle);

      Util.make_Identity(result);
      result[0][0] = cosA;
      result[0][2] = -sinA;
      result[2][0] = sinA;
      result[2][2] = cosA;
   }

   public static void make_RotateYZ(double[][] result, double angle) {
      double cosA = Math.cos(angle);
      double sinA = Math.sin(angle);

      Util.make_Identity(result);
      result[1][1] = cosA;
      result[1][2] = -sinA;
      result[2][1] = sinA;
      result[2][2] = cosA;
   }

   public static double findangle(double x1, double y1, double x2, double y2) {
      double dist;
      if ((x1==x2) && (y1==y2))
         return 0.0;
      dist = Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
      if (x2 >= x1) {
         return Math.asin((y2-y1)/dist);
      } else if (y2>=y1) {
         return Math.acos((x2-x1)/dist);
      } else {
         return (Math.asin((y1-y2)/dist) + PI);
      }
   }
}
