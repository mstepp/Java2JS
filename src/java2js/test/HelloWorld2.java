package java2js.test;

import java2js.*;

public class HelloWorld2 {
    public static void main(String args[]) {
	try {
	    switch (args.length) {
	    case 0: throw new java.io.IOException();
	    case 1: throw new java.lang.ArrayIndexOutOfBoundsException();
	    case 2: throw new java.lang.NumberFormatException();
	    default: throw new java.lang.RuntimeException();
	    }
	} catch (java.io.IOException e) {
	    Output.stdout.println("IOException");
	} catch (java.lang.ArrayIndexOutOfBoundsException e) {
	    Output.stdout.println("ArrayIndexOutOfBoundsException");
	} catch (java.lang.NumberFormatException e) {
	    Output.stdout.println("NumberFormatException");
	} catch (Throwable t) {
	    Output.stdout.println("RuntimeException");
	}
    }
}
