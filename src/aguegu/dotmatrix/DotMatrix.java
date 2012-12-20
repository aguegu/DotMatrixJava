package aguegu.dotmatrix;

public class DotMatrix
{
	public static final int DOT_LENGTH = 512;
	public static final int CACHE_LENGTH = 64;

	private static final int circle[][] = {
			{ 27, 28, 36, 35 },
			{ 18, 19, 20, 21, 29, 37, 45, 44, 43, 42, 34, 26 },
			{ 9, 10, 11, 12, 13, 14, 22, 30, 38, 46, 54, 53, 52, 51, 50, 49,
					41, 33, 25, 17 },
			{ 0, 1, 2, 3, 4, 5, 6, 7, 15, 23, 31, 39, 47, 55, 63, 62, 61, 60,
					59, 58, 57, 56, 48, 40, 32, 24, 16, 8 } };

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

	public void reverse()
	{
		for (int i = 0; i < dot.length; i++)
			dot[i] = !dot[i];
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

	static public String cacheString(byte[] cache)
	{
		String s = new String();

		for (int i = 0; i < cache.length; i++)
		{
			if (i % 8 == 0 && i > 0)
				s = s.concat("\n");
			s = s.concat(String.format("0x%02x, ", cache[i]));
		}
		return s;
	}

	static public int byteToInt(byte c)
	{
		int i = ((int) c + 256) % 256;
		return i;
	}

	public String cacheString()
	{
		return cacheString(this.getCache());
	}

	public void setCache(byte cache)
	{
		for (int i = 0; i < CACHE_LENGTH; i++)
		{
			this.setCache(i, cache);
		}
	}

	public void setCache(int index, byte cache)
	{
		int i = index * 8;
		try
		{
			for (int j = 0; j < 8; j++)
				dot[i + j] = (cache & (0x80 >> j)) > 0;
		}
		catch (ArrayIndexOutOfBoundsException ex)
		{
			System.out.println(ex.getStackTrace());
		}
	}

	public void setCache(byte[] cache, int from)
	{
		try
		{
			for (int i = from, j = 0; i < cache.length; i++, j++)
			{
				this.setCache(j, cache[i]);
			}
		}
		catch (ArrayIndexOutOfBoundsException ex)
		{
			System.out.println(ex.getStackTrace());
		}
	}

	public void setCache(byte[] cache)
	{
		try
		{
			for (int i = 0; i < cache.length; i++)
			{
				this.setCache(i, cache[i]);
			}
		}
		catch (ArrayIndexOutOfBoundsException ex)
		{
			System.out.println(ex.getStackTrace());
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

	public void rotate(int r, boolean clockwise, boolean recycle)
	{
		r %= 4;

		int[] p = circle[r];
		int length = p.length;

		for (int z = 0; z < 8; z++)
		{
			if (clockwise)
			{
				boolean tmp = dot[getIndex(p[0] % 8, p[0] / 8, z)];

				for (int i = 0; i < p.length - 1; i++)
				{
					dot[getIndex(p[i] % 8, p[i] / 8, z)] = dot[getIndex(
							p[i + 1] % 8, p[i + 1] / 8, z)];
				}

				dot[getIndex(p[p.length - 1] % 8, p[p.length - 1] / 8, z)] = recycle ? tmp
						: false;
			}
			else
			{
				boolean tmp = dot[getIndex(p[p.length - 1] % 8,
						p[p.length - 1] / 8, z)];

				for (int i = length - 1; i > 0; i--)
				{
					dot[getIndex(p[i] % 8, p[i] / 8, z)] = dot[getIndex(
							p[i - 1] % 8, p[i - 1] / 8, z)];
				}

				dot[getIndex(p[0] % 8, p[0] / 8, z)] = recycle ? tmp
						: false;
			}
		}

		System.out.println(length);
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
