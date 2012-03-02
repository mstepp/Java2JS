package java2js;

public class Output {
    public static final Output stdout = new Output("stdout");
    public static final Output stderr = new Output("stderr");
    
    private final String id;

    private Output(String _id) {
	this.id = _id;
    }

    public native void println(String str);
    public native void println(int i);
    public native void println(long l);
    public native void println(double d);
}
