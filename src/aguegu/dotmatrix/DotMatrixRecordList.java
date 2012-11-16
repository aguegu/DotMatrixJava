package aguegu.dotmatrix;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

public class DotMatrixRecordList extends JList<DotMatrixRecordFrame>
{
	private static final long serialVersionUID = -8408932545525051039L;
	private DotMatrixRecord dmr;
	private DefaultListModel<DotMatrixRecordFrame> lm;

	public DotMatrixRecordList(DotMatrixRecord dmr)
	{
		this.dmr = dmr;
		this.lm = new DefaultListModel<DotMatrixRecordFrame>();
		
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		this.setModel(lm);
		this.setCellRenderer(new DotMatrixRecordCellRender());

		syncToReocrd();
	}

	public void syncToReocrd()
	{
		lm.clear();		
		for (DotMatrixRecordFrame dmrf : this.dmr.getFrames())
		{
			lm.addElement(dmrf);			
		}
	}

	class DotMatrixRecordCellRender extends JLabel implements
			ListCellRenderer<DotMatrixRecordFrame>
	{
		private static final long serialVersionUID = 6448688964390706942L;

		public DotMatrixRecordCellRender()
		{
			setOpaque(true);
		}

		@Override
		public Component getListCellRendererComponent(
				JList<? extends DotMatrixRecordFrame> list,
				DotMatrixRecordFrame value, int index, boolean isSelected,
				boolean cellHasFocus)
		{
			DotMatrixRecordFrame entry = (DotMatrixRecordFrame) value;
			this.setText(Integer.toString(entry.getIndex()));

			if (isSelected)
			{
				setBackground(Color.blue);
				setForeground(Color.white);
			}
			else
			{
				setBackground(Color.white);
				setForeground(Color.black);
			}
			return this;
		}
	}

	

}
