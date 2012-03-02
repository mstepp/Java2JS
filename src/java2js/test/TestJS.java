package java2js.test;

import java2js.*;

public class TestJS {
    public static void main(String args[]) {
	int top = 100;
	Output.stdout.println(top /  7); //  14
	Output.stdout.println(top / -7); // -14
	Output.stdout.println(top %  7); // 2
	Output.stdout.println(top % -7); // 2
	
	top = -100;
	Output.stdout.println(top /  7); // -14
	Output.stdout.println(top / -7); //  14
	Output.stdout.println(top %  7); // -2
	Output.stdout.println(top % -7); // -2

	top = 560567564;
	Output.stdout.println(top / 11); // 50960687
	
	long topL = 100L;
	Output.stdout.println(topL /  7L); //  14
	Output.stdout.println(topL / -7L); // -14
	Output.stdout.println(topL %  7L); // 2
	Output.stdout.println(topL % -7L); // 2

	topL = -100L;
	Output.stdout.println(topL /  7L); // -14
	Output.stdout.println(topL / -7L); //  14
	Output.stdout.println(topL %  7L); // -2
	Output.stdout.println(topL % -7L); // -2
    }
}
