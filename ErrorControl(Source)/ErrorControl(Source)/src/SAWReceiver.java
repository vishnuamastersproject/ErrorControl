import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;

/**
 * Stop-And-Wait algorithm receiver class
 * 
 *
 */
public class SAWReceiver extends BaseReceiver {

	public SAWReceiver(ErrorServer app, int iPort, String strSavePath) {
		super(app, iPort, strSavePath);
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
		while(!mStop) {
			try {
				mApp.writeLog("SAW Receiver start!");
				mReciever = new ServerSocket(mPort);			
				mApp.writeLog("waiting for connection...");
				mConnection = mReciever.accept();
				
				mApp.writeLog("Connection established!");
	
				mOut = mConnection.getOutputStream();
				mOut.flush();
				mIn = mConnection.getInputStream();
				
				mFout = new FileOutputStream(mSaveFilePath);
				
				byte sequence = 0;
				
				do {
					try {		
						// read from client
						int iReadLen = mIn.read(mBuffer, 0, Packet.PACKET_SIZE);
						if(iReadLen < 1) {
							mApp.writeLog("Read error!");
							break;
						}					
						if(!mPacket.parse(mBuffer)) {
							mApp.writeLog(mPacket.error);
							break;
						}
						if(mPacket.sequence != sequence) {
							mApp.writeLog("Sequence number error! expected: " + sequence + ", got: " + mPacket.sequence);
							break;
						} else {
							// if received packet type is data, write data to file and send ack to client.
							if(mPacket.type == Packet.TYPE_DATA) {
								mFout.write(mPacket.data, 0, mPacket.datalen);
							}
							mBuffer = Packet.buildData(Packet.TYPE_ACK, mPacket.sequence, null, 0);
							mOut.write(mBuffer, 0, Packet.PACKET_SIZE);
							sequence++;
						}
					} catch (Exception e) {
						mApp.writeLog("Error! " + e.getMessage());
						throw e;
					}
				} while (mPacket.type != Packet.TYPE_END);
				
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
		}
	}
}
