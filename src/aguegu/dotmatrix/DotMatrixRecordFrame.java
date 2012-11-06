package aguegu.dotmatrix;

public class DotMatrixRecordFrame
{
	enum Type
	{
		F0, F1, F2
	};

	private byte[] data;
	private int index;

	public DotMatrixRecordFrame(Type type, int index)
	{
		byte header = (byte) 0xf0;
		int length = 8;

		switch (type)
		{
		case F0:
			header = (byte) 0xf0;
			length = 8;
			break;
		case F1:
			header = (byte) 0xf1;
			length = 8;
			break;
		case F2:
			header = (byte) 0xf2;
			length = 64 + 8;
			break;
		}

		data = new byte[length];
		data[0] = header;

		this.index = index;
	}

	public int getIndex()
	{
		return index;
	}

	public void setMode(byte mode)
	{
		data[1] = mode;
	}

	public void setBrightness(byte brightness)
	{
		data[2] = brightness;
	}

	public void setDecoratedLed(byte decorateLed)
	{
		data[3] = decorateLed;
	}

	public void setSpan(short span)
	{
		data[4] = ((Integer) (span >> 8)).byteValue();
		data[5] = ((Integer) (span & 0xff)).byteValue();
	}

	public void setCol(byte index, byte val)
	{
		if (data[0] == (byte) 0xf1)
		{
			data[6] = index;
			data[7] = val;
		}
	}

	public void setAll(byte val)
	{
		if (data[0] == (byte) 0xf0)
		{
			data[6] = val;
		}
	}

	public void setBatch(byte[] val)
	{
		if (data[0] == (byte) 0xf2)
			System.arraycopy(val, 0, data, 8,
					Math.min(data.length - 8, val.length));
	}

	public byte[] getData()
	{
		return data;
	}

	public int getLength()
	{
		return data.length;
	}
}
