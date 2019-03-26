package aguegu.dotmatrix;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class DMRecordHeaderPanel extends JPanel {
	private static final long serialVersionUID = 7530602456593370095L;

	private DMRecordPanel parent;

	private SerialPort port;
	private OutputStream outputStream;
	private JComboBox<String> baudBox;
	private JComboBox<String> portBox;
	private Timer timer;
	private JTextField delayBox;
	private JCheckBox loopChk;

	public DMRecordHeaderPanel(DMRecordPanel dmrp, final ResourceBundle res) {
		parent = dmrp;
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.setBorder(new EmptyBorder(new Insets(0, 4, 0, 0)));
		
		JPanel panelMode = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		panelMode.setAlignmentX(LEFT_ALIGNMENT);

		JPanel panelAttachment = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panelAttachment.add(new JLabel(res.getString("comport") + ":"));
		
		portBox = new JComboBox<String>(enumeratePorts().toArray(new String[]{}));
		portBox.setEditable(true);
		
		panelAttachment.add(portBox);
		
		final JButton refreshBtn = new JButton(res.getString("refresh"));
		refreshBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				portBox.removeAllItems();
				portBox.setModel(new DefaultComboBoxModel<String>(enumeratePorts().toArray(new String[]{})));
			}
		});
		panelAttachment.add(refreshBtn);
		
		panelAttachment.add(new JLabel(res.getString("baud") + ":"));
		baudBox = new JComboBox<String>(new String[]{"9600","19200","38400","57600","115200"});
		baudBox.setEditable(true);
		panelAttachment.add(baudBox);
		
		final JButton playBtn = new JButton(res.getString("play"));
		playBtn.setEnabled(false);
		
		final JButton closeBtn = new JButton(res.getString("close_serial"));
		closeBtn.setEnabled(false);
		
		final JButton openBtn = new JButton(res.getString("open_serial"));
		openBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (portBox.getSelectedItem() == null) return;
				String input = ((String)portBox.getSelectedItem()).trim();
				if (!input.isEmpty()) {
					if (openSerial(input)) {
						closeBtn.setEnabled(true);
						openBtn.setEnabled(false);
						playBtn.setEnabled(true);
					}
				}
			}
		});
		panelAttachment.add(openBtn);
		panelAttachment.add(closeBtn);
		
		closeBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				closeSerial();
				closeBtn.setEnabled(false);
				openBtn.setEnabled(true);
				playBtn.setEnabled(false);
			}
		});

		playBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (timer != null) {
					timer.cancel();
					timer = null;
					playBtn.setText(res.getString("play"));
					closeBtn.setEnabled(true);
					return;
				}
				
				int delay = 20; // default ms
				try {
					delay = Integer.parseInt(delayBox.getText());
					if (delay <= 0) {
						throw new NumberFormatException("Not a positive number");
					}
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(null, "Invalid frame update delay!", "Play error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				if (parent.getNumFrames() > 0) {
					TimerTask task = new TimerTask() {
						private int counter = 0;
						@Override
						public void run() {
							int num = parent.getNumFrames();
							if (num > 0) {
								// still have frames left
								counter = (counter+1)%num;
								parent.setFrame(counter);
								
								try {
									byte data[] = parent.getRecordFrame().getSimpleData();
									outputStream.write(data);
								} catch (IOException e) {
									this.cancel();
									JOptionPane.showMessageDialog(null, "Unable to play:\n"+e.getMessage(), "Play error", JOptionPane.ERROR_MESSAGE);
								}
							} else {
								// e.g. new project was created and all frames are gone!
								timer.cancel();
								timer = null;
								playBtn.setText(res.getString("play"));
								closeBtn.setEnabled(true);
							}
						}
					};
					
					if (loopChk.isSelected()) {
						playBtn.setText(res.getString("stop"));
						closeBtn.setEnabled(false);
						
						timer = new Timer();
						timer.schedule(task, 0, delay);
					} else {
						task.run();
					}
				} else {
					JOptionPane.showMessageDialog(null, "Nothing to play - add frames first!", "Play error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		JPanel panelModeAndAttachment = new JPanel();
		panelModeAndAttachment.setLayout(new BoxLayout(panelModeAndAttachment, BoxLayout.Y_AXIS));
		panelModeAndAttachment.add(panelMode);
		panelModeAndAttachment.add(panelAttachment);

		JPanel panelBelow = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panelBelow.add(new JLabel(res.getString("delay") + ": "));
		
		delayBox = new JTextField(res.getString("delay_ms"), 10);
		panelBelow.add(delayBox);
		panelBelow.add(playBtn);
		
		loopChk = new JCheckBox(res.getString("anim_loop"), true);
		panelBelow.add(playBtn);
		panelBelow.add(loopChk);
		panelModeAndAttachment.add(panelBelow);
		
		JPanel panelSliders = new JPanel();
		panelSliders.setLayout(new BoxLayout(panelSliders, BoxLayout.Y_AXIS));

		this.add(panelModeAndAttachment);
		this.add(panelSliders);
	}
	
	public void updateParent(DMRecordPanel parent) {
		this.parent = parent;
	}
	
	public List<String> enumeratePorts() {
		// scan available COM ports
		List<String> ports = new ArrayList<String>();
		System.out.println("enumerate serial ports");
		
		try {
			Enumeration<?> port_list = CommPortIdentifier.getPortIdentifiers();
			
		    while (port_list.hasMoreElements()) {
		    	CommPortIdentifier port_id = (CommPortIdentifier) port_list.nextElement();
		        if (port_id.getPortType() == CommPortIdentifier.PORT_SERIAL) {
		        	ports.add(port_id.getName());
		        }
		    }
		} catch (UnsatisfiedLinkError e) {
			JOptionPane.showMessageDialog(null, "Error initializing serial port:\n" + e.getMessage(), "Serial error", JOptionPane.ERROR_MESSAGE);
		} finally {
			System.out.println(ports.size()+" ports found: " + ports);
			if (ports.isEmpty()) {
				ports.add("No ports found!"); // default
			}
		}
		
		return ports;
	}
	
	public boolean openSerial(String name) {
		try {
			System.out.println("open serial: " + name);
			
		    Enumeration<?> port_list = CommPortIdentifier.getPortIdentifiers();
		    boolean found = false;
		
		    while (port_list.hasMoreElements()) {
		        // Get the list of ports
		        CommPortIdentifier port_id = (CommPortIdentifier) port_list.nextElement();
		        
		        if (port_id.getPortType() == CommPortIdentifier.PORT_SERIAL && port_id.getName().equals(name)) {
		        	found = true;
		        	
		            try {
		            	// attempt to open
		                port = (SerialPort) port_id.open("PortListOpen", 20);
		                if (port == null) {
		                	throw new Exception("Cannot open port: " + name);
		                }
		                
		                System.out.println("serial port opened: " + name);
		  
	                    int baudRate = Integer.parseInt((String)baudBox.getSelectedItem());
	                    port.setSerialPortParams(
	                            baudRate,
	                            SerialPort.DATABITS_8,
	                            SerialPort.STOPBITS_1,
	                            SerialPort.PARITY_NONE);
	                    port.setDTR(true);
	                    
	                    outputStream = port.getOutputStream();
	                    return true;
	                } catch (UnsupportedCommOperationException e) {
	                    JOptionPane.showMessageDialog(null, "Invalid serial parameters:\n" + e.getMessage(), "Serial error", JOptionPane.ERROR_MESSAGE);
	                } catch (PortInUseException e) {
	                	String owner = port_id.getCurrentOwner();
	                	JOptionPane.showMessageDialog(null, "The port is already in use! Owner: " + (owner != null ? owner : "unknown"), 
	                			"Serial error", JOptionPane.ERROR_MESSAGE);
	                } catch (Exception e) {
	                	JOptionPane.showMessageDialog(null, "I/O error:\n" + e.getMessage(), "Serial error", JOptionPane.ERROR_MESSAGE);
	                }
	            }
		    }
		    
		    // not found
		    if (!found) {
		    	JOptionPane.showMessageDialog(null, "Serial port not found: " + name, "Serial error", JOptionPane.ERROR_MESSAGE);
		    }
		} catch (UnsatisfiedLinkError e) {
			JOptionPane.showMessageDialog(null, "Error initializing serial port:\n" + e.getMessage(), "Serial error", JOptionPane.ERROR_MESSAGE);
		}
		
	    return false;
    }
	
	private void closeSerial() {
		if (port != null) {
			System.out.println("closing serial port");
			port.close();
		}
		port = null;
		outputStream = null;
	}

	public void refresh() {
		//sliderBrightness.removeChangeListener(cl);
		//sliderBrightness.setValue(parent.getRecordFrame().getBrightness());
		//sliderBrightness.addChangeListener(cl);

		//sliderSpan.removeChangeListener(cl);
		//sliderSpan.setValue(parent.getRecordFrame().getSpan());
		//sliderSpan.addChangeListener(cl);

		/*switch (parent.getRecordFrame().getAttachment()) {
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
				.setSelected(true);*/
	}

}
