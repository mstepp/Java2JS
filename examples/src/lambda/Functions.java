package lambda;

import java.util.*;

public class Functions {
   public static final Expression ONE = new Expression(1);
   public static final Expression TWO = new Expression(2);
   public static final Expression THREE = new Expression(3);
   public static final Expression FOUR = new Expression(4);
    
   public static final Expression TRUE = L(L(TWO)); // \a.\b.a
   public static final Expression FALSE = L(L(ONE)); // \a.\b.b
   public static final Expression CONS = L(L(L(A(ONE, THREE, TWO)))); // \a.\b.\f.f a b
   public static final Expression CAR = L(A(ONE, TRUE)); // \p.p TRUE
   public static final Expression CDR = L(A(ONE, FALSE)); // \p.p FALSE
   public static final Expression NIL = L(FALSE); // \f.FALSE
   public static final Expression ISNIL = L(A(ONE, L(L(TRUE)))); // \p.p (\a.\b.TRUE)
   public static final Expression ZERO = FALSE; // \f.\x.x
   public static final Expression INC = L(L(L(A(TWO, A(THREE, TWO, ONE))))); // \m.\f.\x.f (m f x)
   public static final Expression ADD = L(L(L(L(A(A(FOUR, TWO), A(THREE, TWO, ONE)))))); // \m.\n.\f.\x.(m f) (n f x)
   public static final Expression MULTIPLY = L(L(A(ONE, A(ADD, TWO), ZERO))); // \m.\n.n (ADD m) ZERO
   public static final Expression DEC = L(A(CAR, 
                                            A(ONE, 
                                              L(A(CONS, A(CDR, ONE), A(INC, A(CDR, ONE)))),   
                                              A(CONS, ZERO, ZERO))));
   // \n.CAR (n (\p. (CONS (CDR p) (INC (CDR p)))) (CONS ZERO ZERO))
   public static final Expression MONUS = L(L(A(ONE, DEC, TWO))); // \n.\m.m DEC n
   public static final Expression Y = L(A(L(A(TWO,A(ONE,ONE))), L(A(TWO,A(ONE,ONE))))); // \f. (\x.f (x x)) (\x. f (x x))
   public static final Expression ISZERO = L(A(ONE, L(FALSE), TRUE)); // \n.n (\x.FALSE) TRUE
   public static final Expression NUMBERS = A(Y, L(L(A(CONS, ONE, A(TWO, A(INC, ONE))))));
   // Y (\func.\n.CONS n (func (INC n)))
   public static final Expression TAKEHELPER = L(A(L(L( A(A(ISNIL, ONE), 
                                                           THREE, 
                                                           A(CONS, L(A(THREE, A(CONS, A(CAR, TWO), ONE))),
                                                             A(CDR, ONE))))),
                                                   A(CAR, ONE), A(CDR, ONE)));
   /* FUNC = \p.(\f.\r. (ISNIL r) p
                                  (CONS (\a.f (CONS (CAR r) a))
                                        (CDR r)))
                (CAR p) (CDR p)
   */
   public static final Expression TAKE = L(L(A(CAR, A(TWO, TAKEHELPER, A(CONS, L(ONE), ONE)), NIL)));
   // \n.\list.CAR (n HELPER (CONS (\x.x) list)) NIL

   private static final Map<Integer,Expression> cache = new HashMap<Integer,Expression>();
   public static final Expression N(int n) {
      if (cache.containsKey(n)) {
         return cache.get(n);
      } else {
         Expression body = ONE;
         while (n > 0) {
            body = new Expression(TWO, body);
            n--;
         }
         body = L(L(body));
         cache.put(n, body);
         return body;
      }
   }

   public static final Expression A(Expression... ops) {
      if (ops.length == 0) {
         throw new RuntimeException("Not enough ops");
      }
      Expression result = ops[0];
      for (int i = 1; i < ops.length; i++) {
         result = new Expression(result, ops[i]);
      }
      return result;
   }

   public static final Expression L(Expression child) {
      return new Expression(child);
   }
}
