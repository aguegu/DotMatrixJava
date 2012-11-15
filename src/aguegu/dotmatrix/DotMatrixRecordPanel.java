package aguegu.dotmatrix;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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

	private DotMatrixRecordFrame dmrf;
	
	private DotMatrixPanel panelDm;
	private JTextArea textAreaCache;
	private JPanel panelController;
	

	private JCheckBox checkboxInLoop;
	private JCheckBoxMenuItem miInLoop;

	private DotMatrixPanel.Mode mode = DotMatrixPanel.Mode.XYZ;

	private static final String[] MOVEMENTS = new String[] { "on", "off", "X+",
			"X-", "Y+", "Y-", "Z+", "Z-" };

	private boolean inLoop = true;

	public DotMatrixRecordPanel()
	{
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		panelController = new JPanel();
		panelController.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 2));

		panelDm = new DotMatrixPanel();
		panelDm.setMode(mode);

		panelDm.addMouseListener(new MouseListenerPanelDotMatrix());
		this.add(panelDm);

		textAreaCache = new JTextArea(9, 48);
		textAreaCache.setLineWrap(true);
		textAreaCache.setFont(new Font("monospaced", Font.PLAIN, 12));

		Document doc = textAreaCache.getDocument();
		doc.addDocumentListener(new DocumentListeneDotMatrixTextArea());

		JScrollPane textAreaPane = new JScrollPane(textAreaCache);
		textAreaPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		textAreaPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		panelController.add(textAreaPane);
		this.add(panelController);

		panelDm.requestFocusInWindow();
	}

	public void setFrame(DotMatrixRecordFrame dmrf)
	{
		DotMatrix dm = panelDm.getDotMatrix();

		switch (dmrf.getMode())
		{
		case 0x01:
			panelDm.setMode(DotMatrixPanel.Mode.YZX);
			break;
		case 0x02:
			panelDm.setMode(DotMatrixPanel.Mode.ZXY);
			break;
		case 0x00:
		default:
			panelDm.setMode(DotMatrixPanel.Mode.XYZ);
			break;
		}
		
		this.dmrf = dmrf;
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
			textAreaCache.setText(dmrf.getCacheString());
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
			String s = textAreaCache.getText();

			if (textAreaCache.isFocusOwner()
					&& s.matches("(0[x|X][a-f0-9A-Z]{2},[\\s]+){72}"))
			{
				Pattern pattern = Pattern.compile("0[x|X][a-f0-9A-Z]{2}");
				Matcher matcher = pattern.matcher(s);

				byte[] cache = new byte[64];

				for (int i = 0; i < cache.length && matcher.find(); i++)
				{
					String match = matcher.group();
					cache[i] = Integer.decode(match).byteValue();
				}

				// dm.setCache(cache);
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
			textAreaCache.setText(dmrf.getCacheString());
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

		miInLoop = new JCheckBoxMenuItem("loop", true);
		miInLoop.addActionListener(new ActionListenerInLoop());
		mnEdit.add(miInLoop);

		for (String s : MOVEMENTS)
		{
			JMenuItem button = new JMenuItem(s);
			button.setActionCommand(s);
			// button.addActionListener(new ActionListenerButtonMove());
			mnEdit.add(button);
		}

		return mnEdit;

	}

	public JPanel getPanelFrameOperation()
	{
		JPanel panelMove = new JPanel();
		panelMove.setLayout(new BoxLayout(panelMove, BoxLayout.Y_AXIS));
		panelMove.setBorder(BorderFactory.createEmptyBorder(13, 5, 13, 5));

		for (String s : MOVEMENTS)
		{
			JButton button = new JButton(s);
			button.setActionCommand(s);
			button.addActionListener(new ActionListenerFrameOperation());

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

	private class ActionListenerFrameOperation implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			boolean recycle = inLoop;

			DotMatrix dm = dmrf.getDotMatrix();
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
			miInLoop.setSelected(inLoop);

		}
	}
}
