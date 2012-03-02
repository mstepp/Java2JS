package bf;

public class State {
    private final int[] data;
    private int dataIndex;
    private final IO stream;

    public State(IO _stream, int _maxDataSize) {
	this.data = new int[_maxDataSize];
	this.dataIndex = 0;
	this.stream = _stream;

	// TODO
	this.stream.write(this.data[0]);
    }

    public int getData() {
	return this.data[this.dataIndex];
    }

    public void setData(int value) {
	this.data[this.dataIndex] = value;
    }

    public void forward() {
	this.dataIndex++;
    }

    public void backward() {
	if (this.dataIndex>0)
	    this.dataIndex--;
    }

    public IO getStream() {
	return this.stream;
    }
}
