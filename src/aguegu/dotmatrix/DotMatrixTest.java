package aguegu.dotmatrix;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

public class DotMatrixTest extends JFrame {
	private static final long serialVersionUID = 5805285424717739698L;

	private DMRecord dmr;
	private DMRecordList listFrame;
	private DMRecordFrame dmrf;

	private DMRecordPanel panelRecord;
	private DotMatrixTestMenuBar bar;

	private JPanel panelToolbar;
	private JLabel labelStatus;

	private static final String[] FILE_OPERATION_COMMANDS = { "new", "open",
			"save", "save_as", "exit" };
	private static final String[] RECORD_OPERATION_COMMANDS = new String[] {
			"append", "insert", "update", "delete", "-", "mode", "brightness",
			"span" };

	private static final String PROGRAME_NAME = new String(
			"3D8 TF Animation Editor");

	private Locale locale = Locale.ENGLISH;
	private ResourceBundle res;

	private File fileRecord = null;
	private String message;
	private boolean isSaved = true;
	private boolean firstInit = true;
	private JFileChooser fs = new JFileChooser();

	public static void main(String[] args) {
		DotMatrixTest dmt = new DotMatrixTest();
		dmt.init();
	}

	public void init() {
		res = ResourceBundle
				.getBundle("aguegu.dotmatrix.DotMatrixTest", locale);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.getContentPane().removeAll();

		dmrf = new DMRecordFrame(0);

		panelRecord = new DMRecordPanel(this, panelRecord, res);
		panelRecord.setFrame(dmrf);

		panelToolbar = panelToolBar();
		this.getContentPane().add(BorderLayout.NORTH, panelToolbar);
		this.getContentPane().add(BorderLayout.CENTER, panelRecord);
		this.getContentPane().add(BorderLayout.WEST,
				panelRecord.getFrameOperationPanel());

		labelStatus = new JLabel();
		labelStatus.setBorder(BorderFactory
				.createBevelBorder(BevelBorder.LOWERED));
		this.getContentPane().add(BorderLayout.SOUTH, labelStatus);

		dmr = new DMRecord();

		listFrame = new DMRecordList(dmr);
		listFrame.addListSelectionListener(new ListSelectionListenerListFrame());

		JScrollPane listFramePane = new JScrollPane(listFrame,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		listFramePane.setPreferredSize(new Dimension(48, 0));
		this.getContentPane().add(BorderLayout.EAST, listFramePane);

		bar = new DotMatrixTestMenuBar();
		this.setJMenuBar(bar);

		message = res.getString("message");

		begin();
		refreshFrame();

		if (firstInit) {
			firstInit = false;
			this.setLocation(100, 100);
		}
		this.pack();
		this.setResizable(false);

		this.setVisible(true);
	}

	private class ActionListenerFileOperation implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			FileNameExtensionFilter filter = new FileNameExtensionFilter(
					"3D8 animation record (*.dat)", "dat");
			fs.setFileFilter(filter);
			File file = null;
			int result;

			switch (e.getActionCommand()) {
			case "new":
				if (fileRecord != null && !isSaved) {
					result = JOptionPane.showConfirmDialog(null,
							"Save changes to " + fileRecord.getName() + "?",
							"warning", JOptionPane.YES_NO_CANCEL_OPTION,
							JOptionPane.QUESTION_MESSAGE);
					if (result == JOptionPane.YES_OPTION)
						save();
				}
				init(); //begin
				break;
			case "open":
				result = fs.showOpenDialog(null);
				file = fs.getSelectedFile();

				if (file == null || result != JFileChooser.APPROVE_OPTION)
					break;
				
				fs.setCurrentDirectory(file); // save old directory

				fileRecord = file;
				dmr.readRecord(fileRecord);
				listFrame.syncToReocrd();

				if (dmr.getLength() > 0)
					listFrame.setSelectedIndex(0);

				message = fileRecord.getName() + " loaded, "
						+ fileRecord.length() + " bytes.";
				break;
			case "save":
				save();
				break;
			case "save_as":
				fs.setSelectedFile(new File("record.dat"));
				result = fs.showSaveDialog(null);
				file = fs.getSelectedFile();
				if (file == null || result != JFileChooser.APPROVE_OPTION)
					break;
				fileRecord = file;
				save();
				break;
			case "exit":
				if (fileRecord != null && !isSaved) {
					result = JOptionPane.showConfirmDialog(null,
							"Save changes to " + fileRecord.getName() + "?",
							"warning", JOptionPane.YES_NO_CANCEL_OPTION,
							JOptionPane.QUESTION_MESSAGE);

					if (result == JOptionPane.CANCEL_OPTION)
						break;

					if (result == JOptionPane.YES_OPTION)
						save();
				}
				System.exit(0);
				break;
			}

			refreshFrame();
		}
	}

    private class ActionListenerRocordOperation implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			int index = listFrame.getSelectedIndex();

			switch (e.getActionCommand()) {
			case "insert":
				dmr.insert(panelRecord.getData(), index);
				listFrame.syncToReocrd();
				listFrame.setSelectedIndex(index);
				break;
			case "append":
				dmr.append(panelRecord.getData(), index);
				listFrame.syncToReocrd();
				listFrame.setSelectedIndex(index + 1);
				break;
			case "update":
				if (index == -1)
					break;
				dmr.update(panelRecord.getData(), index);
				break;
			case "delete":
				if (index == -1)
					break;
				dmr.remove(index);
				listFrame.syncToReocrd();
				break;
			case "mode":
				String sMode = JOptionPane.showInputDialog(
						res.getString("mode_prompt"), "0");

				if (sMode != null && sMode.matches("[012]")) {
					dmr.setMode(DMMode.getMode(Integer.decode(sMode)
							.byteValue()));
				}
				break;
			case "brightness":
				String sBrightness = JOptionPane.showInputDialog(
						res.getString("brightness_prompt"), "0xff");

				if (sBrightness != null
						&& sBrightness.matches("0[x|X][\\p{XDigit}]{2}")) {
					int brightness = Integer.decode(sBrightness);
					dmr.setBrightness(brightness);
				}
				break;
			case "span":
				String sSpan = JOptionPane.showInputDialog(
						res.getString("span_prompt"), "0x0080");

		if (sSpan != null
			&& sSpan.matches("0[x|X][\\p{XDigit}]{4}")) {
					int span = Integer.decode(sSpan);
					dmr.setSpan(span);
				}
				break;
			}

			isSaved = false;
			refreshFrame();
		}
	}

	private class ListSelectionListenerListFrame implements
			ListSelectionListener {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting())
				return;

			int index = listFrame.getSelectedIndex();

			if (index == -1)
				return;

			setActiveFrame(index);
		}
	}
	
	public void setActiveFrame(int index) {
		dmrf = dmr.getFrame(index);
		panelRecord.setFrame(dmrf);
		panelRecord.refresh(true);
		refreshFrame();
	}
	
	public int getNumFrames() {
		return listFrame.getNumFrames();
	}

	private class DotMatrixTestMenuBar extends JMenuBar implements
			ActionListener {
		private static final long serialVersionUID = -6873734389340066641L;

		public DotMatrixTestMenuBar() {
			JMenu mnFile = new JMenu(res.getString("file"));
			mnFile.setMnemonic(KeyEvent.VK_F);

			for (String s : FILE_OPERATION_COMMANDS) {
				JMenuItem button = new JMenuItem(res.getString(s));
				button.setActionCommand(s);
				button.addActionListener(new ActionListenerFileOperation());
				mnFile.add(button);
			}

			// ////////////

			JMenu mnRecord = new JMenu(res.getString("record"));
			mnRecord.setMnemonic(KeyEvent.VK_R);

			for (String s : RECORD_OPERATION_COMMANDS) {
				if (s.equals("-")) {
					mnRecord.addSeparator();
					continue;
				}

				JMenuItem button = new JMenuItem(res.getString(s));

				button.setActionCommand(s);
		button.addActionListener(new ActionListenerRocordOperation());
				mnRecord.add(button);
			}

			// ////////////
			JMenu mnHelp = new JMenu(res.getString("help"));
			mnHelp.setMnemonic(KeyEvent.VK_H);

			JMenuItem miAbout = new JMenuItem(res.getString("about"));
			miAbout.setActionCommand("about");
			miAbout.addActionListener(this);
			mnHelp.add(miAbout);

			JMenu mnLanguage = new JMenu(res.getString("language"));
			JMenuItem miEnglish = new JMenuItem(res.getString("english"));
			miEnglish.setActionCommand("english");
			miEnglish.addActionListener(this);
			mnLanguage.add(miEnglish);

			JMenuItem miChinese = new JMenuItem(res.getString("chinese"));
			miChinese.setActionCommand("chinese");
			miChinese.addActionListener(this);
			mnLanguage.add(miChinese);

			this.add(mnFile);
			this.add(panelRecord.getMenu());
			this.add(mnRecord);
			this.add(mnLanguage);
			this.add(mnHelp);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			switch (e.getActionCommand()) {
			case "about":
				JOptionPane.showMessageDialog(this,
						"For more info, check\nhttp://aguegu.net\nhttp://www.wzona.info",
						res.getString("about"), JOptionPane.OK_OPTION
								| JOptionPane.INFORMATION_MESSAGE);
				break;
			case "chinese":
				locale = Locale.CHINESE;
				init();
				break;
			case "english":
				locale = Locale.ENGLISH;
				init();
				break;
			}
		}
	}

	private void begin() {
		// dmr.clear();
		listFrame.syncToReocrd();

		panelRecord.refresh(true);
		fileRecord = null;
		isSaved = true;
	}

	private void save() {
		dmr.save(fileRecord);
		message = fileRecord.getName() + " saved, " + fileRecord.length()
				+ " bytes.";
		isSaved = true;
	}

	private void refreshFrame() {
		bar.getMenu(0)
				.getMenuComponent(
						Arrays.asList(FILE_OPERATION_COMMANDS).indexOf("save"))
				.setEnabled(fileRecord != null);

		((JToolBar) panelToolbar.getComponent(0)).getComponent(2).setEnabled(
				fileRecord != null);

		int selectedIndex = listFrame.getSelectedIndex();

		bar.getMenu(2)
				.getMenuComponent(
						Arrays.asList(RECORD_OPERATION_COMMANDS).indexOf(
								"update")).setEnabled(selectedIndex != -1);

		((JToolBar) panelToolbar.getComponent(1)).getComponent(2).setEnabled(
				selectedIndex != -1);

		bar.getMenu(2)
				.getMenuComponent(
						Arrays.asList(RECORD_OPERATION_COMMANDS).indexOf(
								"delete")).setEnabled(selectedIndex != -1);

		((JToolBar) panelToolbar.getComponent(1)).getComponent(3).setEnabled(
				selectedIndex != -1);

		labelStatus.setText(message);

		if (fileRecord == null)
			this.setTitle(PROGRAME_NAME);
		else

			this.setTitle(PROGRAME_NAME + " | " + fileRecord.getName() + " "
					+ (isSaved ? "" : "*"));
	}

	private JPanel panelToolBar() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));

		JToolBar toolbarFile = new JToolBar();

		for (String s : FILE_OPERATION_COMMANDS) {
			JButton button = new JButton(new ImageIcon(getClass().getResource(
					"/image/document-" + s + ".png")));
			button.setActionCommand(s);
			button.addActionListener(new ActionListenerFileOperation());
			button.setToolTipText(res.getString(s));
			toolbarFile.add(button);
		}

		panel.add(toolbarFile);

		JToolBar toolbarReord = new JToolBar();

		for (int i = 0; i < Arrays.asList(RECORD_OPERATION_COMMANDS).indexOf(
				"-"); i++) {
			String s = RECORD_OPERATION_COMMANDS[i];
			JButton button = new JButton(new ImageIcon(getClass().getResource(
					"/image/" + s + ".png")));
			button.setActionCommand(s);
	    button.addActionListener(new ActionListenerRocordOperation());
			button.setToolTipText(res.getString(s));
			toolbarReord.add(button);
		}

		panel.add(toolbarReord);

		return panel;
	}

}
