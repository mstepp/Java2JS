package heart;

public class Animator {
   private final String id;

   public Animator(String _id) {
      this.id = _id;
      assignContextPojo();
   }

   private native void assignContextPojo();
   public native void clear();
   public native void setColor(int r, int g, int b);
   public native void drawLine(int x1, int y1, int x2, int y2);
}