package sequence;

public class Tile {
	public static final int MASKS[] = new int[]{0x01, 0x08, 0x40};
	boolean movable;
	int tile;
	int which;
	
	public Tile(boolean m, int t) {
		movable = m;
		tile = t;
		which = 0;
	}
}
