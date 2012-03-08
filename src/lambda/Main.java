package lambda;

import static lambda.Functions.*;

public class Main {
    public static void run(Printer printer) {
	// Y (\fact.\n.(ISZERO? n) ONE (MULTIPLY n (fact (DEC n))))
	Expression FACTORIAL = A(Y, L(L( A( A(ISZERO, ONE), N(1), A(MULTIPLY, ONE, A(TWO, A(DEC, ONE)))))));
	Expression apply = THREE; // A(FACTORIAL, N(4));
	while (apply.eval()) {}
	apply.dump(printer);
    }
}
