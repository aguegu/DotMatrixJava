package aguegu.dotmatrix;

import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSlider;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class DMRecordHeaderPanel extends JPanel {
	private static final long serialVersionUID = 7530602456593370095L;

	private DMRecordPanel parent;

	private JSlider sliderBrightness;
	private JSlider sliderSpan;

	private JCheckBox checkboxUpperLed;
	private JCheckBox checkboxBottomLed;

	private JRadioButtonMenuItem[] radiobuttonModes;

	private CL cl;

	public DMRecordHeaderPanel(DMRecordPanel dmrp, ResourceBundle res) {
		parent = dmrp;
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.setBorder(new EmptyBorder(new Insets(0, 4, 0, 0)));

		JPanel panelMode = new JPanel(new FlowLayout(FlowLayout.LEFT));

		radiobuttonModes = new JRadioButtonMenuItem[3];
		ButtonGroup bgMode = new ButtonGroup();
		int i = 0;
		for (DMMode mode : DMMode.values()) {
			radiobuttonModes[i] = new JRadioButtonMenuItem(
					new ImageIcon(getClass().getResource(
							"/image/" + mode.toString().toLowerCase() + ".png")));

			radiobuttonModes[i].setActionCommand(mode.toString());
			radiobuttonModes[i].addActionListener(new ActionListenerMode());
			bgMode.add(radiobuttonModes[i]);
			panelMode.add(radiobuttonModes[i]);
			i++;
		}
		panelMode.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelAttachment = new JPanel(new FlowLayout(FlowLayout.LEFT));
		checkboxUpperLed = new JCheckBox(res.getString("upper_led"));
		checkboxUpperLed.addActionListener(new ActionListenerAttachment());
		panelAttachment.add(checkboxUpperLed);

		checkboxBottomLed = new JCheckBox(res.getString("bottom_led"));
		checkboxBottomLed.addActionListener(new ActionListenerAttachment());
		panelAttachment.add(checkboxBottomLed);
		panelAttachment.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelModeAndAttachment = new JPanel();
		panelModeAndAttachment.setLayout(new BoxLayout(panelModeAndAttachment,
				BoxLayout.Y_AXIS));
		panelModeAndAttachment.add(new JLabel(res.getString("mode") + ":"));
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

		sliderSpan = new JSlider(0, 0x0800, 0x0080);
		sliderSpan.setMinorTickSpacing(0x10);
		sliderSpan.setSnapToTicks(true);
		sliderSpan.setPaintTicks(true);
		sliderSpan.addChangeListener(cl);
		sliderSpan.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelSliders = new JPanel();
		panelSliders.setLayout(new BoxLayout(panelSliders, BoxLayout.Y_AXIS));
		panelSliders.add(new JLabel(res.getString("brightness") + ":"));
		panelSliders.add(sliderBrightness);
		panelSliders.add(new JLabel(res.getString("time_span") + ":"));
		panelSliders.add(sliderSpan);

		this.add(panelModeAndAttachment);
		this.add(panelSliders);
	}

	private class CL implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent e) {
			if (e.getSource() instanceof JSlider) {
				if (e.getSource().equals(sliderBrightness)) {
					parent.getRecordFrame().setBrightness(
							(Integer) sliderBrightness.getValue());
				} else if (e.getSource().equals(sliderSpan)) {
					parent.getRecordFrame().setSpan(
							(Integer) sliderSpan.getValue());
				}

				parent.refresh(true);
			}
		}
	}

	private class ActionListenerAttachment implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
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

	private class ActionListenerMode implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			parent.getRecordFrame().setMode(
					DMMode.getMode(e.getActionCommand()));
			parent.refresh(true);
		}
	}

	public void refresh() {
		sliderBrightness.removeChangeListener(cl);
		sliderBrightness.setValue(parent.getRecordFrame().getBrightness());
		sliderBrightness.addChangeListener(cl);

		sliderSpan.removeChangeListener(cl);
		sliderSpan.setValue(parent.getRecordFrame().getSpan());
		sliderSpan.addChangeListener(cl);

		switch (parent.getRecordFrame().getAttachment()) {
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
