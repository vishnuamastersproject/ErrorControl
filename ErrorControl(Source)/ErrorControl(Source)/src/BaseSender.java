import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


public class BaseSender implements Runnable {
	protected final static int TIMEOUT = 5000;
	
	protected String mServerIP;
	protected int mPort;
	protected int mLossPercentage;
	protected String mSendFilePath;
	protected Socket mSock;
	protected OutputStream mOut;
	protected InputStream mIn;
	protected ErrorClient mClient;
	
	protected FileInputStream mFin;
	
	protected byte[] mBuffer;
	protected Packet mPacket;
	protected boolean mStop;

	public void stopSend() {
		mStop = true;
	}
	
	public BaseSender(ErrorClient client, String strIP, int iPort, int iPercent, String strSendFilePath) {
		mClient = client;
		mServerIP = strIP;
		mPort = iPort;
		mLossPercentage = iPercent;
		mSendFilePath = strSendFilePath;
		mBuffer = new byte[Packet.PACKET_SIZE];
		mPacket = new Packet();
		mStop = false;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
