package aguegu.dotmatrix;

public class DotMatrixRecordFrame
{
	private DotMatrix dm;
	private DMMode mode;
	private byte brightness;
	private DMAttachment attachment;
	private int index;
	private int smallSpan;
	private int bigSpan;

	public DotMatrixRecordFrame(int index)
	{
		this.index = index;

		dm = new DotMatrix();
		mode = DMMode.XYZ;
		brightness = (byte) 0xff;
		attachment = DMAttachment.NONE;
	}

	public void setData(byte[] data)
	{
		mode = DMMode.getMode(data[1]);
		brightness = data[2];
		attachment = DMAttachment.getDMAttachment(data[3]);
		smallSpan = (data[4] << 8) | data[5];
		bigSpan = (data[6] << 8) | data[7];

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

	public void setMode(DMMode mode)
	{
		this.mode = mode;
	}

	public DMMode getMode()
	{
		return mode;
	}

	public void setBrightness(byte brightness)
	{
		this.brightness = brightness;
	}

	public void setAttachment(DMAttachment attachment)
	{
		this.attachment = attachment;
	}

	public void setSmallSpan(short smallSpan)
	{
		this.smallSpan = smallSpan;
	}

	public void setBigSpan(byte bigSpan)
	{
		this.bigSpan = bigSpan;
	}

	public byte[] getData()
	{
		byte[] data = new byte[72];

		data[0] = (byte) 0xf2;
		data[1] = mode.value();
		data[2] = brightness;
		data[3] = attachment.value();
		data[4] = ((Integer) (smallSpan >> 8)).byteValue();
		data[5] = ((Integer) (smallSpan & 0xff)).byteValue();
		data[6] = ((Integer) (bigSpan >> 8)).byteValue();
		data[7] = ((Integer) (bigSpan & 0xff)).byteValue();		
		System.arraycopy(dm.getCache(), 0, data, 8, DotMatrix.CACHE_LENGTH);
		
		return data;
	}

	public String getCacheString()
	{
		return DotMatrix.cacheString(getData());
	}
}
