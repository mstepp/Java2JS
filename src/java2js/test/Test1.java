package java2js.test;

import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;
import java2js.*;

public class Test1 {
   private static Logger logger = 
      new Logger() {
         public void logWarning(String message) {
            System.err.println("[WARNING] " + message);
         }
         public void logError(String message) {
            System.err.println("[ERROR] " + message);
         }
         public void log(String message) {
            System.out.println(message);
         }
      };

   public static void main(String args[]) throws Throwable {
      ClassParser parser = new ClassParser(args[0]);
      JavaClass clazz = parser.parse();
      java2js.Compiler compiler = new java2js.Compiler(new ClassGen(clazz), new DefaultNameMunger(), logger);
      compiler.compile(new DefaultPrinter(System.out));
   }
}
