package bf;

public class Interpreter {
   private final Command first;

   public Interpreter(Command _first) {
      this.first = _first;
   }

   public void interpret(State state) {
      Command current = this.first;
      while (current != null) {
         current = current.execute(state);
      }
   }
}
