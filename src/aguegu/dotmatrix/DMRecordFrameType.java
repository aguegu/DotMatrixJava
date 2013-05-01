package aguegu.dotmatrix;

public enum DMRecordFrameType {
	ALL(0xf0), BATCH(0xf2);

	private final byte val;

	DMRecordFrameType(int val) {
		this.val = (byte) val;
	}

	byte value() {
		return val;
	}
}
