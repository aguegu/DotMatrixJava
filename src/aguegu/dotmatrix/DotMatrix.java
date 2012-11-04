package aguegu.dotmatrix;

public class DotMatrix
{
	public static final int DOT_LENGTH = 512;
	public static final int CACHE_LENGTH = 64;
	private boolean[] dot;

	public DotMatrix()
	{
		dot = new boolean[DOT_LENGTH];
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
		byte[] cache = new byte[64];

		for (int i = 0; i < dot.length; i++)
		{
			int index = i / 8;
			if (dot[i])
				cache[index] |= 0x80 >> (i % 8);
		}

		return cache;
	}

	public void setCache(byte[] cache)
	{
		try
		{
			for (int i = 0; i < cache.length; i++)
			{
				int index = i * 8;
				for (int j = 0; j < 8; j++)
				{
					dot[index + j] = (cache[i] & (0x80 >> j)) > 0;
				}
			}
		}
		catch (ArrayIndexOutOfBoundsException ex)
		{
		}
	}

	public void clear(boolean val)
	{
		for (int i = 0; i < dot.length; i++)
		{
			dot[i] = val;
		}
	}
}
