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
 * Server Main UI Class
 * 
 *
 */
public class ErrorServer extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1242867485421163348L;
	private JRadioButton mRdSaw, mRdGbn;
	private JTextField mTxtPort;
	private JButton mBtnStart;
	private JTextArea mTxtStatus;
	private BaseReceiver mReceiver;
	private boolean mIsStarted;
	private Thread mThread;
	
	private JScrollPane mScroller;
	
	public ErrorServer() {
		setPreferredSize(new Dimension(877, 420));
		mIsStarted = false;
				
		JPanel pnlSetting = new JPanel();
		JLabel lblPort = new JLabel("Port: ");
		mTxtPort = new JTextField(4);		
		mTxtPort.setText("5000");
		JLabel lblProtocol = new JLabel("Protocol: ");
		mRdSaw = new JRadioButton("SAW(Stop-And-Wait)");
		mRdGbn = new JRadioButton("GBN(Go-Back-N)");
		mBtnStart = new JButton("Server Start");
		
		pnlSetting.add(lblPort);
		pnlSetting.add(mTxtPort);
		pnlSetting.add(lblProtocol);
		pnlSetting.add(mRdSaw);
		pnlSetting.add(mRdGbn);
		
		JPanel pnlStart = new JPanel();
		pnlStart.add(mBtnStart);
		
		mTxtStatus = new JTextArea();
		
		mScroller = new JScrollPane(mTxtStatus);
		mScroller.setPreferredSize(new Dimension(878, 350));
		
		writeLog("Ready to start server!");
		
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
		
		mBtnStart.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(!mIsStarted) {
					try {
						int iPort = Integer.parseInt(mTxtPort.getText());
						if(iPort < 1000 || iPort > 65535) { 
							JOptionPane.showMessageDialog(null, "Please input a port as 1000 ~ 65535");
							return;
						}
					} catch(Exception ex) {
						JOptionPane.showMessageDialog(null, "Invalid port!");
						return;
					}
					
					mBtnStart.setText("Server Stop");					
					mTxtPort.setEnabled(false);
					mRdSaw.setEnabled(false);
					mRdGbn.setEnabled(false);
					
					if(mRdSaw.isSelected())
						mReceiver = new SAWReceiver(ErrorServer.this, Integer.parseInt(mTxtPort.getText()), "DataRecieved.txt");
					else
						mReceiver = new GBNReceiver(ErrorServer.this, Integer.parseInt(mTxtPort.getText()), "DataRecieved.txt");
					
					mThread = new Thread(mReceiver);
					mThread.start();
				} else {
					mReceiver.stopReceive();
					try {
						mThread.join();
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}					
					mThread = null;

					mBtnStart.setText("Server Start");
					mTxtPort.setEnabled(true);
					mRdSaw.setEnabled(true);
					mRdGbn.setEnabled(true);
				}
				mIsStarted = !mIsStarted;
			}
		});
    }
	
	public void writeLog(String strMsg) {
		mTxtStatus.append(strMsg + "\r\n");
		mTxtStatus.setCaretPosition(mTxtStatus.getDocument().getLength());
	}
}
