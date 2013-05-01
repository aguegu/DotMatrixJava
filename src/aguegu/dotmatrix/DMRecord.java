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

public class DMRecord {
	private ArrayList<DMRecordFrame> record;

	public DMRecord() {
		record = new ArrayList<DMRecordFrame>();
	}

	public DMRecordFrame[] getFrames() {
		return record.toArray((DMRecordFrame[]) Array.newInstance(
				DMRecordFrame.class, record.size()));
	}

	public void save(File file) {
		try {
			FileOutputStream fos = new FileOutputStream(file);
			DataOutputStream dos = new DataOutputStream(fos);

			for (DMRecordFrame f : record) {
				dos.write(f.getData());
			}

			dos.close();
			fos.close();
		} catch (IOException ex) {
			System.out.println(ex);
		}
	}

	public void readRecord(File file) {
		try {
			FileInputStream fis = new FileInputStream(file);
			DataInputStream dis = new DataInputStream(fis);

			record.clear();
			int index = 0;
			byte[] val = new byte[72];

			while (dis.read(val) != -1) {
				DMRecordFrame dmrf = new DMRecordFrame(index);
				dmrf.setData(val);
				record.add(index, dmrf);
				index++;
			}

			dis.close();
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<String> getList() {
		ArrayList<String> list = new ArrayList<String>();

		for (int i = 0; i < record.size(); i++) {
			list.add(Integer.toString(i));
		}
		return list;
	}

	public int getLength() {
		return record.size();
	}

	public void insert(byte[] cache, int index) {
		if (index == -1) {
			index = record.size();
		}

		for (int i = index; i < record.size(); i++) {
			record.get(i).setIndex(i + 1);
		}

		DMRecordFrame newFrame = new DMRecordFrame(index);
		newFrame.setData(cache);
		record.add(newFrame);

		this.sortRecord();
	}

	public void append(byte[] cache, int index) {
		if (index == -1) {
			index = record.size() - 1;
		}

		for (int i = index + 1; i < record.size(); i++) {
			record.get(i).setIndex(i + 1);
		}

		DMRecordFrame newFrame = new DMRecordFrame(index + 1);
		newFrame.setData(cache);
		record.add(newFrame);

		this.sortRecord();
	}

	public void update(byte[] cache, int index) {
		record.get(index).setData(cache);
	}

	public DMRecordFrame getFrame(int index) {
		return record.get(index);
	}

	public void remove(int index) {
		record.remove(index);

		for (int i = 0; i < record.size(); i++) {
			record.get(i).setIndex(i);
		}
	}

	public void clear() {
		record.clear();
	}

	public void setBrightness(int brightness) {
		for (DMRecordFrame dmrf : record) {
			dmrf.setBrightness(brightness);
		}
	}

	public void setMode(DMMode mode) {
		for (DMRecordFrame dmrf : record) {
			dmrf.setMode(mode);
		}
	}

	public void setSpan(int span) {
		for (DMRecordFrame dmrf : record) {
			dmrf.setSpan(span);
		}
	}

	private void sortRecord() {
		Collections.sort(record, new FrameComparator());
	}

	public class FrameComparator implements Comparator<DMRecordFrame> {
		@Override
		public int compare(DMRecordFrame o1, DMRecordFrame o2) {
			return o1.getIndex() - o2.getIndex();

		}
	}
}
