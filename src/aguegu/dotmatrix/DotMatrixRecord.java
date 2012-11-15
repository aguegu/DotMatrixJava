package aguegu.dotmatrix;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DotMatrixRecord
{
	private ArrayList<DotMatrixRecordFrame> record;

	public DotMatrixRecord()
	{
		record = new ArrayList<DotMatrixRecordFrame>();
	}

	public DotMatrixRecordFrame[] getFrames()
	{
		return record.toArray((DotMatrixRecordFrame[]) Array.newInstance(
				DotMatrixRecordFrame.class, record.size()));
	}

	public void save(File file)
	{
		try
		{
			FileOutputStream fos = new FileOutputStream(file);
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

	public void readRecord(File file)
	{
		try
		{
			FileInputStream fis = new FileInputStream(file);
			DataInputStream dis = new DataInputStream(fis);

			record.clear();			
			int index = 0;
			byte[] val = new byte[72];

			while (dis.read(val) != -1)
			{
				DotMatrixRecordFrame dmrf = new DotMatrixRecordFrame(index);
				dmrf.setData(val);
				record.add(index, dmrf);
				index++;
			}

			dis.close();
			fis.close();
		}
		catch (IOException e)
		{
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

	public int getLength()
	{
		return record.size();
	}

	public void insert(byte[] cache, int index)
	{
		if (index == -1)
		{
			index = record.size();
		}

		for (int i = index; i < record.size(); i++)
		{
			record.get(i).setIndex(i + 1);
		}

		DotMatrixRecordFrame newFrame = new DotMatrixRecordFrame(index);
		//newFrame.setBatch(cache);
		record.add(newFrame);

		this.sortRecord();
	}

	public void append(byte[] cache, int index)
	{
		if (index == -1)
		{
			index = record.size() - 1;
		}

		for (int i = index + 1; i < record.size(); i++)
		{
			record.get(i).setIndex(i + 1);
		}

		DotMatrixRecordFrame newFrame = new DotMatrixRecordFrame(index + 1);
		//newFrame.setBatch(cache);
		record.add(newFrame);

		this.sortRecord();
	}

	public void update(byte[] cache, int index)
	{
		//record.get(index).setBatch(cache);
	}

	public DotMatrixRecordFrame getFrame(int index)
	{
		return record.get(index);
	}

	public void remove(int index)
	{
		record.remove(index);

		for (int i = 0; i < record.size(); i++)
		{
			record.get(i).setIndex(i);
		}
	}

	public void clear()
	{
		record.clear();
	}

	private void sortRecord()
	{
		Collections.sort(record, new FrameComparator());
	}

	public class FrameComparator implements Comparator<DotMatrixRecordFrame>
	{
		@Override
		public int compare(DotMatrixRecordFrame o1, DotMatrixRecordFrame o2)
		{
			return o1.getIndex() - o2.getIndex();

		}
	}
}
