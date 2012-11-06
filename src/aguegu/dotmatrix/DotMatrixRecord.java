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
	private ArrayList<DotMatrixRecordFrame> record;

	public DotMatrixRecord(String filename)
	{
		this.filename = filename;
		record = new ArrayList<DotMatrixRecordFrame>();
	}

	public void save()
	{
		try
		{
			FileOutputStream fos = new FileOutputStream(filename);
			DataOutputStream dos = new DataOutputStream(fos);

			for (DotMatrixRecordFrame f : record)
			{
				dos.write(f.getData());
			}

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

		try
		{
			FileInputStream fis = new FileInputStream(filename);
			DataInputStream dis = new DataInputStream(fis);

			int i = 0;
			int head;

			while ((head = dis.read()) != -1)
			{
				if (head == 0xf2)
				{
					byte[] val = new byte[7 + 64];
					dis.read(val);
				}
				else
				{
					byte[] val = new byte[7];
					dis.read(val);					
				}
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

	public void add(byte[] cache)
	{
		DotMatrixRecordFrame newFrame = new DotMatrixRecordFrame(
				DotMatrixRecordFrame.Type.F2, 0);
		newFrame.setBatch(cache);
		record.add(newFrame);
		save();
	}

}
