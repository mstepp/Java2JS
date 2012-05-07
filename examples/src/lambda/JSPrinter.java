package lambda;

public class JSPrinter implements Printer {
   public void printf(String str, Object... args) {
      String result = String.format(str, args);
      nativePrint(result);
   }
   private native void nativePrint(String str);
}
