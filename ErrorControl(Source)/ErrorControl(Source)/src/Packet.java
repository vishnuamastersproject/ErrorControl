/**
 * Packet data class
 * first 4 bytes is header, 28 bytes is body
 * header:
 * byte1: Signature, value is 0x55
 * byte2: Sequence Number
 * byte3: Data Length, if packet is not data, it may be 0
 * byte4: Packet Type, data or ack or end 
 *
 *
 */
public class Packet {
	protected final static byte SIGN = 0x55;

	public final static int PACKET_SIZE = 32;
	public final static int HEADER_SIZE = 4;
	public final static int CONTENT_SIZE = PACKET_SIZE - HEADER_SIZE;
	
	public final static int GBN_WINDOW_SIZE = 3; 
	
	public final static byte TYPE_DATA = 0x4F;
	public final static byte TYPE_ACK = 0x49;
	public final static byte TYPE_END = 0x40;
	
	public byte signature;
	public byte sequence;
	public byte datalen;
	public byte type;
	public byte[] data;
	
	public String error;
	
	public Packet() {
		this.signature = SIGN;
		this.sequence = 0;
		this.datalen = 0;
		this.type = 0;
		this.data = null;
		this.error = "";
	}
	
	public static byte[] buildData(byte bType, byte bSeq, byte[] sendData, int iDataLength) {		
		try {
			byte[] packet = new byte[PACKET_SIZE];
			packet[0] = SIGN;
			packet[1] = bSeq;
			packet[2] = (byte)iDataLength;
			packet[3] = bType;
			
			if(bType == TYPE_DATA) {
				if(iDataLength < 1 || iDataLength > CONTENT_SIZE) {
					return null;
				}
				for(int i = 0; i < iDataLength; i++) {
					packet[HEADER_SIZE + i] = sendData[i];
				}
			}
			
			return packet;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public Packet copy() {
		Packet pac = new Packet();
		pac.signature = this.signature;
		pac.sequence = this.sequence;
		pac.datalen = this.datalen;
		pac.type = this.type;
		pac.data = this.data.clone();
		return pac;
	}
	public boolean parse(byte[] recv) {
		this.error = "";
		try {
			if(recv[0] != SIGN) {
				this.error = "Signature error!";
				return false;
			}
			this.sequence = recv[1];
			this.datalen = recv[2];
			this.type = recv[3];
			
			if(this.type == TYPE_DATA) {			
				if(this.datalen < 1 || this.datalen > CONTENT_SIZE) {
					this.error = "Data length must be larger than zero and smaller than " + CONTENT_SIZE + "byte!";
					return false;
				}
				this.data = new byte[this.datalen];
				for(int i = 0; i < this.datalen; i++) {
					this.data[i] = recv[HEADER_SIZE + i];
				}
			}
			return true;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
