package aguegu.dotmatrix;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

public class DotMatrixTest extends JFrame
{
	private static final long serialVersionUID = 5805285424717739698L;

	private DotMatrixRecord dmr;
	private DotMatrixRecordList listFrame;
	//private DotMatrix dm;

	DotMatrixRecordPanel panelRecord;
	DotMatrixTestMenuBar bar;

	private JLabel labelStatus;

	// Menu
	private static final String[] FILE_OPERATIONS_LABEL = { "New", "Open...",
			"Save", "Save As...", "Exit" };
	private static final String[] FILE_OPERATIONS_COMMAND = { "new", "open",
			"save", "saveAs", "exit" };
	private static final String[] RECORD_OPERACTIONS = new String[] { "Append",
			"Insert", "Update", "Delete" };

	private static final String PROGRAME_NAME = new String("dot-matrix (Java)");

	// Status

	private File fileRecord = null;
	private String message;
	private boolean isSaved = true;
	private DotMatrixRecordFrame dmrf;

	public static void main(String[] args)
	{
		DotMatrixTest dmt = new DotMatrixTest();
		dmt.init();
	}

	public void init()
	{
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		dmrf = new DotMatrixRecordFrame(DotMatrixRecordFrame.Type.Batch, 0);
		panelRecord = new DotMatrixRecordPanel(dmrf);

		this.getContentPane().add(BorderLayout.CENTER, panelRecord);
		this.getContentPane().add(BorderLayout.WEST,
				panelRecord.getPanelFrameOperation());

		labelStatus = new JLabel(message);
		labelStatus.setBorder(BorderFactory
				.createBevelBorder(BevelBorder.LOWERED));
		this.add(BorderLayout.SOUTH, labelStatus);

		dmr = new DotMatrixRecord();
		listFrame = new DotMatrixRecordList(dmr);

		listFrame
				.addListSelectionListener(new ListSelectionListenerListFrame());
		this.getContentPane().add(BorderLayout.EAST, listFrame);

		bar = new DotMatrixTestMenuBar();
		this.setJMenuBar(bar);

		message = "http://aGuegu.net";

		begin();
		refreshFrame();

		this.setLocation(100, 100);
		this.setSize(this.getPreferredSize());
		this.setResizable(false);

		this.setVisible(true);
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
			File file = null;
			int result;

			switch (e.getActionCommand())
			{
			case "new":
				if (fileRecord != null && !isSaved)
				{
					result = JOptionPane.showConfirmDialog(null,
							"Save changes to " + fileRecord.getName() + "?",
							"warning", JOptionPane.YES_NO_CANCEL_OPTION,
							JOptionPane.QUESTION_MESSAGE);

					if (result == JOptionPane.CANCEL_OPTION)
						break;

					if (result == JOptionPane.YES_OPTION)
						save();
				}
				begin();
				break;
			case "open":
				result = fs.showOpenDialog(null);
				file = fs.getSelectedFile();
				if (file == null || result != JFileChooser.APPROVE_OPTION)
					break;

				fileRecord = file;
				dmr.readRecord(fileRecord);
				listFrame.syncToReocrd();

				if (dmr.getLength() > 0)
				{
					listFrame.setSelectedIndex(0);
				}

				message = fileRecord.getName() + " loaded, "
						+ fileRecord.length() + " bytes.";
				break;
			case "save":
				save();
				break;
			case "saveAs":
				fs.setSelectedFile(new File("record.dat"));
				result = fs.showSaveDialog(null);
				file = fs.getSelectedFile();
				if (file == null || result != JFileChooser.APPROVE_OPTION)
					break;
				fileRecord = file;
				save();
				break;
			case "exit":
				if (fileRecord != null && !isSaved)
				{
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

	private class ActionListenerRocordOperation implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			int index = listFrame.getSelectedIndex();
/*
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
*/

			isSaved = false;
			refreshFrame();
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

			dmrf = dmr.getFrame(index);
			panelRecord.setDotMatrixRecordFrame(dmrf);
			panelRecord.refresh(true);
			refreshFrame();
		}
	}

	private class DotMatrixTestMenuBar extends JMenuBar implements
			ActionListener
	{
		private static final long serialVersionUID = -6873734389340066641L;

		public DotMatrixTestMenuBar()
		{
			JMenu mnFile = new JMenu("File");

			JMenu mnRecord = new JMenu("Record");
			JMenu mnHelp = new JMenu("Help");

			mnFile.setMnemonic(KeyEvent.VK_F);

			mnRecord.setMnemonic(KeyEvent.VK_R);
			mnHelp.setMnemonic(KeyEvent.VK_H);

			for (String s : FILE_OPERATIONS_LABEL)
			{
				JMenuItem button = new JMenuItem(s);
				button.setActionCommand(FILE_OPERATIONS_COMMAND[Arrays.asList(
						FILE_OPERATIONS_LABEL).indexOf(s)]);
				button.addActionListener(new ActionListenerFileOperation());
				mnFile.add(button);
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
			this.add(panelRecord.getMenu());
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

	private void begin()
	{
		dmr.clear();
		listFrame.syncToReocrd();
		//dm.clear(false);

		panelRecord.refresh(true);
		fileRecord = null;
		isSaved = true;
	}

	private void save()
	{
		dmr.save(fileRecord);
		message = fileRecord.getName() + " saved, " + fileRecord.length()
				+ " bytes.";
		isSaved = true;
	}

	private void refreshFrame()
	{
		bar.getMenu(0)
				.getMenuComponent(
						Arrays.asList(FILE_OPERATIONS_LABEL).indexOf("Save"))
				.setEnabled(fileRecord != null);

		int selectedIndex = listFrame.getSelectedIndex();

		bar.getMenu(2)
				.getMenuComponent(
						Arrays.asList(RECORD_OPERACTIONS).indexOf("Update"))
				.setEnabled(selectedIndex != -1);

		bar.getMenu(2)
				.getMenuComponent(
						Arrays.asList(RECORD_OPERACTIONS).indexOf("Delete"))
				.setEnabled(selectedIndex != -1);

		labelStatus.setText(message);

		if (fileRecord == null)
		{
			this.setTitle(PROGRAME_NAME);
		}
		else
		{
			this.setTitle(PROGRAME_NAME + " | " + fileRecord.getName() + " "
					+ (isSaved ? "" : "*"));
		}

	}

}
