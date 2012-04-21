package tangram;

public class Point {
   public final int x, y, z;
   public final boolean hasZ;
   public Point(int x, int y, int z) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.hasZ = true;
   }
   public Point(int x, int y) {
      this.x = x;
      this.y = y;
      this.z = 0;
      this.hasZ = false;
   }
}
