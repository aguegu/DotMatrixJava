package aguegu.dotmatrix;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

public class DMRecordPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private DMRecordFrame dmrf;

	private DMPanel panelDm;

	private JPanel panelController;
	private JTextArea textAreaCache;
	private DMRecordHeaderPanel panelHeader;

	private JCheckBox checkboxInLoop;
	private JCheckBoxMenuItem miInLoop;

	private JRadioButtonMenuItem[] radiobuttonMenuItemModes;

	private static final String[] FRAME_OPERATION_COMMANDS = new String[] {
			"on", "off", "x+", "x-", "y+", "y-", "z+", "z-", "3c", "3a", "2c",
			"2a", "1c", "1a", "0c", "0a", "r" };

	private boolean inLoop = true;
	private static Font monoFont;

	private JMenu menu;
	private JPanel panelFrameOperation;

	private ResourceBundle res;
	private DotMatrixTest parent;

	public DMRecordPanel(DotMatrixTest parent, DMRecordPanel prev, ResourceBundle res) {
		this.res = res;
		this.parent = parent;
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		panelController = new JPanel();
		panelController.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 2));

		panelDm = new DMPanel();
		panelDm.addMouseListener(new MouseListenerPanelDotMatrix());
		this.add(panelDm);

		monoFont = new Font("monospaced", Font.PLAIN, 12);

		textAreaCache = new JTextArea(9, 51);
		textAreaCache.setLineWrap(true);
		textAreaCache.setFont(monoFont);

		Document doc = textAreaCache.getDocument();
		doc.addDocumentListener(new DocumentListeneDotMatrixTextArea());

		JScrollPane textAreaPane = new JScrollPane(textAreaCache,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		panelController.add(textAreaPane);
		
		panelHeader = (prev != null) ? prev.getHeader() : new DMRecordHeaderPanel(this, res);
		panelHeader.updateParent(this);
		panelController.add(panelHeader);

		this.add(panelController);

		panelDm.requestFocusInWindow();

		this.dmrf = new DMRecordFrame(0);
		initMenu();
		initFrameOperationPanel();
	}
	
	public DMRecordHeaderPanel getHeader() {
		return panelHeader;
	}
	
	public void setFrame(int index) {
		parent.setActiveFrame(index);
	}
	
	public int getNumFrames() {
		return parent.getNumFrames();
	}

	public void setFrame(DMRecordFrame dmrf) {
		this.dmrf = new DMRecordFrame(dmrf.getIndex());
		this.dmrf.setData(dmrf.getData());
	}

	public byte[] getData() {
		return this.dmrf.getData();
	}

	public DMRecordFrame getRecordFrame() {
		return this.dmrf;
	}

	private class MouseListenerPanelDotMatrix implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
			textAreaCache.setText(dmrf.getCacheString());
			panelDm.requestFocusInWindow();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}
	}

	private class DocumentListeneDotMatrixTextArea implements DocumentListener {
		@Override
		public void insertUpdate(DocumentEvent e) {
			if (!textAreaCache.isFocusOwner())
				return;

			String s = textAreaCache.getText();

			if (s.matches("(0[x|X][\\p{XDigit}]{2},[\\s]+){72}")) {
				Pattern pattern = Pattern.compile("0[x|X][\\p{XDigit}]{2}");
				Matcher matcher = pattern.matcher(s);

				byte[] data = new byte[72];

				for (int i = 0; i < data.length && matcher.find(); i++) {
					String match = matcher.group();
					int t = Integer.decode(match);
					data[i] = (byte) t;
				}

				dmrf.setData(data);
				refresh(false);
			}
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
		}
	}

	private class ActionListenerMode implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			dmrf.setMode(DMMode.getMode(e.getActionCommand()));
			refresh(true);
		}
	}

	public void refresh(boolean updateString) {
		panelDm.setMode(dmrf.getMode());
		panelDm.setDotMatrix(dmrf.getDotMatrix());
		panelDm.update();
		panelDm.repaint();

		radiobuttonMenuItemModes[dmrf.getMode().ordinal()].setSelected(true);
		panelHeader.refresh();

		if (updateString) {
			textAreaCache.setText(dmrf.getCacheString());
		}
	}

	private void initMenu() {
		menu = new JMenu(res.getString("frame"));
		menu.setMnemonic(KeyEvent.VK_E);

		radiobuttonMenuItemModes = new JRadioButtonMenuItem[3];

		ButtonGroup bgMode = new ButtonGroup();
		int i = 0;

		for (DMMode mode : DMMode.values()) {
			radiobuttonMenuItemModes[i] = new JRadioButtonMenuItem(
					mode.toString());
			radiobuttonMenuItemModes[i].setActionCommand(mode.toString());
			radiobuttonMenuItemModes[i]
					.addActionListener(new ActionListenerMode());
			bgMode.add(radiobuttonMenuItemModes[i]);
			menu.add(radiobuttonMenuItemModes[i]);
			i++;
		}

		menu.addSeparator();

		miInLoop = new JCheckBoxMenuItem(res.getString("loop"), true);
		miInLoop.addActionListener(new ActionListenerInLoop());
		menu.add(miInLoop);

		for (String s : FRAME_OPERATION_COMMANDS) {
			JMenuItem button = new JMenuItem(res.getString(s));
			button.setActionCommand(s);
			button.addActionListener(new ActionListenerFrameOperation());
			menu.add(button);
		}
	}

	public JMenu getMenu() {
		return menu;
	}

	public void initFrameOperationPanel() {
		panelFrameOperation = new JPanel();
		panelFrameOperation.setPreferredSize(new Dimension(64, 0));
		panelFrameOperation.setLayout(new FlowLayout(FlowLayout.LEFT));
		// panelFrameOperation.setBorder(BorderFactory.createEmptyBorder(13, 5,
		// 13, 5));

		for (String s : FRAME_OPERATION_COMMANDS) {
			JButton button = new JButton();

			button.setActionCommand(s);
			button.addActionListener(new ActionListenerFrameOperation());

			button.setIcon(new ImageIcon(getClass().getResource(
					"/image/" + s + ".png")));
			button.setToolTipText(res.getString(s));

			button.setMargin(new Insets(1, 1, 1, 1));

			panelFrameOperation.add(button);
		}

		checkboxInLoop = new JCheckBox(res.getString("loop"), inLoop);
		checkboxInLoop.addActionListener(new ActionListenerInLoop());
		checkboxInLoop.setToolTipText(res.getString("loop"));
		panelFrameOperation.add(checkboxInLoop);

	}

	public JPanel getFrameOperationPanel() {
		return panelFrameOperation;
	}

	private class ActionListenerFrameOperation implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			boolean recycle = inLoop;

			DotMatrix dm = dmrf.getDotMatrix();
			switch (e.getActionCommand()) {
			case "on":
				dm.clear(true);
				break;
			case "off":
				dm.clear(false);
				break;
			case "x+":
				dm.move(DotMatrix.Direction.X_POSI, recycle);
				break;
			case "x-":
				dm.move(DotMatrix.Direction.X_NEGA, recycle);
				break;
			case "y+":
				dm.move(DotMatrix.Direction.Y_POSI, recycle);
				break;
			case "y-":
				dm.move(DotMatrix.Direction.Y_NEGA, recycle);
				break;
			case "z+":
				dm.move(DotMatrix.Direction.Z_POSI, recycle);
				break;
			case "z-":
				dm.move(DotMatrix.Direction.Z_NEGA, recycle);
				break;
			case "r":
				dm.reverse();
				break;
			case "3c":
				dm.rotate(3, true, recycle);
				break;
			case "2c":
				dm.rotate(2, true, recycle);
				break;
			case "1c":
				dm.rotate(1, true, recycle);
				break;
			case "0c":
				dm.rotate(0, true, recycle);
				break;
			case "3a":
				dm.rotate(3, false, recycle);
				break;
			case "2a":
				dm.rotate(2, false, recycle);
				break;
			case "1a":
				dm.rotate(1, false, recycle);
				break;
			case "0a":
				dm.rotate(0, false, recycle);
				break;
			}
			refresh(true);
		}
	}

	private class ActionListenerInLoop implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() instanceof JCheckBox) {
				inLoop = ((JCheckBox) e.getSource()).isSelected();
			} else if (e.getSource() instanceof JCheckBoxMenuItem) {
				inLoop = ((JCheckBoxMenuItem) e.getSource()).isSelected();
			}

			checkboxInLoop.setSelected(inLoop);
			miInLoop.setSelected(inLoop);
		}
	}
}
