package aguegu.dotmatrix;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

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

}
