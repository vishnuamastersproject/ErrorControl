import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

/**
 * Go-Back-N algorithm Receiver class
 * 
 *
 */
public class GBNReceiver extends BaseReceiver {
	protected Map<Byte, Packet> mPackets = null;
	public GBNReceiver(ErrorServer app, int iPort, String strSavePath) {
		super(app, iPort, strSavePath);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void stopReceive() {
		super.stopReceive();
		try {
			mReciever.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		mPackets = new TreeMap<Byte, Packet>();
		
		while(!mStop) {
			try {
				// open server socket and wait connection				
				mApp.writeLog("GBN Receiver start!");
				mReciever = new ServerSocket(mPort);			
				mApp.writeLog("waiting for connection...");
				mConnection = mReciever.accept();
				
				mApp.writeLog("Connection established!");
	
				mOut = mConnection.getOutputStream();
				mOut.flush();
				mIn = mConnection.getInputStream();
								
				mPackets.clear();
				
				do {
					try {		
						// read packet from client
						int iReadLen = mIn.read(mBuffer, 0, Packet.PACKET_SIZE);
						if(iReadLen < 1) {
							mApp.writeLog("Read error!");
							break;
						}					
						if(!mPacket.parse(mBuffer)) {
							mApp.writeLog(mPacket.error);
							break;
						}
						// if packet is data, add it to packets list
						if(mPacket.type == Packet.TYPE_DATA) {
							mPackets.put(mPacket.sequence, mPacket.copy());
						}
						// build ACK packet, send it to client.
						mBuffer = Packet.buildData(Packet.TYPE_ACK, mPacket.sequence, null, 0);
						mOut.write(mBuffer, 0, Packet.PACKET_SIZE);						
					} catch (Exception e) {
						mApp.writeLog("Error! " + e.getMessage());
						throw e;
					}
				} while (mPacket.type != Packet.TYPE_END); // if packet type is end, end to read
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					mIn.close();
					mOut.close();
					mReciever.close();
					mFout.close();
				} catch (Exception e) {
				}
				
				mApp.writeLog("Stopped!");
			}
			
			try {
				// write data to file
				mFout = new FileOutputStream(mSaveFilePath);
				Set<Entry<Byte, Packet>> s = mPackets.entrySet();
				Iterator<Entry<Byte, Packet>> iter = s.iterator();
				while(iter.hasNext()) {
					Entry<Byte, Packet> entry = iter.next();
					mFout.write(entry.getValue().data, 0, entry.getValue().datalen);
				}
				
			} catch(Exception e) {				
			} finally {
				try {
					mFout.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
	}
}
