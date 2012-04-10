package lambda;

public class ConsolePrinter implements Printer {
   public void printf(String str, Object... args) {
      System.out.printf(str, args);
   }
}
