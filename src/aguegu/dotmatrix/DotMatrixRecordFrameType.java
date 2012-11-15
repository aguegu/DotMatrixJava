package aguegu.dotmatrix;

public enum DotMatrixRecordFrameType
{
	ALL(0xf0), BATCH(0xf2);

	private final byte val;

	DotMatrixRecordFrameType(int val)
	{
		this.val = (byte) val;
	}

	byte value()
	{
		return val;
	}
}
