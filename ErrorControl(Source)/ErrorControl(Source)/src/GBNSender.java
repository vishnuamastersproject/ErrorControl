import java.io.FileInputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

/**
 * Go-Back-N Sender class
 *
 *
 */
public class GBNSender extends BaseSender {
	protected Map<Byte, byte[]> mPackets;
	
	public GBNSender(ErrorClient client, String strIP, int iPort, int iPercent, String strSendFilePath) {
		super(client, strIP, iPort, iPercent, strSendFilePath);
		mPackets = new HashMap<Byte, byte[]>();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		try {
			byte sequence = 0;
			// connect to server
			mClient.writeLog("Connecting server...");
			mSock = new Socket(mServerIP, mPort);
			// set timeout for read ACK.
			mSock.setSoTimeout(TIMEOUT);
			mOut = mSock.getOutputStream();
			mIn = mSock.getInputStream();
			
			Random rnd = new Random(System.currentTimeMillis());
			
			mFin = new FileInputStream(mSendFilePath);
			byte[] packet;
			mPackets.clear();
			
			do {
				// read unknowledge packet size
				int iPacketCount = mPackets.size();
				while(!mStop) {
					// if size of packets is window size, break
					if(iPacketCount >= Packet.GBN_WINDOW_SIZE)
						break;
					
					// read data from file, build packet and add it to packets
					int iReadLen = mFin.read(mBuffer, 0, Packet.CONTENT_SIZE);
					if(iReadLen < 0) {
						mClient.writeLog("Ended to send file!");
						packet = Packet.buildData(Packet.TYPE_END, sequence, null, 0);
						mPackets.put(sequence, packet);
						break;
					} else {
						packet = Packet.buildData(Packet.TYPE_DATA, sequence, mBuffer, iReadLen);
						mPackets.put(sequence, packet);
					}
					iPacketCount++;
					sequence++;
				}
				
				Set<Entry<Byte, byte[]>> s = mPackets.entrySet();
				Iterator<Entry<Byte, byte[]>> iter = s.iterator();
				while(iter.hasNext()) {
					Entry<Byte, byte[]> entry = iter.next();
					// generate random number and decide send data or simulate loss.
					int iRandom = rnd.nextInt() % 100;
					if(iRandom >= mLossPercentage) {
						mClient.writeLog("Sending packet...sequence=" + entry.getKey());
						mOut.write(entry.getValue(), 0, Packet.PACKET_SIZE);
					} else {
						mClient.writeLog("Simulated to loss!");
					}
				}

				// wait ACK				
				mClient.writeLog("Receiving ACK...");
				while(!mStop) {
					try {
						mIn.read(mBuffer, 0, Packet.PACKET_SIZE);
						
						if(!mPacket.parse(mBuffer)) {
							mClient.writeLog("Parse packet error! " + mPacket.error);
							break;
						}
						if(mPacket.type != Packet.TYPE_ACK) {
							mClient.writeLog("Packet type is not ACK!");
							break;
						}
						mClient.writeLog("Received ACK of sequence " + mPacket.sequence);
						mPackets.remove(mPacket.sequence);
						
						// If received all ACK of packets, break; 
						if(mPackets.size() < 1)
							break;
					} catch(Exception e) {
						// timeout, data may be lossed. send it next time.
						mClient.writeLog("Server may not receive data! try send packet again...");
						break;
					}
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
