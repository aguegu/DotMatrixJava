package aguegu.dotmatrix;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

public class DotMatrixRecordPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	private DotMatrixPanel panelDm;
	private JTextArea textArea;
	private JPanel panelController;	
	private DotMatrix dm;
	
	private DotMatrixPanel.Mode mode = DotMatrixPanel.Mode.XYZ;
	
	private static final String[] MOVEMENTS = new String[] { "on", "off", "X+",
		"X-", "Y+", "Y-", "Z+", "Z-" };
	
	public DotMatrixRecordPanel()
	{
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		dm = new DotMatrix();
		
		panelController = new JPanel();
		panelController.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 2));



		panelDm = new DotMatrixPanel(dm);
		panelDm.setMode(mode);
		
		panelDm.addMouseListener(new MouseListenerPanelDotMatrix());
		this.add(panelDm);
		
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
		this.add(panelController);
		
		panelDm.requestFocusInWindow();
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
			textArea.setText(dm.cacheString());
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
	
	public void refresh(boolean updateString)
	{
		panelDm.update();
		panelDm.repaint();
		if (updateString)
			textArea.setText(dm.cacheString());
	}
	
	public DotMatrix getDotMatrix()
	{
		return dm;
	}
	
	public JMenuItem getMenu()
	{
		JMenu mnEdit = new JMenu("Frame");
		mnEdit.setMnemonic(KeyEvent.VK_E);
		
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

		JCheckBoxMenuItem miLoop = new JCheckBoxMenuItem("loop", true);
		//miLoop.addActionListener(new ActionListenerInLoop());
		mnEdit.add(miLoop);

		for (String s : MOVEMENTS)
		{
			JMenuItem button = new JMenuItem(s);
			button.setActionCommand(s);
			//button.addActionListener(new ActionListenerButtonMove());
			mnEdit.add(button);
		}
		
		return mnEdit;

	}
}
