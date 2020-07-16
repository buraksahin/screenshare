package network;

import hostinterface.ServerUI;
import userinterface.ApplicationGUI;

public class NetworkManager implements Runnable{
	boolean isClient = false;
	Server server;
	Thread servetThread;
	Client client;
	Thread clientThreads;	
	String ipNumber;
	// Start server
	public boolean startServer(ServerUI ui) {
		if(!isClient) {
			System.out.println("SERVER STARTED..");
			server = new Server(ui);
			servetThread = new Thread(server);
			server.start();
		}
		return true;
	}
	
	@SuppressWarnings("deprecation")
	public void startClient(ApplicationGUI ui, String ipNumber) {
		this.ipNumber = ipNumber;
		if(client != null) {
			client.close();
			client.tryAgain(ipNumber);
		}
		else {
			client = new Client(ui, ipNumber);
			clientThreads = new Thread(client);
			client.start();
		}
		System.out.println("TRY TO CONNECT..");
		
	}
	
	
	// Connect
	public boolean connect() {
		return true;
	}
	
	
	// Close connection
	public boolean close() {
		if(client != null) {
			client.close();
		}
		if(server != null) {
			server.close();
		}
		return true;
	}
	
	
	// Disconnect
	public boolean disconnect() {
		return true;
	}
	
	
	// Destroy
	public boolean destroy() {
		return true;
	}
	
	public void setIsClient(boolean isClient) {
		this.isClient = isClient;
	}


	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
}
