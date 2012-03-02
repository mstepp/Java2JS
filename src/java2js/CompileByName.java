package java2js;

import java.io.*;
import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;

public class CompileByName {
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
      InputStream in = CompileByName.class.getClassLoader().getResourceAsStream(args[0]);
      if (in == null) {
         throw new RuntimeException("Cannot find resource: " + args[0]);
      }
      ClassParser parser = new ClassParser(in, args[0]);
      JavaClass clazz = parser.parse();
      Compiler compiler = new Compiler(new ClassGen(clazz), new DefaultNameMunger(), logger);
      compiler.compile(new DefaultPrinter(System.out));
   }
}
