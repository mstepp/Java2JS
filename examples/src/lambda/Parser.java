package lambda;

/*
  expr ::= lambda | app | var
  
  var ::= '[0-9]+'

  app ::= '(' expr expr ')'
  
  lambda ::= '(\' expr ')'
 */
public class Parser {
   public static Expression parse(String str) {
      return parseExpression(str, new int[]{0});
   }

   private static void skipWS(String str, int[] index) {
      while (index[0] < str.length()) {
         switch (str.charAt(index[0])) {
         case ' ':
         case '\t':
         case '\r':
         case '\n': {
            index[0]++;
            break;
         }
         default:
            return;
         }
      }
   }

   private static Expression parseExpression(String str, int[] index) {
      skipWS(str, index);
      if (index[0] >= str.length())
         throw new RuntimeException("Premature EOF");
      char first = str.charAt(index[0]);
      if (first == '(') {
         // app or lambda
         int store = index[0];
         index[0]++;
         skipWS(str, index);
         if (index[0] < str.length() && str.charAt(index[0]) == '\\') {
            // lambda
            index[0] = store;
            return parseLambda(str, index);
         } else {
            // app
            index[0] = store;
            return parseApp(str, index);
         }
      } else if ('0' <= first && first <= '9') {
         // var
         return parseVar(str, index);
      } else {
         throw new RuntimeException("Unexpected character: " + first);
      }
   }

   // '[0-9]+'
   private static Expression parseVar(String str, int[] index) {
      StringBuilder builder = new StringBuilder();
      char c;
      while (index[0] < str.length() && '0' <= (c = str.charAt(index[0])) && c <= '9') {
         index[0]++;
         builder.append(c);
      }
      if (builder.length() == 0) 
         throw new RuntimeException("Empty integer");
      return new Expression(Integer.parseInt(builder.toString()));
   }
   
   // '(' expr expr ')'
   private static Expression parseApp(String str, int[] index) {
      // start on '('
      if (!(index[0] < str.length() && str.charAt(index[0]) == '('))
         throw new RuntimeException("Expecting '('");
      index[0]++;
      Expression lhs = parseExpression(str, index);
      Expression rhs = parseExpression(str, index);
      skipWS(str, index);
      if (!(index[0] < str.length() && str.charAt(index[0]) == ')'))
         throw new RuntimeException("Expecting ')'");
      index[0]++;
      return new Expression(lhs, rhs);
   }

   // '(' '\' expr ')'
   private static Expression parseLambda(String str, int[] index) {
      // start on '('
      if (!(index[0] < str.length() && str.charAt(index[0]) == '('))
         throw new RuntimeException("Expecting '('");
      index[0]++;
      skipWS(str, index);
      if (!(index[0] < str.length() && str.charAt(index[0]) == '\\'))
         throw new RuntimeException("Expecting '('");
      index[0]++;
      Expression body = parseExpression(str, index);
      skipWS(str, index);
      if (!(index[0] < str.length() && str.charAt(index[0]) == ')'))
         throw new RuntimeException("Expecting ')'");
      index[0]++;
      return new Expression(body);
   }

   public static void main(String args[]) {
      Expression expr = Parser.parse(args[0]);
      expr.dump(new DefaultPrinter());
      System.out.println();
   }
}
