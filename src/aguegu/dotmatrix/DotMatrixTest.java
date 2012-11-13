package aguegu.dotmatrix;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
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
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.ButtonGroup;
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
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.Document;
import javax.swing.Timer;

//import aguegu.dotmatrix.DotMatrixPanel;

public class DotMatrixTest extends JFrame
{
	private static final long serialVersionUID = 5805285424717739698L;

	private DotMatrix dm;
	private DotMatrixRecord dmr;
	private DotMatrixPanel panelDm;
	private DotMatrixRecordList listFrame;

	private Timer timer;

	private JTextArea textArea;
	private JPanel panelController;

	private JPanel panelMain;
	private JLabel labelStatus;

	private JCheckBox checkboxInLoop;
	private JCheckBoxMenuItem miLoop;

	// Menu
	private static final String[] FILE_OPERATIONS = new String[] { "New",
			"Open", "Save", "Exit" };
	private static final String[] MOVEMENTS = new String[] { "on", "off", "X+",
			"X-", "Y+", "Y-", "Z+", "Z-" };
	private static final String[] RECORD_OPERACTIONS = new String[] { "Append",
			"Insert", "Update", "Delete" };

	private static final String PROGRAME_NAME = new String("dot-matrix (Java)");

	// Status
	private boolean inLoop = true;
	private DotMatrixPanel.Mode mode = DotMatrixPanel.Mode.XYZ;
	private boolean isCreated = false;

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
		this.setTitle(PROGRAME_NAME + " | aGuegu.net");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		dm = new DotMatrix();
		timer = new Timer(50, new TimerActionListener());

		panelDm = new DotMatrixPanel(dm);
		panelDm.setMode(mode);

		panelMain = new JPanel();
		panelMain.setLayout(new BoxLayout(panelMain, BoxLayout.Y_AXIS));

