package aguegu.dotmatrix;

public enum DMAttachment
{
	UPPER_LED(0x01), BUTTOM_LED(0x02), BOTH(0x03);
	
	private final byte val;
	
	DMAttachment(int val)
	{
		this.val = (byte)val;
	}	
	
	byte value()
	{
		return val;
	}
}
