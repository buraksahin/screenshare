package hostinterface;

import javax.swing.JFrame;

import network.NetworkManager;
import userinterface.ApplicationGUI;

public class Init {
	public static NetworkManager networkHandler = new NetworkManager();
	public Thread networkThread = new Thread(networkHandler);
	public static void main(String[] args) {
		
		ServerUI f = new ServerUI(networkHandler);
		f.show();
		networkHandler.setIsClient(false);
		networkHandler.startServer(f);
		
	}
}