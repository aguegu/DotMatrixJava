package aguegu.dotmatrix;

public enum DMAttachment
{
	NONE(0x00), UPPER_LED(0x01), BUTTOM_LED(0x02), BOTH(0x03);

	private final byte val;

	DMAttachment(int val)
	{
		this.val = (byte) val;
	}

	byte value()
	{
		return val;
	}

	public static DMAttachment getDMAttachment(byte val)
	{
		DMAttachment attachment = NONE;
		switch (val)
		{
		case 0x01:
			attachment = UPPER_LED;
			break;
		case 0x02:
			attachment = BUTTOM_LED;
			break;
		case 0x03:
			attachment = BOTH;
			break;		
		}
		return attachment;
	}
}
