package aguegu.dotmatrix;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.Document;
import javax.swing.Timer;

import aguegu.dotmatrix.DotMatrixPanel;

public class DotMatrixTest implements ActionListener
{
	private DotMatrix dm;
	private DotMatrixPanel panelDm;
	private JTextArea textArea;
	private JPanel panelController;

	private JPanel panelMain;
	private JLabel labelStatus;

	private JButton buttonSave;
	private JButton buttonAdd;

	private JButton buttonAllOn;
	private JButton buttonAllOff;
	private JButton buttonMoveXPosi;
	private JButton buttonMoveXNega;
	private JButton buttonMoveYPosi;
	private JButton buttonMoveYNega;
	private JButton buttonMoveZPosi;
	private JButton buttonMoveZNega;
	private JCheckBox checkboxRecycle;
	private DotMatrixRecord dmr;

	private JList<String> listFrame;

	private Timer timer;

	// private boolean mouse;

	public static void main(String[] args)
	{
		setUIFont(new javax.swing.plaf.FontUIResource(Font.MONOSPACED,
				Font.PLAIN, 12));
		DotMatrixTest dmt = new DotMatrixTest();
		dmt.init();
	}

	public static void setUIFont(javax.swing.plaf.FontUIResource f)
	{
		Enumeration<Object> keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements())
		{
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value instanceof javax.swing.plaf.FontUIResource)
				UIManager.put(key, f);
		}
	}

	public void init()
	{
		dm = new DotMatrix();
		timer = new Timer(50, this);
		dmr = new DotMatrixRecord("record.dat");

		panelDm = new DotMatrixPanel(dm);
		panelMain = new JPanel();
		panelMain.setLayout(new BoxLayout(panelMain, BoxLayout.Y_AXIS));

		panelController = new JPanel();
		panelController.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 2));
		// panelController.setLayout(new BoxLayout(panelController,
		// BoxLayout.X_AXIS));

		JFrame frame = new JFrame("dot-matrix (Java) | aGuegu.net");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// frame.setContentPane(new DotMatrixPanel());

		panelDm.addMouseListener(new MouseListenerPanelDotMatrix());
		// frame.getContentPane().add(BorderLayout.CENTER, panelDm);
		panelMain.add(panelDm);

		textArea = new JTextArea(8, 50);
		textArea.setLineWrap(true);

		Document doc = textArea.getDocument();
		doc.addDocumentListener(new DocumentListeneDotMatrixTextArea());

		JScrollPane textAreaPane = new JScrollPane(textArea);
		textAreaPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		textAreaPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		panelController.add(textAreaPane);
		buttonSave = new JButton("Save");
		buttonSave.addActionListener(new ActionListenerButtonSave());
		panelController.add(buttonSave);

		buttonAdd = new JButton("Add");
		buttonAdd.addActionListener(new ActionListenerButtonAdd());
		panelController.add(buttonAdd);

		// frame.getContentPane().add(BorderLayout.SOUTH, panelController);
		panelMain.add(panelController);
		frame.getContentPane().add(BorderLayout.CENTER, panelMain);

		frame.getContentPane().add(BorderLayout.WEST, initPanelMove());

		labelStatus = new JLabel("http://aguegu.net");
		labelStatus.setBorder(BorderFactory
				.createBevelBorder(BevelBorder.LOWERED));
		frame.add(BorderLayout.SOUTH, labelStatus);

		dmr.readRecord();
		listFrame = new JList<String>(dmr.getList().toArray(new String[0]));
		listFrame.setPreferredSize(new Dimension(48, 0));
		listFrame
				.addListSelectionListener(new ListSelectionListenerListFrame());
		frame.getContentPane().add(BorderLayout.EAST, listFrame);

		frame.setLocation(100, 100);
		frame.setSize(frame.getPreferredSize());
		frame.setResizable(false);

		frame.setVisible(true);
		panelDm.requestFocusInWindow();
		timer.isRunning();
	}

	private JPanel initPanelMove()
	{
		JPanel panelMove = new JPanel();
		panelMove.setLayout(new BoxLayout(panelMove, BoxLayout.Y_AXIS));
		panelMove.setBorder(BorderFactory.createEmptyBorder(13, 5, 13, 5));

		buttonAllOn = new JButton(new ImageIcon(getClass().getResource(
				"AllOn.png")));
		buttonAllOn.addActionListener(new ActionListenerSwitchAll());

		buttonAllOff = new JButton(new ImageIcon(getClass().getResource(
				"AllOff.png")));
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

		checkboxRecycle = new JCheckBox("loop", true);

		panelMove.add(buttonAllOn);

		panelMove.add(Box.createRigidArea(new Dimension(0, 5)));
		// System.out.println(buttonAllOff.getMargin());

		panelMove.add(buttonAllOff);
		panelMove.add(Box.createRigidArea(new Dimension(0, 5)));
		panelMove.add(buttonMoveXNega);
		panelMove.add(Box.createRigidArea(new Dimension(0, 5)));
		panelMove.add(buttonMoveXPosi);
		panelMove.add(Box.createRigidArea(new Dimension(0, 5)));
		panelMove.add(buttonMoveYNega);
		panelMove.add(Box.createRigidArea(new Dimension(0, 5)));
		panelMove.add(buttonMoveYPosi);
		panelMove.add(Box.createRigidArea(new Dimension(0, 5)));
		panelMove.add(buttonMoveZNega);
		panelMove.add(Box.createRigidArea(new Dimension(0, 5)));
		panelMove.add(buttonMoveZPosi);
		panelMove.add(Box.createRigidArea(new Dimension(0, 5)));
		panelMove.add(checkboxRecycle);

		return panelMove;
	}

	private class MouseListenerPanelDotMatrix implements MouseListener
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

	private class ActionListenerButtonSave implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			// dmr.save(dm.getCache());
		}
	}

	private class ActionListenerButtonAdd implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			dmr.add(dm.getCache());
		}
	}

	private class ActionListenerSwitchAll implements ActionListener
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

	private class ActionListenerButtonMove implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			boolean recycle = checkboxRecycle.isSelected();

			if (e.getSource() == buttonMoveXPosi)
			{
				dm.move(DotMatrix.Direction.X_POSI, recycle);
			}
			else if (e.getSource() == buttonMoveXNega)
			{
				dm.move(DotMatrix.Direction.X_NEGA, recycle);
			}
			else if (e.getSource() == buttonMoveYPosi)
			{
				dm.move(DotMatrix.Direction.Y_POSI, recycle);
			}
			else if (e.getSource() == buttonMoveYNega)
			{
				dm.move(DotMatrix.Direction.Y_NEGA, recycle);
			}
			else if (e.getSource() == buttonMoveZPosi)
			{
				dm.move(DotMatrix.Direction.Z_POSI, recycle);
			}
			else if (e.getSource() == buttonMoveZNega)
			{
				dm.move(DotMatrix.Direction.Z_NEGA, recycle);
			}

			panelDm.update();
			panelDm.repaint();
			textArea.setText(cacheString());
		}
	}

	private class DocumentListeneDotMatrixTextArea implements DocumentListener
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

	private class ListSelectionListenerListFrame implements
			ListSelectionListener
	{
		@Override
		public void valueChanged(ListSelectionEvent e)
		{
			if (e.getValueIsAdjusting())
				return;

			int index = listFrame.getSelectedIndex();
			System.out.println(index);
			DotMatrixRecordFrame dmrf = dmr.getFrame(index);

			byte[] cache = new byte[64];
			System.arraycopy(dmrf.getData(), 8, cache, 0, 64);

			dm.setCache(cache);
			refresh();
		}
	}

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

	@Override
	public void actionPerformed(ActionEvent e)
	{
		dm.move(DotMatrix.Direction.Z_NEGA, true);
		refresh();
	}

	private void refresh()
	{
		panelDm.update();
		panelDm.repaint();
		textArea.setText(cacheString());
	}
}
