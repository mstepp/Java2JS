package tetris3d;

/** 
 * This class represents all the information needed for a camera in
 * 3D. It can have any center point and direction.  There are methods
 * provided to transform vectors from world coordinates to canonical
 * cube coordinates.
 */

/**
 * point = [float]x3, center point of the camera,
 * n = [float]x3, vector pointing backwards from the screen,
 * up = [float]x3, vector identifying the 'up' direction of the camera
 */

public class Camera {
   public double UMAX, UMIN, VMAX, VMIN, FOCAL_LENGTH, HITHER, YON;
   public double[] U, V, N, CAMERA_POINT;
   public double[][] CAMERA2CUBE, WORLD2CAMERA, WORLD2CUBE;
   public boolean camera2cube_dirty, world2camera_dirty;
   
   public Camera(double[] point, double[] n, double[] up) {
      this.UMAX = 5.0;
      this.UMIN = -5.0;
      this.VMAX = 5.0;
      this.VMIN = -5.0;
      this.FOCAL_LENGTH = -5.0;
      this.HITHER = -4.0;
      this.YON = -1000000.0;
      this.U = new double[]{0,0,0};
      this.V = new double[]{0,0,0};
      this.N = new double[]{0,0,0};
      this.CAMERA_POINT = new double[]{0,0,0};
      /////////////////////////////////////////////////
      this.CAMERA2CUBE = Util.make4x4();
      this.camera2cube_dirty = true;
      this.WORLD2CAMERA = Util.make4x4();
      this.world2camera_dirty = true;
      this.WORLD2CUBE = Util.make4x4();
      
      this.setPoint(point);
      this.setAxes(n, up);
   }

   public void setHitherYon(double hither, double yon) {
      this.HITHER = hither;
      this.YON = yon;
      this.camera2cube_dirty = true;
   }

   public void setPoint(double[] point) {
      Util.copy(this.CAMERA_POINT, point);
      this.world2camera_dirty = true;
   }

   public void setBounds(double umin, double umax, double vmin, double vmax, double focal) {
      this.UMIN = umin;
      this.UMAX = umax;
      this.VMIN = vmin;
      this.VMAX = vmax;
      this.FOCAL_LENGTH = focal;
      this.camera2cube_dirty = true;
   }

   public void setAxes(double[] n, double[] up) {
      Util.normalize(n, this.N);
      
      Util.cross(up, this.N, this.U);
      Util.normalize(this.U, this.U);
      
      Util.cross(this.N, this.U, this.V);
      Util.normalize(this.V, this.V);
      
      this.world2camera_dirty = true;
   }

   /** 
    * 1. Translate by (0,0,f).
    * 2. Scale by (-2f/deltaU, -2f/deltaV, 1)
    * 2b. Scale by (-1/(B+f), -1/(B+f), -1/(B+f))
    * 3. Convert to cube
    */
   public void create_Camera2Cube(double[][] result) {
      double[][] temp = Util.make4x4();
      double zmin;
      
      Util.make_Identity(result);
      result[2][3] = this.FOCAL_LENGTH;
      
      Util.make_Identity(temp);
      temp[0][0] = (-2.0*this.FOCAL_LENGTH)/(this.UMAX-this.UMIN);
      temp[1][1] = (-2.0*this.FOCAL_LENGTH)/(this.VMAX-this.VMIN);
      Util.mult4x4(temp, result, result);
      
      Util.make_Identity(temp);
      temp[0][0] = -1.0/(this.YON+this.FOCAL_LENGTH);
      temp[1][1] = -1.0/(this.YON+this.FOCAL_LENGTH);
      temp[2][2] = -1.0/(this.YON+this.FOCAL_LENGTH);
      Util.mult4x4(temp, result, result);
      
      zmin = -(this.HITHER+this.FOCAL_LENGTH)/(this.YON+this.FOCAL_LENGTH);
      
      Util.make_Identity(temp);
      temp[2][2] = 1.0/(1.0+zmin);
      temp[2][3] = -(zmin/(1.0+zmin));
      temp[3][2] = -1.0;
      temp[3][3] = 0;
      Util.mult4x4(temp, result, result);
   }

   /**
    * Fills the given matrix with the world-->camera coordinate
    * transformation.  (i.e. translate by -VRP and rotate so that u=x,
    * v=y, and n=z).
    */
   public void create_World2Camera(double[][] result) {
      Util.make_Identity(result);
      result[0][3] = -this.CAMERA_POINT[0];
      result[1][3] = -this.CAMERA_POINT[1];
      result[2][3] = -this.CAMERA_POINT[2];
      
      double[][] tempMat = {
         {this.U[0], this.U[1], this.U[2], 0.0},
         {this.V[0], this.V[1], this.V[2], 0.0},
         {this.N[0], this.N[1], this.N[2], 0.0},
         {0.0, 0.0, 0.0, 1.0}
      };
      
      Util.mult4x4(tempMat, result, result);
   }
   