		panelController = new JPanel();
		panelController.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 2));

		panelDm.addMouseListener(new MouseListenerPanelDotMatrix());
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

		// frame.getContentPane().add(BorderLayout.SOUTH, panelController);
		panelMain.add(panelController);
		this.getContentPane().add(BorderLayout.CENTER, panelMain);

		this.getContentPane().add(BorderLayout.WEST, initPanelMove());

		labelStatus = new JLabel("http://aguegu.net");
		labelStatus.setBorder(BorderFactory
				.createBevelBorder(BevelBorder.LOWERED));
		this.add(BorderLayout.SOUTH, labelStatus);

		dmr = new DotMatrixRecord("record.dat");
		dmr.create();
		listFrame = new DotMatrixRecordList(dmr);

		listFrame
				.addListSelectionListener(new ListSelectionListenerListFrame());
		this.getContentPane().add(BorderLayout.EAST, listFrame);

		DotMatrixTestMenuBar bar = new DotMatrixTestMenuBar();
		this.setJMenuBar(bar);

		this.setLocation(100, 100);
		this.setSize(this.getPreferredSize());
		this.setResizable(false);

		this.setVisible(true);
		panelDm.requestFocusInWindow();
		timer.isRunning();
		// timer.start();

		refresh(true);

		// System.out.println(Arrays.asList(FILE_OPERATIONS).indexOf("Save"));
	}

	private JPanel initPanelMove()
	{
		JPanel panelMove = new JPanel();
		panelMove.setLayout(new BoxLayout(panelMove, BoxLayout.Y_AXIS));
		panelMove.setBorder(BorderFactory.createEmptyBorder(13, 5, 13, 5));

		for (String s : MOVEMENTS)
		{
			JButton button = new JButton(s);
			button.setActionCommand(s);
			button.addActionListener(new ActionListenerButtonMove());

			if (s.equals("on") || s.equals("off"))
			{
				button.setText(null);
				button.setIcon(new ImageIcon(getClass().getResource(
						s.concat(".png"))));
			}

			panelMove.add(button);
			panelMove.add(Box.createRigidArea(new Dimension(0, 5)));
		}

		checkboxInLoop = new JCheckBox("loop", inLoop);
		checkboxInLoop.addActionListener(new ActionListenerInLoop());
		panelMove.add(checkboxInLoop);

		return panelMove;
	}

	private class ActionListenerInLoop implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() instanceof JCheckBox)
			{
				inLoop = ((JCheckBox) e.getSource()).isSelected();
			}
			else if (e.getSource() instanceof JCheckBoxMenuItem)
			{
				inLoop = ((JCheckBoxMenuItem) e.getSource()).isSelected();
			}

			checkboxInLoop.setSelected(inLoop);
			miLoop.setSelected(inLoop);
		}
	}

	private class ActionListenerMode implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			switch (e.getActionCommand())
			{
			case "XYZ":
				mode = DotMatrixPanel.Mode.XYZ;
				break;
			case "YZX":
				mode = DotMatrixPanel.Mode.YZX;
				break;
			case "ZXY":
				mode = DotMatrixPanel.Mode.ZXY;
				break;
			}
			panelDm.setMode(mode);
			refresh(true);
		}
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

	private class ActionListenerFileOperation implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			JFileChooser fs = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter(
					"3D8 animation record (*.dat)", "dat");
			fs.setFileFilter(filter);

			switch (e.getActionCommand())
			{
			case "New":
				break;
			case "Open":

				fs.showOpenDialog(null);

				if (fs.getSelectedFile() != null)
				{
					dmr.setFileName(fs.getSelectedFile().getAbsolutePath());
					dmr.readRecord();
					listFrame.syncToReocrd();					
				}
				break;
			case "Save":
				if (isCreated)
				{
				}
				else
				{
					fs.showSaveDialog(null);

					if (fs.getSelectedFile() != null)
						dmr.setFileName(fs.getSelectedFile().getAbsolutePath());
				}
				dmr.save();
				isCreated = true;
				break;

			case "Exit":
				System.exit(0);
				break;
			}
		}
	}

	private class ActionListenerRocordOperation implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			int index = listFrame.getSelectedIndex();

			switch (e.getActionCommand())
			{
			case "Insert":
				dmr.insert(dm.getCache(), index);
				listFrame.syncToReocrd();
				listFrame.setSelectedIndex(index);
				break;
			case "Append":
				dmr.append(dm.getCache(), index);
				listFrame.syncToReocrd();
				listFrame.setSelectedIndex(index + 1);
				break;
			case "Update":
				if (index == -1)
					break;
				dmr.update(dm.getCache(), index);
				break;
			case "Delete":
				if (index == -1)
					break;
				dmr.remove(index);
				listFrame.syncToReocrd();
				break;
			}
			// setTitle("*");
		}
	}

	private class ActionListenerButtonMove implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			boolean recycle = inLoop;

			switch (e.getActionCommand())
			{
			case "on":
				dm.clear(true);
				break;
			case "off":
				dm.clear(false);
				break;
			case "X+":
				dm.move(DotMatrix.Direction.X_POSI, recycle);
				break;
			case "X-":
				dm.move(DotMatrix.Direction.X_NEGA, recycle);
				break;
			case "Y+":
				dm.move(DotMatrix.Direction.Y_POSI, recycle);
				break;
			case "Y-":
				dm.move(DotMatrix.Direction.Y_NEGA, recycle);
				break;
			case "Z+":
				dm.move(DotMatrix.Direction.Z_POSI, recycle);
				break;
			case "Z-":
				dm.move(DotMatrix.Direction.Z_NEGA, recycle);
				break;
			}
			refresh(true);
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
				refresh(false);
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

			if (index == -1)
				return;

			DotMatrixRecordFrame dmrf = dmr.getFrame(index);

			byte[] cache = new byte[64];
			System.arraycopy(dmrf.getData(), 8, cache, 0, 64);

			dm.setCache(cache);
			refresh(true);
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

	private class TimerActionListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			dm.move(DotMatrix.Direction.Z_NEGA, true);
			refresh(true);
		}
	}

	private void refresh(boolean updateString)
	{
		panelDm.update();
		panelDm.repaint();
		if (updateString)
			textArea.setText(cacheString());
	}

	private class DotMatrixTestMenuBar extends JMenuBar implements
			ActionListener
	{
		private static final long serialVersionUID = -6873734389340066641L;

		public DotMatrixTestMenuBar()
		{
			JMenu mnFile = new JMenu("File");
			JMenu mnEdit = new JMenu("Edit");
			JMenu mnRecord = new JMenu("Record");
			JMenu mnHelp = new JMenu("Help");

			mnFile.setMnemonic(KeyEvent.VK_F);
			mnEdit.setMnemonic(KeyEvent.VK_E);
			mnRecord.setMnemonic(KeyEvent.VK_R);
			mnHelp.setMnemonic(KeyEvent.VK_H);

			for (String s : FILE_OPERATIONS)
			{
				JMenuItem button = new JMenuItem(s);
				button.setActionCommand(s);
				button.addActionListener(new ActionListenerFileOperation());
				mnFile.add(button);
			}

			String[] Modes = new String[] { "XYZ", "YZX", "ZXY" };
			ButtonGroup bgMode = new ButtonGroup();

			for (String s : Modes)
			{
				JRadioButtonMenuItem button = new JRadioButtonMenuItem(s);
				button.setActionCommand(s);
				button.addActionListener(new ActionListenerMode());
				bgMode.add(button);

				if (s.equals("XYZ"))
					button.setSelected(true);

				mnEdit.add(button);
			}

			mnEdit.addSeparator();

			miLoop = new JCheckBoxMenuItem("loop", inLoop);
			miLoop.addActionListener(new ActionListenerInLoop());
			mnEdit.add(miLoop);

			for (String s : MOVEMENTS)
			{
				JMenuItem button = new JMenuItem(s);
				button.setActionCommand(s);
				button.addActionListener(new ActionListenerButtonMove());
				mnEdit.add(button);
			}

			for (String s : RECORD_OPERACTIONS)
			{
				JMenuItem button = new JMenuItem(s);
				button.setActionCommand(s);
				button.addActionListener(new ActionListenerRocordOperation());
				mnRecord.add(button);
			}

			JMenuItem miAbout = new JMenuItem("About");
			miAbout.setActionCommand("About");
			miAbout.addActionListener(this);
			mnHelp.add(miAbout);

			this.add(mnFile);
			this.add(mnEdit);
			this.add(mnRecord);
			this.add(mnHelp);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			switch (e.getActionCommand())
			{
			case "About":
				JOptionPane
						.showMessageDialog(this,
								"For more info, check\nhttp://aguegu.net",
								"About", JOptionPane.OK_OPTION
										| JOptionPane.INFORMATION_MESSAGE);
				break;
			}
		}
	}

}
