package aguegu.dotmatrix;

import javax.swing.JFrame;
import aguegu.dotmatrix.DotMatrixPanel;

public class DotMatrixTest
{

	public static void main(String[] args)
	{
		DotMatrixTest dmt = new DotMatrixTest();
		dmt.go();
	}

	public void go()
	{
		JFrame frame = new JFrame("dot-matrix on Java");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(new DotMatrixPanel());
		//frame.setBounds(100, 100, 1000, 400);
		frame.setLocation(100, 100);
		// frame.setLocationRelativeTo(null);
		frame.setSize(frame.getContentPane().getWidth(), frame.getContentPane().getHeight());
		frame.setResizable(false);
		
		frame.setVisible(true);
	}
}
