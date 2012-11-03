package aguegu.dotmatrix;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import aguegu.dotmatrix.DotMatrixPanel;

public class DotMatrixTest
{
	public static DotMatrixPanel dotmatrixPanel;
	public static JTextArea textArea;
	public static JPanel controllerPanel;

	public static void main(String[] args)
	{
		DotMatrixTest dmt = new DotMatrixTest();
		dmt.go();
	}

	public void go()
	{
		dotmatrixPanel= new DotMatrixPanel();
		controllerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
		// controllerPanel.setLayout(new BoxLayout(controllerPanel,
		// BoxLayout.X_AXIS ));

		JFrame frame = new JFrame("dot-matrix on Java");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// frame.setContentPane(new DotMatrixPanel());

		dotmatrixPanel.addMouseListener(new PanelMouseListener());

		frame.getContentPane().add(BorderLayout.CENTER, dotmatrixPanel);

		textArea = new JTextArea(8, 50);
		textArea.setLineWrap(true);
		Font font = new Font(Font.MONOSPACED, Font.PLAIN, 12);

		textArea.setFont(font);

		JScrollPane textAreaPane = new JScrollPane(textArea);
		textAreaPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		textAreaPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		controllerPanel.add(textAreaPane);
		JButton button = new JButton("connect");
		controllerPanel.add(button);

		frame.getContentPane().add(BorderLayout.SOUTH, controllerPanel);

		// frame.setBounds(100, 100, 1000, 400);
		frame.setLocation(100, 100);
		// frame.setLocationRelativeTo(null);
		frame.setSize(dotmatrixPanel.getWidth(), dotmatrixPanel.getHeight() + (int)controllerPanel.getPreferredSize().getHeight());
		frame.setResizable(false);

		frame.setVisible(true);
	}

	public class PanelMouseListener implements MouseListener
	{
		@Override
		public void mouseClicked(MouseEvent e)
		{
		}

		@Override
		public void mousePressed(MouseEvent e)
		{
			byte[] data = dotmatrixPanel.getCache();
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
		}

		@Override
		public void mouseEntered(MouseEvent e)
		{
		}

		@Override
		public void mouseExited(MouseEvent e)
		{
		}

	}
}
