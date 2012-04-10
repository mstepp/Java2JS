package bf;

public class Parser {
    private static class Stack {
	LoopCommand loop;
	Stack prev;
	Stack(LoopCommand _loop, Stack _prev) {
	    this.loop = _loop;
	    this.prev = _prev;
	}
    }

    public static Command parse(String program) {
	int index = program.length()-1;
	Command last = null;
	Stack stack = null;
	
	while (index >= 0) {
	    switch (program.charAt(index)) {
	    case '>':
		last = new ForwardCommand(last);
		index--;
		break;

	    case '<':
		last = new BackwardCommand(last);
		index--;
		break;

	    case '+':
		last = new IncrementCommand(last);
		index--;
		break;
		
	    case '-':
		last = new DecrementCommand(last);
		index--;
		break;
		
	    case '.':
		last = new OutputCommand(last);
		index--;
		break;

	    case ',':
		last = new InputCommand(last);
		index--;
		break;

	    case ']': {
		LoopCommand loop = new LoopCommand(last);
		last = loop;
		stack = new Stack(loop, stack);
		index--;
		break;
	    }

	    case '[': {
		if (stack == null)
		    throw new RuntimeException("Stack underflow");
		LoopCommand loop = stack.loop;
		stack = stack.prev;
		loop.setStart(last);
		last = loop;
		index--;
		break;
	    }

	    default:
		// skip
		index--;
		break;
	    }
	}

	if (stack != null)
	    throw new RuntimeException("Stack overflow");
	return last;
    }
}
