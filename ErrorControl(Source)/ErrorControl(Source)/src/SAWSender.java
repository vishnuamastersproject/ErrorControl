import java.io.FileInputStream;
import java.net.Socket;
import java.util.Random;

/**
 * Stop-And-Wait algorithm sender class
 * @author Jimmy
 *
 */
public class SAWSender extends BaseSender {
		
	public SAWSender(ErrorClient client, String strIP, int iPort, int iPercent, String strSendFilePath) {
		super(client, strIP, iPort, iPercent, strSendFilePath);
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			byte sequence = 0;
			mClient.writeLog("Connecting server...");
			mSock = new Socket(mServerIP, mPort);
			mSock.setSoTimeout(TIMEOUT);
			mOut = mSock.getOutputStream();
			mIn = mSock.getInputStream();
			
			Random rnd = new Random(System.currentTimeMillis());
			
			mFin = new FileInputStream(mSendFilePath);
			byte[] packet;
			do {
				// read data from file
				int iReadLen = mFin.read(mBuffer, 0, Packet.CONTENT_SIZE);
				if(iReadLen < 0) {
					mClient.writeLog("Ended to send file!");
					packet = Packet.buildData(Packet.TYPE_END, sequence, null, 0);
					mOut.write(packet, 0, Packet.PACKET_SIZE);
					break;
				} else {
					while(!mStop) {
						// generate random number, and decide send packet or simulate to loss
						int iRandom = rnd.nextInt() % 100;
						if(iRandom >= mLossPercentage) {
							mClient.writeLog("Sending packet...sequence=" + sequence);
							packet = Packet.buildData(Packet.TYPE_DATA, sequence, mBuffer, iReadLen);
							mOut.write(packet, 0, Packet.PACKET_SIZE);
						} else {
							mClient.writeLog("Simulated to loss!");
						}
						
						mClient.writeLog("Receiving ACK...");
						try {
							// read ack from server
							mIn.read(mBuffer, 0, Packet.PACKET_SIZE);
							// if success to read ack, break
							break;
						} catch(Exception e) {
							// timeout, data may be lossed. try to send it again
							mClient.writeLog("Server may not receive data! try send packet again...");
							continue;
						}
					}
					if(!mPacket.parse(mBuffer)) {
						mClient.writeLog("Parse packet error! " + mPacket.error);
						break;
					}
					if(mPacket.type != Packet.TYPE_ACK) {
						mClient.writeLog("Packet type is not ACK!");
						break;
					}
					if(mPacket.sequence != sequence) {
						mClient.writeLog("Sequence error! expected: " + sequence + ", got: " + mPacket.sequence);
						break;
					}
					sequence++;
				}
			} while(!mStop);
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				mIn.close();
				mOut.close();
				mSock.close();
			} catch(Exception e) {}
		}
		mClient.writeLog("Stopped!");
		mClient.changeControls();
	}
}
