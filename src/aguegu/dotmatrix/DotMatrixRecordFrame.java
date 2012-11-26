package aguegu.dotmatrix;

public class DotMatrixRecordFrame
{
	private DotMatrix dm;
	private DMMode mode;
	private int brightness;
	private DMAttachment attachment;
	private int index;
	private int minorSpan;
	private int majorSpan;

	public DotMatrixRecordFrame(int index)
	{
		this.index = index;

		dm = new DotMatrix();
		mode = DMMode.XYZ;
		brightness = (byte) 0xff;
		attachment = DMAttachment.NONE;
		minorSpan = 0x0000;
		majorSpan = 0x00c0;
	}

	public void setData(byte[] data)
	{
		mode = DMMode.getMode(data[1]);
		brightness = DotMatrix.byteToInt(data[2]);
		attachment = DMAttachment.getDMAttachment(data[3]);
		minorSpan = (DotMatrix.byteToInt(data[4]) << 8)
				| DotMatrix.byteToInt(data[5]);
		majorSpan = (DotMatrix.byteToInt(data[6]) << 8)
				| DotMatrix.byteToInt(data[7]);

		dm.setCache(data, 8);

	}

	public int getIndex()
	{
		return index;
	}

	public void setIndex(int index)
	{
		this.index = index;
	}

	public DotMatrix getDotMatrix()
	{
		return dm;
	}

	public void setMode(DMMode mode)
	{
		this.mode = mode;
	}

	public DMMode getMode()
	{
		return mode;
	}

	public void setBrightness(int brightness)
	{
		this.brightness = brightness % 256;
	}

	public int getBrightness()
	{
		return brightness;
	}

	public void setAttachment(DMAttachment attachment)
	{
		this.attachment = attachment;
	}

	public DMAttachment getAttachment()
	{
		return this.attachment;
	}

	public void setMinorSpan(int minorSpan)
	{
		this.minorSpan = minorSpan % 65536;
	}

	public int getMinorSpan()
	{
		return minorSpan;
	}

	public void setMajorSpan(int majorSpan)
	{
		this.majorSpan = majorSpan % 65536;
	}

	public int getMajorSpan()
	{
		return majorSpan;
	}

	public byte[] getData()
	{
		byte[] data = new byte[72];

		data[0] = (byte) 0xf2;
		data[1] = mode.value();
		data[2] = (byte) brightness;
		data[3] = attachment.value();
		data[4] = (byte) (minorSpan >> 8);
		data[5] = (byte) (minorSpan & 0xff);
		data[6] = (byte) (majorSpan >> 8);
		data[7] = (byte) (majorSpan & 0xff);
		System.arraycopy(dm.getCache(), 0, data, 8, DotMatrix.CACHE_LENGTH);

		return data;
	}

	public String getCacheString()
	{
		return DotMatrix.cacheString(getData());
	}
}
