package java2js.test;

import java2js.*;

public class Factorial {
   public static void main(String args[]) {
      Output.stdout.println(factorial(10));
   }

   public static int factorial(int n) {
      int result = 1;
      while (n>1) {
         result *= (n--);
      }
      return result;
   }
}
