package tetris3d;

/**
 * [int]x3, {red:float,green:float,blue:float}
 */
class Box {
   public boolean flashing;
   public Block block;
   public int[] center;
   public Shape shape;

   public Box(Block block, int[] center, Color color) {
      this.flashing = false;
      this.block = block;
      this.center = center;
      this.shape = new Shape(new double[][]{{0,0,0,1},  // neartopleft
                                            {0,0,0,1},  // neartopright
                                            {0,0,0,1},  // nearbottomright
                                            {0,0,0,1},  // nearbottomleft
                                            {0,0,0,1},  // fartopleft
                                            {0,0,0,1},  // fartopright
                                            {0,0,0,1},  // farbottomright
                                            {0,0,0,1}}); // farbottomleft
      /*   
           4----5
           /|   /|
           0----1 |
           | 7--|-6
           |/   |/
           3----2
      */
      this.shape.addFace(new int[]{0,1,2,3}, color); // near face
      this.shape.addFace(new int[]{5,4,7,6}, color); // far face
      this.shape.addFace(new int[]{1,5,6,2}, color); // right face
      this.shape.addFace(new int[]{3,2,6,7}, color); // bottom face
      this.shape.addFace(new int[]{4,0,3,7}, color); // left face
      this.shape.addFace(new int[]{4,5,1,0}, color); // top face
   }

   // make sure the center is up to date
   public void setPosition(double[] origin, double squaresize) {
      double sq = squaresize;
      double[] base = Util.newVector();
      
      // base = origin + squaresize*center
      double[] dc = {center[0], center[1], center[2]};
      Util.ax_plus_by(1, origin, sq, dc, base);
      
      /*   
           4----5
           /|   /|
           0----1 |
           | 7--|-6
           |/   |/
           3----2
      */
      // neartopleft = base + [0,sq,sq]
      Util.add(base, new double[]{0,sq,sq}, this.shape.points[0]);
      // neartopright = base + [sq,sq,sq]
      Util.add(base, new double[]{sq,sq,sq}, this.shape.points[1]);
      // nearbottomright = base + [sq,0,sq]
      Util.add(base, new double[]{sq,0,sq}, this.shape.points[2]);
      // nearbottomleft = base + [0,0,sq]
      Util.add(base, new double[]{0,0,sq}, this.shape.points[3]);
      // fartopleft = base + [0,sq,0]
      Util.add(base, new double[]{0,sq,0}, this.shape.points[4]);
      // fartopright = base + [sq,sq,0]
      Util.add(base, new double[]{sq,sq,0}, this.shape.points[5]);
      // farbottomright = base + [sq,0,0]
      Util.add(base, new double[]{sq,0,0}, this.shape.points[6]);
      // farbottomleft = base + [0,0,0]
      Util.add(base, new double[]{0,0,0}, this.shape.points[7]);
      
      for (int j = 0; j < this.shape.faces.size(); j++) {
         this.shape.faces.get(j).compute_Normal();
      }
   }
}   



// [int]x3, [[int]x3], {red:float,green:float,blue:float}
// makes deep copy of diffs array
public class Block {
   public int[] center;
   public int[][] diffs;
   public Color color;
   public Box[] boxes;

   public Block(int[] center, int[][] diffs, Color color) {
      this.center = center;
      this.diffs = new int[diffs.length][];
      this.color = color;
      this.boxes = new Box[diffs.length];
      for (int i = 0; i < diffs.length; i++) {
         int[] diffI = diffs[i];
         this.diffs[i] = new int[]{diffI[0], diffI[1], diffI[2]};
         int[] base = {center[0]+diffI[0],
                       center[1]+diffI[1],
                       center[2]+diffI[2]};
         this.boxes[i] = new Box(this, base, color);
      }
   }

   public void setCenters() {
      for (int i = 0; i < this.diffs.length; i++) {
         this.boxes[i].center[0] = this.center[0]+this.diffs[i][0];
         this.boxes[i].center[1] = this.center[1]+this.diffs[i][1];
         this.boxes[i].center[2] = this.center[2]+this.diffs[i][2];
      }
   }

   /**
    * origin=float3, squaresize=float. This function sets the position
    * of the shapes for this block.
    */
   public void setPosition(double[] origin, double squaresize) {
      this.setCenters();
      for (int i = 0; i < this.diffs.length; i++) {
         this.boxes[i].setPosition(origin, squaresize);
      }
   }

   public void rotateXY(boolean which) {
      if (which) {
         // x = y
         // y = -x
         for (int i = 0; i < this.diffs.length; i++) {
            int oldx = this.diffs[i][0];
            int oldy = this.diffs[i][1];
            this.diffs[i][0] = oldy;
            this.diffs[i][1] = -oldx;
         }
      } else {
         // x = -y
         // y = x
         for (int i = 0; i < this.diffs.length; i++) {
            int oldx = this.diffs[i][0];
            int oldy = this.diffs[i][1];
            this.diffs[i][0] = -oldy;
            this.diffs[i][1] = oldx;
         }
      }
   }


   public void rotateYZ(boolean which) {
      if (which) {
         // y = z
         // z = -y
         for (int i = 0; i < this.diffs.length; i++) {
            int oldy = this.diffs[i][1];
            int oldz = this.diffs[i][2];
            this.diffs[i][1] = oldz;
            this.diffs[i][2] = -oldy;
         }
      } else {
         // y = -z
         // z = y
         for (int i = 0; i < this.diffs.length; i++) {
            int oldy = this.diffs[i][1];
            int oldz = this.diffs[i][2];
            this.diffs[i][1] = -oldz;
            this.diffs[i][2] = oldy;
         }
      }
   }
}
