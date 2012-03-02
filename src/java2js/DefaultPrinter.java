package java2js;

import java.io.*;

public class DefaultPrinter implements Printer {
   private String tabs;
   private PrintStream out;
   private DefaultPrinter(String _tabs, PrintStream _out) {
      this.tabs = _tabs;
      this.out = _out;
   }
   public DefaultPrinter(PrintStream _out) {
      this("", _out);
   }
   public Printer tab(String tab) {
      return new DefaultPrinter(this.tabs + tab, this.out);
   }
   public void print(String message, Object... args) {
      this.out.printf(this.tabs + message, args);
   }
   public void println(String message, Object... args) {
      this.out.printf(this.tabs + message + "\n", args);
   }
}
