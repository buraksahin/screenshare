package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;

public class User implements Runnable{
	public DatagramSocket sock = null;
	String username;
	String ipAddress;
	boolean isRunning = true;
	public static final int PORT = 8889;
	
	Queue<DatagramPacket> packQueue = new LinkedList<DatagramPacket>();
	
	public User(String name, String ipAddress,DatagramSocket sock) {
		username = name;
		this.sock = sock;
		this.ipAddress = ipAddress;
	}
	
	public void addPacket(DatagramPacket packet) {
		packQueue.add(packet);
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true) {
			while(true) {
				if(packQueue.size() == 0) {
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}									
				}
				else {
					break;
				}
			}// end of the loop
			
			System.out.println("Sending data to " + username + "(" + ipAddress + ")");
			DatagramPacket pack = packQueue.poll();
			if(pack != null) {
				InetAddress inetAddress = null;
				try {
					inetAddress = InetAddress.getByName(ipAddress);
				} catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}  
		        int iport=8889;
		        pack.setAddress(inetAddress);
		        pack.setPort(iport);
				if(sock != null) {
					try {
						sock.send(pack);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				System.out.println("Packet has been sent to " + pack.getPort());
				
			}
			
		}
	}
	
	
	
}
