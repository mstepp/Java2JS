package java2js.test;

import java2js.*;

public class Test {
    public static void main(String args[]) {
	Output.stdout.println(100L / 7L);
	Output.stdout.println(100L / -7L);
	Output.stdout.println(100L % 7L);
	Output.stdout.println(100L % -7L);

	Output.stdout.println(-100L / 7L);
	Output.stdout.println(-100L / -7L);
	Output.stdout.println(-100L % 7L);
	Output.stdout.println(-100L % -7L);

	Output.stdout.println(0x8000000 * 0x8000000);
    }
}
