package network;

import java.net.DatagramPacket;

public class Packet {
	private long grpID;
	private int index;
	private int totalCount;
	private byte[] content;
	public long getGrpID() {
		return grpID;
	}
	public int getIndex() {
		return index;
	}
	public int getTotalCount() {
		return totalCount;
	}
	public byte[] getContent() {
		return content;
	}


	private int validPackLength;
	private int contentLength;
	public int getValidPackLength() {
		return validPackLength;
	}
	public int getContentLength() {
		return contentLength;
	}
	
	/**
	 * Parse a packet into properties. 
	 */
	public Packet(DatagramPacket pack) {
		byte[] packBytes = pack.getData();
		validPackLength = pack.getLength();
		contentLength = validPackLength - 10;
		
		grpID = Utils.bytes2long(packBytes);
		index = packBytes[8];
		totalCount = packBytes[9];
		content = new byte[contentLength];
		System.arraycopy(packBytes, 10, content, 0, contentLength);
	}
	
}