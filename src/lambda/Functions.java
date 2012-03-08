package lambda;

public class Functions {
    public static final Expression ONE = new Expression(1);
    public static final Expression TWO = new Expression(2);
    public static final Expression THREE = new Expression(3);
    public static final Expression FOUR = new Expression(4);
    
    public static final Expression TRUE = L(L(TWO)); // \a.\b.a
    public static final Expression FALSE = L(L(ONE)); // \a.\b.b
    public static final Expression CONS = L(L(L(A(ONE, TWO, THREE)))); // \a.\b.\f.f a b
    public static final Expression CAR = TRUE;
    public static final Expression CDR = FALSE;
    public static final Expression ZERO = FALSE; // \f.\x.x
    public static final Expression INC = L(L(L(A(THREE, TWO, ONE)))); // \m.\f.\x.m f x
    public static final Expression ADD = L(L(L(L(A(A(FOUR, TWO), A(THREE, TWO, ONE)))))); // \m.\n.\f.\x.(m f) (n f x)
    public static final Expression MULTIPLY = L(L(A(ONE, A(ADD, TWO), ZERO))); // \m.\n.n (ADD m) ZERO
    public static final Expression DEC = L(A(ONE,     L(A(CONS, A(CDR, ONE), A(INC, A(CDR, ONE)))),   A(CONS, ZERO, ZERO))); // \n.n (\p. (CONS (CDR p) (INC (CDR p)))) (CONS ZERO ZERO)
    public static final Expression MONUS = L(L(A(ONE, DEC, TWO))); // \n.\m.m DEC n
    public static final Expression Y = L(A(L(A(TWO,A(ONE,ONE))), L(A(TWO,A(ONE,ONE))))); // \f. (\x.f (x x)) (\x. f (x x))
    public static final Expression ISZERO = L(A(ONE, L(FALSE), TRUE)); // \n.n (\x.FALSE) TRUE
    
    public static final Expression N(int n) {
	Expression body = ONE;
	while (n > 0) {
	    body = new Expression(TWO, body);
	}
	return L(L(body));
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
