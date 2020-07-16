package network;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;

import hostinterface.ServerUI;

public class Server implements Runnable{
	public static final int PORT = 2525;
	public DatagramSocket sock = null;
	double width;
	double height;
	ServerUI ui;
	List<User> users = new ArrayList<User>();
	List<Thread> threads = new ArrayList<Thread>();
	DatagramPacket packetFromUser;
    String messageFromClient;
    HashMap<String, Long> usersTime = new HashMap<String, Long>();
    Long lastCheckTime;
    boolean isRunning = true;
    
	public Server(ServerUI ui) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		width = screenSize.getHeight();
		height = screenSize.getWidth();
	}
	
	public void start() {
		try {
			sock = new DatagramSocket(PORT);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		lastCheckTime = System.currentTimeMillis();
		while(isRunning){
			Long startTime = System.currentTimeMillis();
			System.out.println("Listen port " + PORT);
			updateUserList();
			
			// Listen message from client
			// Get client name for listing
			byte[] receiveData = new byte[1024*8];
			DatagramPacket receivePacket = new DatagramPacket(receiveData,
                    receiveData.length);
			packetFromUser = new DatagramPacket(receiveData, receiveData.length);
			
			try {
				sock.receive(receivePacket);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
            String sentence = new String( receivePacket.getData(), 0,
                               receivePacket.getLength());
            if(sentence != null && sentence.length() != 0) {
            	messageFromClient = new String(packetFromUser.getData(), packetFromUser.getOffset(), packetFromUser.getLength());
 
            	InetAddress adr = packetFromUser.getAddress();
                if(messageFromClient.contains("_")){
                    String[] tmp = messageFromClient.split("_");
                    // 0 username
                    messageFromClient = tmp[0];
                    System.out.println("Connected user name:" + tmp[2]);
                    if(messageFromClient.length() > 0 && messageFromClient.equals("NEWCONNECTION")) {
                        users.add(new User(tmp[2],tmp[1], sock));
                        threads.add(new Thread(users.get(users.size() - 1)));
                        threads.get(threads.size() - 1).start();
                    }
                    if(messageFromClient.length() > 0 && messageFromClient.equals("IAMLIVE")) {
                    	usersTime.put(tmp[2], System.currentTimeMillis());
                    }
                }
            }

			if(users.size() > 0) {
				Rectangle rect = new Rectangle(0, 0, (int)width, (int)height);
				BufferedImage screenImage = null;
				try {
					screenImage = new Robot().createScreenCapture(rect);
				} catch (AWTException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ByteArrayOutputStream imageData = new ByteArrayOutputStream();
				try {
					if(screenImage != null) {
						ImageIO.write(screenImage, "jpg", imageData);					
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				byte[] rawData = imageData.toByteArray();
				
				
				List<DatagramPacket> packs = SplitPacket(rawData);
				for(User userTemp : users) {
					for(DatagramPacket pack : packs) {
						userTemp.addPacket(pack);
					}
				}
				
				if(System.currentTimeMillis() - lastCheckTime > 10000) {
					checkDisconnected();			
				}
				// Check DeltaTime synchronize frame/30ms
				while(true) {
					System.out.println(System.currentTimeMillis() - startTime);
					if(System.currentTimeMillis() - startTime < 30) {
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
			}
			
		}
	}
	
	public static byte[] gzipCompress(byte[] rawData){
		try {
			ByteArrayOutputStream imageData = new ByteArrayOutputStream();
			GZIPOutputStream gzos = new GZIPOutputStream(imageData);
			gzos.write(rawData);
			gzos.close();
			
			return imageData.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	// Split data
	public static List<DatagramPacket> SplitPacket(byte[] rawData) {
		List<DatagramPacket> packs = new ArrayList<DatagramPacket>();
		
		int length = rawData.length;
		int contentLenPerPack = 50 * 1024; // Packet size 50KB
		int totalLenPerPack = contentLenPerPack + 10;
		// Number of packets for that raw data.
		int count = 0;
		if(length % contentLenPerPack == 0) {
			count = length / contentLenPerPack;
		} else {
			count = length / contentLenPerPack + 1;
		}
		// Group ID.
		long grpID = System.nanoTime();
		
		DatagramPacket pack = null;
		// Group ID.
		byte[] grpIDBytes = Utils.long2bytes(grpID);
		byte[] packBytes = null;
		for(int idx = 0; idx < count; ++idx) {
			if(idx != (count -1)) { // Not the last one.
				packBytes = new byte[totalLenPerPack];
				System.arraycopy(grpIDBytes, 0, packBytes, 0, 8);
				// Index.
				packBytes[8] = (byte)idx;
				// Count.
				packBytes[9] = (byte)count;
				// Content.
				System.arraycopy(rawData, idx * contentLenPerPack,
						packBytes, 10, contentLenPerPack);
				
			} else { // The last one.
				int remain = rawData.length - (count - 1) * contentLenPerPack;
				packBytes = new byte[remain + 10];
				System.arraycopy(grpIDBytes, 0, packBytes, 0, 8);
				// Index.
				packBytes[8] = (byte)idx;
				// Count.
				packBytes[9] = (byte)count;
				// Content.
				System.arraycopy(rawData, idx * contentLenPerPack,
						packBytes, 10, remain);
			}
			
			try {
				// Create a packet for this split.
				pack = new DatagramPacket(packBytes, packBytes.length, InetAddress.getByName("127.0.0.1"), PORT);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			packs.add(pack); // Add to List of packet.
		}
		
		return packs;
	}
	

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
	public void updateUserList() {
		// Check timeouts and delete users
		if(users.size()>0) {
			// Update list
			String userList = "Server is running.\nTotal connected user:" + users.size() + "\n";
			for(User tempUser : users) {
				userList = userList + "\n"  + tempUser.username;
			}
			ui.messageLabel.setText(userList);
		}
	}
	
	public void close() {
		ui.messageLabel.setText("Share is stoped..");
		sock.close();
		isRunning = false;
	}
	
	public void checkDisconnected() {
		lastCheckTime = System.currentTimeMillis();
		if(users.size()>0) {
			// Update list
			String userList = "Server is running.\nTotal connected user:" + users.size() + "\n";
			for(User tempUser : users) {
				if(usersTime.get(tempUser.username) != null && System.currentTimeMillis() - usersTime.get(tempUser.username) > 10000) {
					users.remove(tempUser);
					tempUser = null;
					userList = userList + "\nUser "  + tempUser.username + " left";
				}
				else {
					userList = userList + "\n"  + tempUser.username;
				}
			}
			ui.messageLabel.setText(userList);
		}
	}
}
