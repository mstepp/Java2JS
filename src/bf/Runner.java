package bf;

public class Runner {
   public static void main(String args[]) {
      final String input = args[1];
      IO stream = new IO() {
            int index = 0;
            public int read() {
               if (index >= input.length()) {
                  return -1;
               } else {
                  return input.charAt(index++) & 0xFF;
               }
            }
            public void write(int value) {
               System.out.print((char)(value&0xFF));
            }
         };
      State state = new State(stream, 1000);
      Command program = Parser.parse(args[0]);
      Interpreter interpreter = new Interpreter(program);
      interpreter.interpret(state);
   }
}
