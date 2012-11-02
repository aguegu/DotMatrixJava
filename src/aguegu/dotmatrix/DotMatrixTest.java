package aguegu.dotmatrix;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import aguegu.dotmatrix.DotMatrixPanel;

public class DotMatrixTest
{
	public static DotMatrixPanel dmp;
	public static JTextArea textArea;

	public static void main(String[] args)
	{
		DotMatrixTest dmt = new DotMatrixTest();
		dmt.go();
	}

	public void go()
	{
		dmp = new DotMatrixPanel();

		JFrame frame = new JFrame("dot-matrix on Java");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// frame.setContentPane(new DotMatrixPanel());

		dmp.addMouseListener(new PanelMouseListener());

		frame.getContentPane().add(BorderLayout.CENTER, dmp);

		textArea = new JTextArea(8, 20);
		textArea.setLineWrap(true);
		Font font = new Font(Font.MONOSPACED, Font.PLAIN, 12);

		textArea.setFont(font);

		JScrollPane textAreaPane = new JScrollPane(textArea);
		textAreaPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		textAreaPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		frame.getContentPane().add(BorderLayout.SOUTH, textAreaPane);

		// frame.setBounds(100, 100, 1000, 400);
		frame.setLocation(100, 100);
		// frame.setLocationRelativeTo(null);
		frame.setSize(dmp.getWidth(), dmp.getHeight() + 120);
		frame.setResizable(false);

		frame.setVisible(true);
	}

	public class PanelMouseListener implements MouseListener
	{

		@Override
		public void mouseClicked(MouseEvent e)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void mousePressed(MouseEvent e)
		{
			byte[] data = dmp.getCache();
			String s = new String();
			for (int i = 0; i < data.length; i++)
			{
				if (i % 8 == 0 && i > 0)
					s = s.concat("\n");
				s = s.concat(String.format("0x%02x, ", data[i]));
			}

			textArea.setText(s);

		}

		@Override
		public void mouseReleased(MouseEvent e)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseEntered(MouseEvent e)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e)
		{
			// TODO Auto-generated method stub

		}

	}
}
