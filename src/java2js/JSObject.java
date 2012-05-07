package java2js;

public class JSObject {
   JSObject() {}

   public final native void setProperty(String name, JSObject value);
   public final native JSObject getProperty(String name);
   public final native JSObject invokeMethod(String name, JSObject... params);
   public final native boolean hasProperty(String name);

   public final native Double asNumber();
   public final native String asString();
   public final native Element asElement();
   public final native boolean asBoolean();
   public final native JSObject[] asArray();

   public static native JSObject fromBoolean(boolean b);
   public static native JSObject fromString(String str);
   public static native JSObject fromNumber(double d);
   public static native JSObject fromFunction(JSFunction f);
   public static native JSObject fromArray(JSObject[] array);
   public static native JSObject empty();
   public static native JSObject newInstance(JSObject constructor, JSObject... params);
   public static native JSObject newInstance(String classname, JSObject... params);
   public static native JSObject globalObject(String name);

   public static native void setTimeout(JSFunction func, int ms);
}

interface JSFunction {
   JSObject apply(JSObject target, JSObject... args);
}

class NativeJSFunction implements JSFunction {
   public native JSObject apply(JSObject target, JSObject... args);
}

abstract class EventCallback implements JSFunction {
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
