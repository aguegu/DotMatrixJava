package aguegu.dotmatrix;

import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class DotMatrixRecordHeaderPanel extends JPanel
{
	private static final long serialVersionUID = 7530602456593370095L;

	private DotMatrixRecordPanel parent;

	private JSlider sliderBrightness;
	private JSlider sliderSmallSpan;
	private JSlider sliderBigSpan;

	private JCheckBox checkboxUpperLed;
	private JCheckBox checkboxBottomLed;

	private JRadioButton[] radiobuttonModes;

	private CL cl;

	public DotMatrixRecordHeaderPanel(DotMatrixRecordPanel dmrp)
	{
		parent = dmrp;

		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.setBorder(new EmptyBorder(new Insets(0, 4, 0, 0)));
		
		JPanel panelMode = new JPanel(new FlowLayout(FlowLayout.LEFT));
		//panelMode.add(new JLabel("Mode:"));
		
		radiobuttonModes = new JRadioButton[3];
		ButtonGroup bgMode = new ButtonGroup();
		int i = 0;
		for (DMMode mode : DMMode.values())
		{
			radiobuttonModes[i] = new JRadioButton("<html><img src=file:"
					+ getClass().getResource(mode.toString().concat(".png"))
							.getPath() + "/></html>");
			radiobuttonModes[i].setActionCommand(mode.toString());
			radiobuttonModes[i].addActionListener(new ActionListenerMode());
			bgMode.add(radiobuttonModes[i]);
			panelMode.add(radiobuttonModes[i]);
			i++;
		}
		panelMode.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelAttachment = new JPanel(new FlowLayout(FlowLayout.LEFT));
		checkboxUpperLed = new JCheckBox("Upper Led");
		checkboxUpperLed.addActionListener(new ActionListenerAttachment());
		panelAttachment.add(checkboxUpperLed);

		checkboxBottomLed = new JCheckBox("Bottom Led");
		checkboxBottomLed.addActionListener(new ActionListenerAttachment());
		panelAttachment.add(checkboxBottomLed);
		panelAttachment.setAlignmentX(LEFT_ALIGNMENT);
		
		JPanel panelModeAndAttachment = new JPanel();
		panelModeAndAttachment.setLayout(new BoxLayout(panelModeAndAttachment,
				BoxLayout.Y_AXIS));
		panelModeAndAttachment.add(panelMode);
		panelModeAndAttachment.add(panelAttachment);

		cl = new CL();

		sliderBrightness = new JSlider(0, 255, 255);
		sliderBrightness.setMinorTickSpacing(0x20);
		sliderBrightness.setMajorTickSpacing(0x40);
		sliderBrightness.setPaintTicks(true);
		sliderBrightness.setSnapToTicks(true);
		sliderBrightness.addChangeListener(cl);
		sliderBrightness.setAlignmentX(LEFT_ALIGNMENT);

		sliderSmallSpan = new JSlider(0, 65535, 65535);
		sliderSmallSpan.setMinorTickSpacing(0x1000);
		sliderSmallSpan.setSnapToTicks(true);
		sliderSmallSpan.setPaintTicks(true);
		sliderSmallSpan.addChangeListener(cl);
		sliderSmallSpan.setAlignmentX(LEFT_ALIGNMENT);

		sliderBigSpan = new JSlider(0, 65535, 0);
		sliderBigSpan.setMinorTickSpacing(0x1000);
		sliderBigSpan.setSnapToTicks(true);
		sliderBigSpan.setPaintTicks(true);
		sliderBigSpan.addChangeListener(cl);
		sliderBigSpan.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelSliders = new JPanel();
		panelSliders.setLayout(new BoxLayout(panelSliders, BoxLayout.Y_AXIS));
		panelSliders.add(sliderBrightness);
		panelSliders.add(sliderSmallSpan);
		panelSliders.add(sliderBigSpan);

		this.add(panelModeAndAttachment);
		this.add(panelSliders);
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
					parent.getRecordFrame().setBrightness(
							(Integer) sliderBrightness.getValue());
				}
				else if (e.getSource().equals(sliderSmallSpan))
				{
					parent.getRecordFrame().setSmallSpan(
							(Integer) sliderSmallSpan.getValue());
				}
				else if (e.getSource().equals(sliderBigSpan))
				{
					parent.getRecordFrame().setBigSpan(
							(Integer) sliderBigSpan.getValue());
				}

				parent.refresh(true);
			}
		}
	}

	private class ActionListenerAttachment implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (checkboxUpperLed.isSelected() && checkboxBottomLed.isSelected())
				parent.getRecordFrame().setAttachment(DMAttachment.BOTH);
			else if (checkboxUpperLed.isSelected())
				parent.getRecordFrame().setAttachment(DMAttachment.UPPER_LED);
			else if (checkboxBottomLed.isSelected())
				parent.getRecordFrame().setAttachment(DMAttachment.BOTTOM_LED);
			else
				parent.getRecordFrame().setAttachment(DMAttachment.NONE);
			parent.refresh(true);
		}
	}

	private class ActionListenerMode implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			parent.getRecordFrame().setMode(
					DMMode.getMode(e.getActionCommand()));
			parent.refresh(true);
		}
	}

	public void refresh()
	{
		sliderBrightness.removeChangeListener(cl);
		sliderBrightness.setValue(parent.getRecordFrame().getBrightness());
		sliderBrightness.addChangeListener(cl);

		sliderSmallSpan.removeChangeListener(cl);
		sliderSmallSpan.setValue(parent.getRecordFrame().getSmallSpan());
		sliderSmallSpan.addChangeListener(cl);

		sliderBigSpan.removeChangeListener(cl);
		sliderBigSpan.setValue(parent.getRecordFrame().getBigSpan());
		sliderBigSpan.addChangeListener(cl);

		switch (parent.getRecordFrame().getAttachment())
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
			checkboxUpperLed.setSelected(false);
			checkboxBottomLed.setSelected(false);
		default:
			break;
		}

		radiobuttonModes[parent.getRecordFrame().getMode().ordinal()]
				.setSelected(true);
	}

}
