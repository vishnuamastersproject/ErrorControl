import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class BaseReceiver implements Runnable {
	protected ErrorServer mApp;
	protected int mPort;
	protected Packet mPacket;
	protected byte[] mBuffer;
	protected String mSaveFilePath;
	protected boolean mStop;
	
	protected ServerSocket mReciever;
	protected Socket mConnection = null;
	protected OutputStream mOut;
	protected InputStream mIn;
	protected FileOutputStream mFout;
		
	public BaseReceiver(ErrorServer app, int iPort, String strSavePath) {
		mApp = app;
		mPort = iPort;
		mSaveFilePath = strSavePath;
		
		mPacket = new Packet();
		mBuffer = new byte[Packet.PACKET_SIZE];
		mStop = false;
	}
	
	public void stopReceive() {
		mStop = true;
	}


	public void run() {
		// TODO Auto-generated method stub		
	}
}
