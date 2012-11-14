package aguegu.dotmatrix;

public class DotMatrix
{
	public static final int DOT_LENGTH = 512;
	public static final int CACHE_LENGTH = 64;
	private boolean[] dot;

	public static enum Direction
	{
		X_POSI, X_NEGA, Y_POSI, Y_NEGA, Z_POSI, Z_NEGA,
	};

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
		byte[] cache = new byte[CACHE_LENGTH];

		for (int i = 0; i < dot.length; i++)
		{
			int index = i / 8;
			if (dot[i])
				cache[index] |= 0x80 >> (i % 8);
		}

		return cache;
	}
	
	public String cacheString()
	{
		byte[] data = this.getCache();
		String s = new String();
		for (int i = 0; i < data.length; i++)
		{
			if (i % 8 == 0 && i > 0)
				s = s.concat("\n");
			s = s.concat(String.format("0x%02x, ", data[i]));
		}
		return s;
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

	private int getIndex(int x, int y, int z)
	{
		return (64 * z + 8 * y + x);
	}

	public void move(Direction direction, boolean recycle)
	{
		for (int x = 0; x < 8; x++)
		{
			for (int y = 0; y < 8; y++)
			{
				int z;
				boolean temp;

				switch (direction)
				{
				case X_POSI:
					z = 7;
					temp = dot[getIndex(z, x, y)];
					for (; z > 0; z--)
						dot[getIndex(z, x, y)] = dot[getIndex(z - 1, x, y)];
					dot[getIndex(z, x, y)] = recycle ? temp : false;
					break;
				case X_NEGA:
					z = 0;
					temp = dot[getIndex(z, x, y)];
					for (; z < 7; z++)
						dot[getIndex(z, x, y)] = dot[getIndex(z + 1, x, y)];
					dot[getIndex(z, x, y)] = recycle ? temp : false;
					break;
				case Y_NEGA:
					z = 0;
					temp = dot[getIndex(y, z, x)];
					for (; z < 7; z++)
						dot[getIndex(y, z, x)] = dot[getIndex(y, z + 1, x)];
					dot[getIndex(y, z, x)] = recycle ? temp : false;
					break;
				case Y_POSI:
					z = 7;
					temp = dot[getIndex(y, z, x)];
					for (; z > 0; z--)
						dot[getIndex(y, z, x)] = dot[getIndex(y, z - 1, x)];
					dot[getIndex(y, z, x)] = recycle ? temp : false;
					break;
				case Z_NEGA:
					z = 0;
					temp = dot[getIndex(x, y, z)];
					for (; z < 7; z++)
						dot[getIndex(x, y, z)] = dot[getIndex(x, y, z + 1)];
					dot[getIndex(x, y, z)] = recycle ? temp : false;
					break;
				case Z_POSI:
					z = 7;
					temp = dot[getIndex(x, y, z)];
					for (; z > 0; z--)
						dot[getIndex(x, y, z)] = dot[getIndex(x, y, z - 1)];
					dot[getIndex(x, y, z)] = recycle ? temp : false;
					break;
				default:
					break;
				}
			}
		}
	}

}
