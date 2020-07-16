package userinterface;

import java.awt.event.*;
import javax.swing.*;

import network.*;

public class ApplicationGUI extends JFrame implements ActionListener {
	public static NetworkManager networkHandler;
	private JLabel label = null; 
	private ImageIcon icon = null;
	public static final int PORT = 2525;
	JMenuItem menuConnect, menuDisconnect, menuExit, menuAbout;
	JMenuBar topbarMenu;
	JMenu menuFile, menuHelp;
	String message = "About us", errorMessage = "Connection already exist.";

	public ApplicationGUI(NetworkManager nm) {
		this.setBounds(0, 0, 1440, 900);
		this.setLayout(null);
		topbarMenu = new JMenuBar();
		menuFile = new JMenu("File");
		menuConnect = new JMenuItem("Connect");
		menuDisconnect = new JMenuItem("Disconnect");
		menuExit = new JMenuItem("Exit");
		menuConnect.addActionListener(this);
		menuDisconnect.addActionListener(this);
		menuExit.addActionListener(this);
		menuFile.add(menuConnect);
		menuFile.add(menuDisconnect);
		menuFile.add(menuExit);

		menuHelp = new JMenu("Help");
		menuAbout = new JMenuItem("About");
		menuHelp.add(menuAbout);
		menuAbout.addActionListener(this);

		topbarMenu.add(menuFile);
		topbarMenu.add(menuHelp);

		setJMenuBar(topbarMenu);

		setTitle("Screen Share");
		setSize(500, 500);

		icon = new ImageIcon("ScreenShot.jpg");
		
		label = new JLabel();
		label.setBounds(0, 0, 1024, 768);
		label.setIcon(icon);
		
		this.add(label);
		
		// Window Listeners
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (networkHandler != null) {
					networkHandler.destroy();
				}
				System.exit(0);
			}
		});
		networkHandler = nm;
	}

	public void actionPerformed(ActionEvent e) {
		String address = "";
		if (e.getSource() == menuConnect) {
			if (networkHandler != null) {
				address = JOptionPane.showInputDialog(null, "Server ip address", "Connect", 1);
			}
			if(address != null && address.length() > 0) {
				networkHandler.startClient(this, address);
			}
		}

		if (e.getSource() == menuDisconnect) {
			if (networkHandler != null) {
				networkHandler.destroy();
			}
		}

		if (e.getSource() == menuExit) {
			if (networkHandler != null) {
				networkHandler.destroy();
			}
			System.exit(1);

		}

		if (e.getSource() == menuAbout) {
			JOptionPane.showMessageDialog(null, message, "About", JOptionPane.CLOSED_OPTION);
		}
	}

	public NetworkManager getNetworkHandler() {
		return networkHandler;
	}

	public void setNetworkHandler(NetworkManager networkHandler) {
		this.networkHandler = networkHandler;
	}
	
	
	public void refreshImage(byte[] imageData) {
		icon = new ImageIcon(imageData);
		label.setIcon(icon);
	}
	
}