   /**
    * Rebuilds the world->cube matrix, if it is dirty.
    */
   public void makeWorld2Cube() {
      boolean dirty = false;
      if (this.camera2cube_dirty) {
         this.create_Camera2Cube(this.CAMERA2CUBE);
         this.camera2cube_dirty = false;
         dirty = true;
      }
      
      if (this.world2camera_dirty) {
         this.create_World2Camera(this.WORLD2CAMERA);
         this.world2camera_dirty = false;
         dirty = true;
      }
      
      if (dirty)
         Util.mult4x4(this.CAMERA2CUBE, this.WORLD2CAMERA, this.WORLD2CUBE);
   }
   
   
   /**
    * Transforms a list of 4D vectors from world coordinates into the
    * canonical cube.  This function will un-homo the result vectors.
    * NOTE: The vec4list param is not a matrix, it is a list of 4d
    * vectors (it is transposed).
    */
   public void transformWorld2Cube(double[][] vec4list, double[][] resultlist) {
      this.makeWorld2Cube();
      for (int i = 0; i < vec4list.length; i++) {
         Util.mult(this.WORLD2CUBE, vec4list[i], resultlist[i]);
      }
      Util.unhomoList(resultlist);
   }
   
   /**
    * Transforms a single face according to this camera. The transformed
    * points go into the face's 'transformed' field. This function will
    * un-homo the transformed points.
    */
   public void transformFace(Face face) {
      this.makeWorld2Cube();
      for (int i = 0; i < face.points.length; i++) {
         Util.mult(this.WORLD2CUBE, face.parent.points[face.points[i]], face.transformed[i]);
      }
      Util.unhomoList(face.transformed);
   }
   
   public static final int INVISIBLE_RIGHT = 1;
   public static final int INVISIBLE_LEFT = 2;
   public static final int INVISIBLE_TOP = 4;
   public static final int INVISIBLE_BOTTOM = 8;
   public static final int INVISIBLE_HITHER = 16;
   public static final int INVISIBLE_YON = 32;

   public int getVisibility(double[] point) {
      int result = 0;
      double[] v1 = Util.newVector();
      double[] v2 = Util.newVector();
      double[] normal = Util.newVector();
      double[] diff = Util.newVector();
      double vhalf = (this.VMAX-this.VMIN)/2;
      double uhalf = (this.UMAX-this.UMIN)/2;
      
      // center - formal_length*N
      double[] tip = Util.newVector();
      Util.ax_plus_by(1, this.CAMERA_POINT, -this.FOCAL_LENGTH, this.N, tip);
      
      // center + uhalf*U + vhalf*V
      double[] topright = Util.newVector();
      Util.ax_plus_by(uhalf, this.U, vhalf, this.V, topright);
      Util.ax_plus_by(1, topright, 1, this.CAMERA_POINT, topright);
      
      // center + uhalf*U - vhalf*V
      double[] bottomright = Util.newVector();
      Util.ax_plus_by(uhalf, this.U, -vhalf, this.V, bottomright);
      Util.ax_plus_by(1, bottomright, 1, this.CAMERA_POINT, bottomright);
      
      // center - uhalf*U + vhalf*V
      double[] topleft = Util.newVector();
      Util.ax_plus_by(-uhalf, this.U, vhalf, this.V, topleft);
      Util.ax_plus_by(1, topleft, 1, this.CAMERA_POINT, topleft);
      
      // center - uhalf*U - vhalf*V
      double[] bottomleft = Util.newVector();
      Util.ax_plus_by(-uhalf, this.U, -vhalf, this.V, bottomleft);
      Util.ax_plus_by(1, bottomleft, 1, this.CAMERA_POINT, bottomleft);
      
      // diff = point-tip
      Util.subtract(point, tip, diff);
      
      {// right test
         // v1 = topright - tip
         // v2 = bottomright - tip
         // normal = cross(v2,v1)
         // = (point-tip) . normal > 0
         Util.subtract(topright, tip, v1);
         Util.subtract(bottomright, tip, v2);
         Util.cross(v2, v1, normal);
         if (Util.dot(diff, normal) > 0)
            result |= Camera.INVISIBLE_RIGHT;
      }
      
      {// left test
         // v1 = topleft - tip
         // v2 = bottomleft - tip
         // normal = cross(v1,v2)
         // = (point-tip) . normal > 0
         Util.subtract(topleft, tip, v1);
         Util.subtract(bottomleft, tip, v2);
         Util.cross(v1, v2, normal);
         if (Util.dot(diff, normal) > 0)
            result |= Camera.INVISIBLE_LEFT;
      }
      
      {// top test
         // v1 = topleft - tip
         // v2 = topright - tip
         // normal = cross(v2,v1)
         Util.subtract(topleft, tip, v1);
         Util.subtract(topright, tip, v2);
         Util.cross(v2, v1, normal);
         if (Util.dot(diff, normal) > 0)
            result |= Camera.INVISIBLE_TOP;
      }
      
      {// bottom test
         // v1 = bottomleft - tip
         // v2 = bottomright - tip
         // normal = cross(v1,v2)
         Util.subtract(bottomleft, tip, v1);
         Util.subtract(bottomright, tip, v2);
         Util.cross(v1,v2,normal);
         if (Util.dot(normal,diff) > 0)
            result |= Camera.INVISIBLE_BOTTOM;
      }
      
      {// hither test
         // normal = N
         // diff = point - (center + hither*N)
         Util.ax_plus_by(1, this.CAMERA_POINT, this.HITHER, this.N, diff);
         Util.subtract(point, diff, diff);
         double poop = Util.dot(this.N, diff);
         if (poop > 0)
            result |= Camera.INVISIBLE_HITHER;
      }
      
      {// yon test
         // normal = -N
         // diff = point - (center + yon*N)
         Util.ax_plus_by(-1, this.N, 0, normal, normal);
         Util.ax_plus_by(1, this.CAMERA_POINT, this.YON, this.N, diff);
         Util.subtract(point, diff, diff);
         if (Util.dot(normal, diff) > 0)
            result |= Camera.INVISIBLE_YON;
      }
      
      return result;
   }
}
