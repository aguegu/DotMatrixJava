package aguegu.dotmatrix;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class DotMatrixRecord
{
	public static void main(String[] args) throws IOException
	{
		FileOutputStream fos = new FileOutputStream("record.dat");
		DataOutputStream dos = new DataOutputStream(fos);
		
		byte[] data = new byte[256];
		
		for (int i=0; i<256; i++)
			data[i] = (byte)i;
		
		dos.write(data);
		dos.close();
		fos.close();
	}

}
