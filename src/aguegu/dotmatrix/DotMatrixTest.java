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
		JFrame frame = new JFrame("dot-matrix");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(new DotMatrixPanel());
		frame.setBounds(100, 100, 800, 400);
		// frame.setLocationRelativeTo(null);
		// frame.setSize(800, 600);
		frame.setVisible(true);
	}
}
