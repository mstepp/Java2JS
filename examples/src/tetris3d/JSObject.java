package tetris3d;

public class JSObject {
   JSObject() {}

   public final native void setProperty(String name, JSObject value);
   public final native JSObject getProperty(String name);
   public final native JSObject invokeMethod(String name, JSObject... params);

   public final native Double asNumber();
   public final native String asString();
   public final native Element asElement();
   public final native boolean asBoolean();

   public static native JSObject fromBoolean(boolean b);
   public static native JSObject fromString(String str);
   public static native JSObject fromNumber(double d);
   public static native JSObject fromFunction(Function f);

   public static native void setTimeout(Function func, int ms);
}

class Element extends JSObject {
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

   public final native void addEventHandler(String eventName, EventCallback callback);

   public native static Element getElementById(String id);
   // other methods are not really necessary because of invokeMethod
}

interface Function {
   JSObject apply(JSObject target, JSObject... args);
}

class NativeFunction implements Function {
   public native JSObject apply(JSObject target, JSObject... args);
}

abstract class EventCallback implements Function {
   public final JSObject apply(JSObject target, JSObject... args) {
      if (args.length < 1)
         throw new IllegalArgumentException("Expecting >0 args");
      Element element = target.asElement();
      if (element == null)
         throw new IllegalArgumentException("Target should be Element");
      boolean result = onevent(element, args[0]);
      return JSObject.fromBoolean(result);
   }

   public abstract boolean onevent(Element source, JSObject event);
}
