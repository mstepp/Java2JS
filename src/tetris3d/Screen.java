package tetris3d;

public class Screen {
   public final Color bgcolor;
   public final int width;
   public final int height;
   private final JSObject context;
   
   public Screen(int width, int height, Color bgcolor, JSObject context) {
      this.bgcolor = bgcolor;
      this.width = width;
      this.height = height;
      this.context = context;
   }

   public void clear() {
      this.context.setProperty("fillStyle", 
                               JSObject.fromString("rgb(" + this.bgcolor.getRed() + "," + this.bgcolor.getGreen() + "," + this.bgcolor.getBlue() + ")"));
      this.context.invokeMethod("fillRect", 
                                JSObject.fromNumber(0), 
                                JSObject.fromNumber(0), 
                                JSObject.fromNumber(this.width), 
                                JSObject.fromNumber(this.height));
   }

   // p1 and p2 are in canonical cube coords
   public void drawLine(double[] p1, double[] p2, String style) {
      this.context.setProperty("strokeStyle", JSObject.fromString(style));
      this.context.invokeMethod("beginPath");
      double wid2 = (int)(this.width/2.0);
      double hei2 = (int)(this.height/2.0);
      double x = wid2 + wid2*p1[0];
      double y = hei2 - hei2*p1[1];
      this.context.invokeMethod("moveTo", 
                                JSObject.fromNumber(x),
                                JSObject.fromNumber(y));
      x = wid2 + wid2*p2[0];
      y = hei2 - hei2*p2[1];
      this.context.invokeMethod("lineTo", 
                                JSObject.fromNumber(x), 
                                JSObject.fromNumber(y));
      this.context.invokeMethod("closePath");
      this.context.invokeMethod("stroke");
   }

   public void drawString(double[] point, String text, String style) {
      this.context.setProperty("strokeStyle", JSObject.fromString(style));
      this.context.setProperty("textAlign", JSObject.fromString("center"));
      double wid2 = (int)(this.width/2.0);
      double hei2 = (int)(this.height/2.0);
      double x = wid2 + wid2*point[0];
      double y = hei2 - hei2*point[1];
      this.context.invokeMethod("strokeText", 
                                JSObject.fromString(text), 
                                JSObject.fromNumber(x),
                                JSObject.fromNumber(y));
   }

   // assume face has been transformed, does no checking
   public void drawFace(Face face, String style) {
      this.context.setProperty("strokeStyle", JSObject.fromString(style));
      this.context.invokeMethod("beginPath");
      double wid2 = (int)(this.width/2.0);
      double hei2 = (int)(this.height/2.0);
      for (int i = 0; i < face.points.length; i++) {
         if (i==0)
            this.context.invokeMethod("moveTo",
                                      JSObject.fromNumber(wid2 + wid2*face.transformed[i][0]),
                                      JSObject.fromNumber(hei2 - hei2*face.transformed[i][1]));
         else
            this.context.invokeMethod("lineTo",
                                      JSObject.fromNumber(wid2 + wid2*face.transformed[i][0]),
                                      JSObject.fromNumber(hei2 - hei2*face.transformed[i][1]));
      }
      this.context.invokeMethod("closePath");
      this.context.invokeMethod("stroke");
   }

   public void fillFace(Face face, String style) {
      this.context.setProperty("fillStyle", JSObject.fromString(style));
      this.context.invokeMethod("beginPath");
      double wid2 = (int)(this.width/2.0);
      double hei2 = (int)(this.height/2.0);
      for (int i = 0; i < face.points.length; i++) {
         if (i==0)
            this.context.invokeMethod("moveTo", 
                                      JSObject.fromNumber(wid2 + wid2*face.transformed[i][0]),
                                      JSObject.fromNumber(hei2 - hei2*face.transformed[i][1]));
         else
            this.context.invokeMethod("lineTo",
                                      JSObject.fromNumber(wid2 + wid2*face.transformed[i][0]),
                                      JSObject.fromNumber(hei2 - hei2*face.transformed[i][1]));
      }
      this.context.invokeMethod("closePath");
      this.context.invokeMethod("fill");
   }
}
