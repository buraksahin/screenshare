package userinterface;

import javax.swing.JFrame;

import network.NetworkManager;

public class Init {
	public static NetworkManager networkHandler = new NetworkManager();
	public Thread networkThread = new Thread(networkHandler);
	public static void main(String[] args) {
		
		ApplicationGUI f = new ApplicationGUI(networkHandler);
		f.show();
		networkHandler.setIsClient(true);

		
	}
}
