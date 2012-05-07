package lambda;

import static lambda.Expression.Type;
import java.util.*;

public class PrettyPrinter {
   public static void print(Printer printer, Expression expr) {
      Integer numeral = isNumeral(expr);
      Boolean bool = isBoolean(expr);
      List<Expression> list = isList(expr);
      if (numeral != null) {
         printer.printf("#%d", numeral);
      } else if (bool != null) {
         printer.printf("%s", bool);
      } else if (list != null) {
         printer.printf("{");
         boolean sawNull = false;
         for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == null) {
               printer.printf(" . ");
               sawNull = true;
            } else if (sawNull) {
               print(printer, list.get(i));
            } else {
               if (i>0) printer.printf(",");
               print(printer, list.get(i));
            }
         }
         printer.printf("}");
      } else {
         // default
         switch (expr.type()) {
         case VARIABLE:
            printer.printf("%d", expr.index());
            break;
         case LAMBDA:
            printer.printf("(\\ ");
            print(printer, expr.child());
            printer.printf(")");
            break;
         case APPLICATION:
            printer.printf("(");
            print(printer, expr.lhs());
            printer.printf(" ");
            print(printer, expr.rhs());
            printer.printf(")");
            break;
         default:
            throw new RuntimeException("Unknown expression type");
         }
      }
   }

   // \f.\x.f (f (f (f x))) = L(L(A(2, A(2, A(2, A(2,1))))))
   public static Integer isNumeral(Expression expr) {
      if (expr.type().equals(Type.LAMBDA) && expr.child().type().equals(Type.LAMBDA)) {
         Expression body = expr.child().child();
         int value = 0;
         while (body.type().equals(Type.APPLICATION) &&
                body.lhs().type().equals(Type.VARIABLE) && 
                body.lhs().index() == 2) {
            body = body.rhs();
            value++;
         }
         if (body.type().equals(Type.VARIABLE) && body.index() == 1) {
            return value;
         }
      }
      return null;
   }

   // (CONS a (CONS b (CONS c NIL))) = \f.f a (\f. f b (\f. f c NIL)) = L(A(A(1,a), L(A(A(1,b),  ... NIL))))
   public static List<Expression> isList(Expression expr) {
      List<Expression> list = new ArrayList<Expression>();
      while (expr.type().equals(Type.LAMBDA) && 
             expr.child().type().equals(Type.APPLICATION) &&
             expr.child().lhs().type().equals(Type.APPLICATION) &&
             expr.child().lhs().lhs().type().equals(Type.VARIABLE) &&
             expr.child().lhs().lhs().index() == 1) {
         list.add(expr.child().lhs().rhs());
         expr = expr.child().rhs();
      }
      if (isNil(expr)) {
         return list;
      } else if (list.size() > 0) {
         list.add(null);
         list.add(expr);
         return list;
      } else {
         return null;
      }
   }

   // \f.FALSE = \f.\a.\b.b = L(L(L(1)))
   public static boolean isNil(Expression expr) {
      return expr.type().equals(Type.LAMBDA) &&
         expr.child().type().equals(Type.LAMBDA) &&
         expr.child().child().type().equals(Type.LAMBDA) &&
         expr.child().child().child().type().equals(Type.VARIABLE) &&
         expr.child().child().child().index() == 1;
   }
   
   // \a.\b.a = L(L(2)),   \a.\b.b = L(L(1))
   public static Boolean isBoolean(Expression expr) {
      if (expr.type().equals(Type.LAMBDA) && expr.child().type().equals(Type.LAMBDA)) {
         Expression body = expr.child().child();
         if (body.type().equals(Type.VARIABLE)) {
            if (body.index() == 1)
               return false;
            else if (body.index() == 2)
               return true;
         }
      } 
      return null;
   }
}
