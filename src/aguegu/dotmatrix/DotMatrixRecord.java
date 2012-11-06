package aguegu.dotmatrix;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class DotMatrixRecord
{
	private String filename;

	public DotMatrixRecord(String filename)
	{
		this.filename = filename;
	}

	public void save(byte[] data)
	{
		try
		{
			FileOutputStream fos = new FileOutputStream(filename);
			DataOutputStream dos = new DataOutputStream(fos);

			dos.write(data);
			dos.close();
			fos.close();
		}
		catch (IOException ex)
		{
			System.out.println(ex);
		}
	}

	public ArrayList<String> getFrames()
	{
		ArrayList<String> frames = new ArrayList<String>();

		byte[] data = new byte[4];

		try
		{
			FileInputStream fis = new FileInputStream(filename);
			DataInputStream dis = new DataInputStream(fis);

			int i = 0;

			while (dis.read(data) != -1)
			{
				frames.add(String.format("%d", i++));
			}
			
			dis.close();
			fis.close();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();			
		}

		return frames;
	}

}
