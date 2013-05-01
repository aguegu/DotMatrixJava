package aguegu.dotmatrix;

public enum DMMode {
	XYZ((byte) 0x00), YZX((byte) 0x01), ZXY((byte) 0x02);

	private final byte val;

	DMMode(byte val) {
		this.val = val;
	}

	byte value() {
		return val;
	}

	public static DMMode getMode(byte val) {
		DMMode mode = XYZ;
		switch (val) {
		case 0x01:
			mode = YZX;
			break;
		case 0x02:
			mode = ZXY;
			break;
		}
		return mode;
	}

	public static DMMode getMode(String val) {
		DMMode mode = XYZ;
		switch (val) {
		case "YZX":
			mode = YZX;
			break;
		case "ZXY":
			mode = ZXY;
			break;
		}
		return mode;
	}
}
