package hostinterface;

import java.awt.event.*;
import javax.swing.*;

import network.*;

public class ServerUI extends JFrame implements ActionListener {
	public static NetworkManager networkHandler;

	public static final int PORT = 2525;
	JMenuItem menuList, menuClose, menuExit, menuAbout;
	JMenuBar topbarMenu;
	JMenu menuFile, menuHelp;
	String message = "Network app", errorMessage = "Connection already exist.";
	public static JTextArea messageLabel;

	public ServerUI(NetworkManager nm) {

		
		topbarMenu = new JMenuBar();
		menuFile = new JMenu("File");
		//menuList = new JMenuItem("List"); // Added Realtime listing
		menuClose = new JMenuItem("Close");
		menuExit = new JMenuItem("Exit");
		//menuList.addActionListener(this);
		menuClose.addActionListener(this);
		menuExit.addActionListener(this);
		//menuFile.add(menuList);
		menuFile.add(menuClose);
		menuFile.add(menuExit);

		menuHelp = new JMenu("Help");
		menuAbout = new JMenuItem("About");
		menuHelp.add(menuAbout);
		menuAbout.addActionListener(this);

		topbarMenu.add(menuFile);
		topbarMenu.add(menuHelp);

		setJMenuBar(topbarMenu);

		
		messageLabel = new JTextArea();
		 // create a label to display text 
		messageLabel.setWrapStyleWord(true);
		messageLabel.setLineWrap(true);
		messageLabel.setEditable(false);
  
        // add text to label 
        messageLabel.setText("SERVER STARTED..\nTOTAL USER:0");
        
        messageLabel.setSize(this.getSize().width, this.getSize().height);

		this.add(messageLabel);
		setTitle("Screen Share");
		
		setSize(400, 500);

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
		if (e.getSource() == menuList) {
			
		}

		if (e.getSource() == menuClose) {
			if (networkHandler != null) {
				networkHandler.close();
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
	
	
}
