package aguegu.dotmatrix;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import aguegu.dotmatrix.DotMatrixPanel;

public class DotMatrixTest
{
	private static DotMatrix dm;
	private static DotMatrixPanel panelDm;
	private static JTextArea textArea;
	private static JPanel panelController;
	private JButton buttonSave;
	private JButton buttonAllOn;
	private JButton buttonAllOff;
	private JButton buttonMoveXPosi;
	private JButton buttonMoveXNega;
	private JButton buttonMoveYPosi;
	private JButton buttonMoveYNega;
	private JButton buttonMoveZPosi;
	private JButton buttonMoveZNega;

	// private boolean mouse;

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

		// textArea.addKeyListener()

		Document doc = textArea.getDocument();
		doc.addDocumentListener(new DocumentListeneDotMatrixTextArea());

		JScrollPane textAreaPane = new JScrollPane(textArea);
		textAreaPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		textAreaPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		panelController.add(textAreaPane);
		buttonSave = new JButton("s");
		buttonSave.addActionListener(new ActionListenerButtonSave());
		buttonAllOn = new JButton("On");
		buttonAllOn.addActionListener(new ActionListenerSwitchAll());
		buttonAllOff = new JButton("Off");
		buttonAllOff.addActionListener(new ActionListenerSwitchAll());

		buttonMoveXPosi = new JButton("X+");
		buttonMoveXPosi.addActionListener(new ActionListenerButtonMove());
		buttonMoveXNega = new JButton("X-");
		buttonMoveXNega.addActionListener(new ActionListenerButtonMove());

		buttonMoveYPosi = new JButton("Y+");
		buttonMoveYPosi.addActionListener(new ActionListenerButtonMove());
		buttonMoveYNega = new JButton("Y-");
		buttonMoveYNega.addActionListener(new ActionListenerButtonMove());

		buttonMoveZPosi = new JButton("Z+");
		buttonMoveZPosi.addActionListener(new ActionListenerButtonMove());
		buttonMoveZNega = new JButton("Z-");
		buttonMoveZNega.addActionListener(new ActionListenerButtonMove());

		panelController.add(buttonSave);
		panelController.add(buttonAllOn);
		panelController.add(buttonAllOff);

		panelController.add(buttonMoveXPosi);
		panelController.add(buttonMoveXNega);
		panelController.add(buttonMoveYPosi);
		panelController.add(buttonMoveYNega);
		panelController.add(buttonMoveZPosi);
		panelController.add(buttonMoveZNega);

		frame.getContentPane().add(BorderLayout.SOUTH, panelController);

		// frame.setBounds(100, 100, 1000, 400);
		frame.setLocation(100, 100);
		// frame.setLocationRelativeTo(null);
		frame.setSize(panelDm.getWidth(), panelDm.getHeight()
				+ (int) panelController.getPreferredSize().getHeight());
		frame.setResizable(false);

		frame.setVisible(true);
		panelDm.requestFocusInWindow();
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
			textArea.setText(cacheString());
			panelDm.requestFocusInWindow();
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

	public class ActionListenerButtonSave implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			DotMatrixRecord dmr = new DotMatrixRecord("record.dat");
			dmr.save(dm.getCache());
		}
	}

	public class ActionListenerSwitchAll implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() == buttonAllOn)
				dm.clear(true);
			else if (e.getSource() == buttonAllOff)
				dm.clear(false);

			panelDm.update();
			panelDm.repaint();
			textArea.setText(cacheString());
		}
	}

	public class ActionListenerButtonMove implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() == buttonMoveXPosi)
			{
				dm.move(DotMatrix.Direction.X_POSI, true);
			}
			else if (e.getSource() == buttonMoveXNega)
			{
				dm.move(DotMatrix.Direction.X_NEGA, true);
			}
			else if (e.getSource() == buttonMoveYPosi)
			{
				dm.move(DotMatrix.Direction.Y_POSI, true);
			}
			else if (e.getSource() == buttonMoveYNega)
			{
				dm.move(DotMatrix.Direction.Y_NEGA, true);
			}
			else if (e.getSource() == buttonMoveZPosi)
			{
				dm.move(DotMatrix.Direction.Z_POSI, true);
			}
			else if (e.getSource() == buttonMoveZNega)
			{
				dm.move(DotMatrix.Direction.Z_NEGA, true);
			}

			panelDm.update();
			panelDm.repaint();
			textArea.setText(cacheString());
		}
	}

	public class DocumentListeneDotMatrixTextArea implements DocumentListener
	{
		@Override
		public void insertUpdate(DocumentEvent e)
		{
			String s = textArea.getText();

			if (textArea.isFocusOwner()
					&& s.matches("(0[x|X][a-f0-9A-Z]{2},[\\s]+){64}"))
			{
				Pattern pattern = Pattern.compile("0[x|X][a-f0-9A-Z]{2}");
				Matcher matcher = pattern.matcher(s);

				byte[] cache = new byte[64];

				for (int i = 0; i < cache.length && matcher.find(); i++)
				{
					String match = matcher.group();
					cache[i] = Integer.decode(match).byteValue();
				}

				dm.setCache(cache);
				panelDm.update();
				panelDm.repaint();
			}
		}

		@Override
		public void removeUpdate(DocumentEvent e)
		{
		}

		@Override
		public void changedUpdate(DocumentEvent e)
		{
		}
	}

	// public class

	private String cacheString()
	{
		byte[] data = dm.getCache();
		String s = new String();
		for (int i = 0; i < data.length; i++)
		{
			if (i % 8 == 0 && i > 0)
				s = s.concat("\n");
			s = s.concat(String.format("0x%02x, ", data[i]));
		}
		return s;
	}
}
