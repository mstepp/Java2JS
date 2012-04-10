package bf;

public class JSIO {
    private final String input, outputID;
    private int inputIndex = 0;
    public JSIO(String _input, String _outputID) {
	this.input = _input;
	this.outputID = _outputID;
    }
    public int read() {
	if (this.inputIndex >= this.input.length()) {
	    return -1;
	} else {
	    return this.input.charAt(this.inputIndex++) & 0xFF;
	}
    }
    public native void write(int data);
}
