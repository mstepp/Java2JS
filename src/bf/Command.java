package bf;

public abstract class Command {
    protected final Command next;
    public Command(Command _next) {
	this.next = _next;
    }
    public abstract Command execute(State state);
}

class ForwardCommand extends Command {
    public ForwardCommand(Command _next) {
	super(_next);
    }
    public Command execute(State state) {
	state.forward();
	return this.next;
    }
}

class BackwardCommand extends Command {
    public BackwardCommand(Command _next) {
	super(_next);
    }
    public Command execute(State state) {
	state.backward();
	return this.next;
    }
}

class IncrementCommand extends Command {
    public IncrementCommand(Command _next) {
	super(_next);
    }
    public Command execute(State state) {
	state.setData(state.getData()+1);
	return this.next;
    }
}

class DecrementCommand extends Command {
    public DecrementCommand(Command _next) {
	super(_next);
    }
    public Command execute(State state) {
	state.setData(state.getData()-1);
	return this.next;
    }
}

class OutputCommand extends Command {
    public OutputCommand(Command _next) {
	super(_next);
    }
    public Command execute(State state) {
	state.getStream().write(state.getData());
	return this.next;
    }
}

class InputCommand extends Command {
    public InputCommand(Command _next) {
	super(_next);
    }
    public Command execute(State state) {
	state.setData(state.getStream().read());
	return this.next;
    }
}

class LoopCommand extends Command {
    private Command start;
    public LoopCommand(Command _next) {
	super(_next);
    }
    public Command execute(State state) {
	if (state.getData() == 0) {
	    // break
	    return this.next;
	} else {
	    return this.start;
	}
    }
    protected void setStart(Command start) {
	if (this.start == null) {
	    this.start = start;
	} else {
	    throw new IllegalArgumentException();
	}
    }
}
