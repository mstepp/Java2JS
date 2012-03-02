package java2js;

public class CompilationException extends Exception {
   private static final long serialVersionUID = 543875430L;
   public CompilationException(String message) {
      super(message);
   }
   public CompilationException(String message, Throwable t) {
      super(message, t);
   }
}
