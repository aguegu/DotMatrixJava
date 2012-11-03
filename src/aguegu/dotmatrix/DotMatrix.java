package aguegu.dotmatrix;

public class DotMatrix
{
	public static final int LENGTH = 512; 
	private boolean[] dot;

	public DotMatrix()
	{
		dot = new boolean[LENGTH];
	}

	public boolean[] getDot()
	{
		return dot;
	}

	public boolean getDot(int index)
	{
		try
		{
			return dot[index];
		}
		catch (ArrayIndexOutOfBoundsException ex)
		{
			return false;
		}
	}

	public void setDot(int index, boolean val)
	{
		try
		{
			dot[index] = val;
		}
		catch (ArrayIndexOutOfBoundsException ex)
		{
		}
	}

	public void reverseDot(int index)
	{
		try
		{
			dot[index] = !dot[index];
		}
		catch (ArrayIndexOutOfBoundsException ex)
		{
		}
	}

	public byte[] getCache()
	{
		byte[] data = new byte[64];

		for (int i = 0; i < dot.length; i++)
		{
			int index = i / 8;
			if (dot[i])
				data[index] |= 0x80 >> (i % 8);
		}

		return data;
	}

}
