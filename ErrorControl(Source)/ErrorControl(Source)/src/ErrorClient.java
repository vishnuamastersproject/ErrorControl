import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Client Main UI
 * 
 *
 */
public class ErrorClient extends JPanel {	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5714581919432348438L;
	private JRadioButton mRdSaw, mRdGbn;
	private JTextField mTxtServerIP, mTxtPort, mTxtPercent;
	private JButton mBtnStart;
	private JTextArea mTxtStatus;
	
	private boolean mIsStarted;
	private Thread mThread;
	
	private BaseSender mSender;
	private JScrollPane mScroller;
	
	public ErrorClient() {
		setPreferredSize(new Dimension(877, 420));
		JPanel pnlSetting = new JPanel();
		JLabel lblServer = new JLabel("Server: ");
		mTxtServerIP = new JTextField(15);
		mTxtServerIP.setText("10.20.233.118");
		JLabel lblPort = new JLabel("Port: ");
		mTxtPort = new JTextField(4);		
		mTxtPort.setText("5000");
		JLabel lblPercent = new JLabel("Percentage of Loss: ");
		mTxtPercent = new JTextField(3);
		mTxtPercent.setText("50");
		JLabel lblProtocol = new JLabel("Protocol: ");
		mRdSaw = new JRadioButton("SAW(Stop-And-Wait)");
		mRdGbn = new JRadioButton("GBN(Go-Back-N)");
		
		mBtnStart = new JButton("Send");

		pnlSetting.add(lblServer);
		pnlSetting.add(mTxtServerIP);
		pnlSetting.add(lblPort);
		pnlSetting.add(mTxtPort);
		pnlSetting.add(lblPercent);
		pnlSetting.add(mTxtPercent);
		pnlSetting.add(lblProtocol);
		pnlSetting.add(mRdSaw);
		pnlSetting.add(mRdGbn);
		
		JPanel pnlStart = new JPanel();
		pnlStart.add(mBtnStart);

		mTxtStatus = new JTextArea();
		
		mScroller = new JScrollPane(mTxtStatus);
		mScroller.setPreferredSize(new Dimension(878, 350));
		
		add(pnlSetting, BorderLayout.WEST);
		add(pnlStart, BorderLayout.EAST);
		add(mScroller, BorderLayout.SOUTH);
		
		mRdSaw.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				mRdSaw.setSelected(true);
				mRdGbn.setSelected(false);
			}
		});
		mRdGbn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				mRdSaw.setSelected(false);
				mRdGbn.setSelected(true);
			}
		});
		mRdSaw.setSelected(true);
		
		writeLog("ready to start!");
		mBtnStart.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(!mIsStarted) {
					int iPort, iPercent;
					try {
						iPort = Integer.parseInt(mTxtPort.getText());
						iPercent = Integer.parseInt(mTxtPercent.getText());
						if(iPort < 1000 || iPort > 65535) { 
							JOptionPane.showMessageDialog(null, "Please input a port as 1000 ~ 65535");
							return;
						}
						if(iPercent < 1 || iPercent > 100) {
							JOptionPane.showMessageDialog(null, "Please input a percentage as 1 ~ 100");
							return;
						}
					} catch(Exception ex) {
						JOptionPane.showMessageDialog(null, "Invalid port or percent!");
						return;
					}
					
					mBtnStart.setText("Stop");	
					mTxtServerIP.setEnabled(false);
					mTxtPort.setEnabled(false);
					mTxtPercent.setEnabled(false);
					mRdSaw.setEnabled(false);
					mRdGbn.setEnabled(false);
					
					if(mRdSaw.isSelected())
						mSender = new SAWSender(ErrorClient.this, mTxtServerIP.getText(), iPort, iPercent, "DataSent.txt");
					else
						mSender = new GBNSender(ErrorClient.this, mTxtServerIP.getText(), iPort, iPercent, "DataSent.txt");
					
					mThread = new Thread(mSender);
					mThread.start();
					mIsStarted = true;
				} else {
					mSender.stopSend();
					try {
						mThread.join();
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
	}
	
	public void changeControls() {
		mBtnStart.setText("Send");
		mTxtServerIP.setEnabled(true);
		mTxtPort.setEnabled(true);
		mTxtPercent.setEnabled(true);
		mRdSaw.setEnabled(true);
		mRdGbn.setEnabled(true);
		mIsStarted = false;
	}

	public void writeLog(String strMsg) {
		mTxtStatus.append(strMsg + "\r\n");
		mTxtStatus.setCaretPosition(mTxtStatus.getDocument().getLength());
	}
}
