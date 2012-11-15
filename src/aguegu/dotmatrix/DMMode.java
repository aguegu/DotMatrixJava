package aguegu.dotmatrix;

public enum DMMode
{
	XYZ(0x00), YZX(0x01), ZXY(0x02);

	private final byte val;

	DMMode(int val)
	{
		this.val = (byte) val;
	}
	
	byte value()
	{
		return val;
	}
}
