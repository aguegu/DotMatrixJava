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

	public void readRecord()
	{
		try
		{
			FileInputStream fis = new FileInputStream(filename);
			DataInputStream dis = new DataInputStream(fis);

			int head;
			int index = 0;

			while ((head = dis.read()) != -1)
			{
				int length = 7;
				length += head == 0xf2 ? 64 : 0;

				byte[] val = new byte[length];

				dis.read(val);
				DotMatrixRecordFrame dmrf = new DotMatrixRecordFrame(
						DotMatrixRecordFrame.Type.values()[head - 0xf0], index);
				dmrf.setBody(val);
				record.add(index, dmrf);
				index++;
			}

			dis.close();
			fis.close();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ArrayList<String> getList()
	{
		ArrayList<String> list = new ArrayList<String>();

		for (int i = 0; i < record.size(); i++)
		{
			list.add(Integer.toString(i));
		}
		return list;
	}

	public void add(byte[] cache)
	{
		DotMatrixRecordFrame newFrame = new DotMatrixRecordFrame(
				DotMatrixRecordFrame.Type.F2, 0);
		newFrame.setBatch(cache);
		record.add(newFrame);
	}

	public DotMatrixRecordFrame getFrame(int index)
	{
		return record.get(index);
	}

}
