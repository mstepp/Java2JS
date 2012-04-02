package lambda;

import static lambda.Functions.*;

public class Main {
   public static void run(Printer printer) {
      // Y (\fact.\n.(ISZERO? n) ONE (MULTIPLY n (fact (DEC n))))
      //      Expression FACTORIAL = A(Y, L(L( A( ISZERO, ONE, N(1), A(MULTIPLY, ONE, A(TWO, A(DEC, ONE)))))));
      Expression apply = A(CONS, L(ONE), NIL); // A(CONS, N(0), A(CONS, N(1), A(CONS, N(2), NIL)))));
      while (apply.eval()) {}
      PrettyPrinter.print(printer, apply);
   }

   public static void main(String args[]) {
      Printer printer = new ConsolePrinter();
      run(new ConsolePrinter());
      System.out.println();
   }
}

/*
\. 1 (\. \. 1 #0 2) #0

CONS (\f. CONS #0 f) #0



 */