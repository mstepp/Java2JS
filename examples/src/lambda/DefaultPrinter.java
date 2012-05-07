package lambda;

public class DefaultPrinter implements Printer {
   public void printf(String str, Object... args) {
      System.out.printf(str, args);
   }
}
