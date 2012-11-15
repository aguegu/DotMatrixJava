package aguegu.dotmatrix;

import java.util.Arrays;

public class DotMatrixRecordFrame
{
	private byte[] data;
	private int index;

	enum Type
	{
		All, Batch
	};

	public DotMatrixRecordFrame(Type type, int index)
	{
		int length = 72;
		byte header = (byte) 0xf2;

		if (type == Type.All)
		{
			length = 8;
			header = (byte) 0xf0;
		}

		data = new byte[length];
		data[0] = header;

		this.index = index;
	}

	public DotMatrixRecordFrame(int header, int index)
	{
		int length = 72;
		int head = 0xf2;

		if (header == 0xf0)
		{
			length = 8;
			head = header;
		}

		data = new byte[length];
		data[0] = (byte) head;

		this.index = index;
	}

	public void setBody(byte[] data)
	{
		int length = Math.min(this.data.length - 1, data.length);
		System.arraycopy(data, 0, this.data, 1, length);
	}

	public int getIndex()
	{
		return index;
	}

	public void setIndex(int index)
	{
		this.index = index;
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

	public void setBigSpan(byte bigSpan)
	{
		data[6] = bigSpan;
	}

	public void setAll(byte val)
	{
		if (data[0] == (byte) 0xf0)
		{
			data[7] = val;
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

	public DotMatrix getDotMatrix()
	{
		DotMatrix dm = new DotMatrix();
		if (data[0] == (byte) 0xf2)
			dm.setCache(Arrays.copyOfRange(data, 8, data.length));
		else
			dm.setCache(data[7]);

		return dm;
	}

	public String getCacheString()
	{
		return DotMatrix.cacheString(data);
	}
}
