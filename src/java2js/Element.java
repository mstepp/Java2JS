package java2js;

public class Element extends JSObject {
   public static final Element DOCUMENT = null;
   public static final Element WINDOW = null;

   static {
      registerNatives();
   }

   Element() {}

   private static native void registerNatives();

   public final void setAttribute(String name, String value) {
      invokeMethod("setAttribute", fromString(name), fromString(value));
   }
   public final String getAttribute(String name) {
      JSObject result = invokeMethod("getAttribute", fromString(name));
      return result.asString();
   }
   public final boolean hasAttribute(String name) {
      JSObject result = invokeMethod("hasAttribute", fromString(name));
      return result.asBoolean();
   }
   public final void appendChild(Element element) {
      invokeMethod("appendChild", element);
   }
   public final void setStyle(String attribute, String value) {
      JSObject style = this.getProperty("style");
      if (style != null)
         style.setProperty(attribute, JSObject.fromString(value));
   }

   public final native void addEventHandler(String eventName, EventCallback callback);

   public native static Element getElementById(String id);
   // other methods are not really necessary because of invokeMethod
}
