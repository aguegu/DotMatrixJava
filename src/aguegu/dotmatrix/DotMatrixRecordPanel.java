package aguegu.dotmatrix;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
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
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

public class DotMatrixRecordPanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	private DotMatrixRecordFrame dmrf;

	private DotMatrixPanel panelDm;

	private JPanel panelController;
	private JTextArea textAreaCache;

	private JCheckBox checkboxInLoop;
	private JCheckBoxMenuItem miInLoop;

	private JSlider sliderBrightness;
	private JSlider sliderSmallSpan;
	private JSlider sliderBigSpan;

	private JCheckBox checkboxUpperLed;
	private JCheckBox checkboxBottomLed;

	private JRadioButton[] radiobuttonModes;

	private static final String[] MOVEMENTS = new String[] { "on", "off", "X+",
			"X-", "Y+", "Y-", "Z+", "Z-" };

	private boolean inLoop = true;
	private static Font monoFont;
	private CL cl;

	public DotMatrixRecordPanel()
	{
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		panelController = new JPanel();
		panelController.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 2));

		panelDm = new DotMatrixPanel();
		panelDm.addMouseListener(new MouseListenerPanelDotMatrix());
		this.add(panelDm);

		monoFont = new Font("monospaced", Font.PLAIN, 12);

		textAreaCache = new JTextArea(9, 52);
		textAreaCache.setLineWrap(true);
		textAreaCache.setFont(monoFont);

		Document doc = textAreaCache.getDocument();
		doc.addDocumentListener(new DocumentListeneDotMatrixTextArea());

		JScrollPane textAreaPane = new JScrollPane(textAreaCache,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		panelController.add(textAreaPane);

		cl = new CL();

		sliderBrightness = new JSlider(0, 255, 255);
		sliderBrightness.setMinorTickSpacing(0x20);
		sliderBrightness.setPaintTicks(true);
		sliderBrightness.setSnapToTicks(true);
		sliderBrightness.addChangeListener(cl);

		sliderSmallSpan = new JSlider(0, 65535, 65535);
		sliderSmallSpan.setMinorTickSpacing(0x1000);
		sliderSmallSpan.setSnapToTicks(true);
		sliderSmallSpan.setPaintTicks(true);
		sliderSmallSpan.addChangeListener(cl);

		sliderBigSpan = new JSlider(0, 65535, 0);
		sliderBigSpan.setMinorTickSpacing(0x1000);
		sliderBigSpan.setSnapToTicks(true);
		sliderBigSpan.setPaintTicks(true);
		sliderBigSpan.addChangeListener(cl);

		panelController.add(sliderBrightness);
		panelController.add(sliderSmallSpan);
		panelController.add(sliderBigSpan);

		checkboxUpperLed = new JCheckBox("U");
		checkboxUpperLed.addChangeListener(cl);
		panelController.add(checkboxUpperLed);

		checkboxBottomLed = new JCheckBox("B");
		checkboxBottomLed.addChangeListener(cl);
		panelController.add(checkboxBottomLed);

		radiobuttonModes = new JRadioButton[3];
		ButtonGroup bgMode = new ButtonGroup();
		int i = 0;
		for (DMMode mode : DMMode.values())
		{
			radiobuttonModes[i] = new JRadioButton(mode.toString());
			radiobuttonModes[i].setActionCommand(mode.toString());
			radiobuttonModes[i].addActionListener(new ActionListenerMode());
			bgMode.add(radiobuttonModes[i]);
			panelController.add(radiobuttonModes[i]);
			i++;
		}

		this.add(panelController);

		panelDm.requestFocusInWindow();
	}

	public void setFrame(DotMatrixRecordFrame dmrf)
	{
		this.dmrf = new DotMatrixRecordFrame(dmrf.getIndex());
		this.dmrf.setData(dmrf.getData());
	}

	public byte[] getData()
	{
		return this.dmrf.getData();
	}

	private class CL implements ChangeListener
	{
		@Override
		public void stateChanged(ChangeEvent e)
		{
			if (e.getSource() instanceof JSlider)
			{
				if (e.getSource().equals(sliderBrightness))
				{
					dmrf.setBrightness((Integer) sliderBrightness.getValue());
				}
				else if (e.getSource().equals(sliderSmallSpan))
				{
					dmrf.setSmallSpan((Integer) sliderSmallSpan.getValue());
				}
				else if (e.getSource().equals(sliderBigSpan))
				{
					dmrf.setBigSpan((Integer) sliderBigSpan.getValue());
				}
				refresh(true);
			}
			else if (e.getSource() instanceof JCheckBox)
			{
				if (checkboxUpperLed.isSelected()
						&& checkboxBottomLed.isSelected())
					dmrf.setAttachment(DMAttachment.BOTH);
				else if (checkboxUpperLed.isSelected())
					dmrf.setAttachment(DMAttachment.UPPER_LED);
				else if (checkboxBottomLed.isSelected())
					dmrf.setAttachment(DMAttachment.BOTTOM_LED);
				else
					dmrf.setAttachment(DMAttachment.NONE);

				refresh(true);
			}
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
			if (!textAreaCache.isFocusOwner())
				return;

			String s = textAreaCache.getText();

			if (s.matches("(0[x|X][a-f0-9A-Z]{2},[\\s]+){72}"))
			{
				Pattern pattern = Pattern.compile("0[x|X][a-f0-9A-Z]{2}");
				Matcher matcher = pattern.matcher(s);

				byte[] data = new byte[72];

				for (int i = 0; i < data.length && matcher.find(); i++)
				{
					String match = matcher.group();
					int t = Integer.decode(match);
					data[i] = (byte) t;
				}

				dmrf.setData(data);
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
			dmrf.setMode(DMMode.getMode(e.getActionCommand()));
			refresh(true);
		}
	}

	private class ActionListenerAttachment implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			dmrf.setAttachment(DMAttachment.getDMAttachment(e
					.getActionCommand()));
			refresh(true);
		}
	}

	public void refresh(boolean updateString)
	{
		panelDm.setMode(dmrf.getMode());
		panelDm.setDotMatrix(dmrf.getDotMatrix());
		panelDm.update();
		panelDm.repaint();

		sliderBrightness.removeChangeListener(cl);
		sliderBrightness.setValue(dmrf.getBrightness());
		sliderBrightness.addChangeListener(cl);

		sliderSmallSpan.removeChangeListener(cl);
		sliderSmallSpan.setValue(dmrf.getSmallSpan());
		sliderSmallSpan.addChangeListener(cl);

		sliderBigSpan.removeChangeListener(cl);
		sliderBigSpan.setValue(dmrf.getBigSpan());
		sliderBigSpan.addChangeListener(cl);

		checkboxUpperLed.removeChangeListener(cl);
		checkboxBottomLed.removeChangeListener(cl);

		switch (dmrf.getAttachment())
		{
		case BOTH:
			checkboxUpperLed.setSelected(true);
			checkboxBottomLed.setSelected(true);
			break;
		case BOTTOM_LED:
			checkboxUpperLed.setSelected(false);
			checkboxBottomLed.setSelected(true);
			break;
		case UPPER_LED:
			checkboxUpperLed.setSelected(true);
			checkboxBottomLed.setSelected(false);
			break;
		case NONE:
		default:
			break;
		}

		checkboxUpperLed.addChangeListener(cl);
		checkboxBottomLed.addChangeListener(cl);
		
		radiobuttonModes[dmrf.getMode().ordinal()].setSelected(true);

		if (updateString)
		{
			textAreaCache.setText(dmrf.getCacheString());
		}
	}

	public JMenuItem getMenu()
	{
		JMenu mnEdit = new JMenu("Frame");
		mnEdit.setMnemonic(KeyEvent.VK_E);

		ButtonGroup bgMode = new ButtonGroup();

		for (DMMode mode : DMMode.values())
		{
			JRadioButtonMenuItem button = new JRadioButtonMenuItem(
					mode.toString());
			button.setActionCommand(mode.toString());
			button.addActionListener(new ActionListenerMode());
			bgMode.add(button);

			if (mode.equals(dmrf.getMode()))
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
			button.addActionListener(new ActionListenerFrameOperation());
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
			button.setFont(monoFont);
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
