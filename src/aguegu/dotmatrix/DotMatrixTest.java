package aguegu.dotmatrix;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
	public static DotMatrix dm;
	public static DotMatrixPanel panelDm;
	public static JTextArea textArea;
	public static JPanel panelController;

	public static void main(String[] args)
	{
		DotMatrixTest dmt = new DotMatrixTest();
		dmt.go();
	}

	public void go()
	{
		dm = new DotMatrix();
		panelDm = new DotMatrixPanel(dm);
		panelController = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
		// controllerPanel.setLayout(new BoxLayout(controllerPanel,
		// BoxLayout.X_AXIS ));

		JFrame frame = new JFrame("dot-matrix on Java");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// frame.setContentPane(new DotMatrixPanel());

		panelDm.addMouseListener(new MouseListenerPanelDotMatrix());

		frame.getContentPane().add(BorderLayout.CENTER, panelDm);

		textArea = new JTextArea(8, 50);
		textArea.setLineWrap(true);
		Font font = new Font(Font.MONOSPACED, Font.PLAIN, 12);

		textArea.setFont(font);

		JScrollPane textAreaPane = new JScrollPane(textArea);
		textAreaPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		textAreaPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		panelController.add(textAreaPane);
		JButton buttonSave = new JButton("save");
		buttonSave.addActionListener(new ListenerButtonSave());
		panelController.add(buttonSave);

		frame.getContentPane().add(BorderLayout.SOUTH, panelController);

		// frame.setBounds(100, 100, 1000, 400);
		frame.setLocation(100, 100);
		// frame.setLocationRelativeTo(null);
		frame.setSize(panelDm.getWidth(), panelDm.getHeight()
				+ (int) panelController.getPreferredSize().getHeight());
		frame.setResizable(false);

		frame.setVisible(true);
	}

	public class MouseListenerPanelDotMatrix implements MouseListener
	{
		@Override
		public void mouseClicked(MouseEvent e)
		{
		}

		@Override
		public void mousePressed(MouseEvent e)
		{
			byte[] data = dm.getCache();
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

	public class ListenerButtonSave implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			DotMatrixRecord dmr = new DotMatrixRecord("record.dat");
			dmr.save(dm.getCache());
		}
	}
}
