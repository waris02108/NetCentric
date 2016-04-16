package p2p;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class MyMineWelcome extends JPanel {

	Socket con = null;
	PrintWriter out = null;
	BufferedReader in = null;
	
	JTextArea textView = new JTextArea();
	JScrollPane scrolledTextView = new JScrollPane(textView);
	int id = 0;

	
	JPanel panel;
	JButton clientButton;
	JButton serverButton;
	public MyMineWelcome() throws IOException{
		super();
		setGUI();
	}
	
	public void setGUI() {
		setLayout(new BorderLayout());
		Image mine = null;
		try {
			mine = ImageIO.read(new File("logo.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Image newImg = mine.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
		ImageIcon min = new ImageIcon(newImg);
		JLabel mineLogo = new JLabel(min);
		
		
	
		JLabel titleBanner = new JLabel("Welcome to FindMyMine!!");
		JPanel title = new JPanel();
		
		//title.setLayout(new GridLayout(2,1));
		title.add(titleBanner);
		title.add(mineLogo);
		
		
		add(title,BorderLayout.NORTH);
		panel = new JPanel(new GridLayout(1,2));
		clientButton = new JButton("Join Server");
		clientButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				JPanel inputPanel = new JPanel();
				JTextField inputAddr = new JTextField();
				
				JTextField inputPort = new JTextField();
				JLabel addrLabel = new JLabel("Address");
				JLabel portLabel = new JLabel("Port");
				
				
				inputPanel.setLayout(new GridLayout(2,2));
				inputPanel.add(addrLabel);
				inputPanel.add(inputAddr);
				inputPanel.add(portLabel);
				inputPanel.add(inputPort);
				
				int results = JOptionPane.showConfirmDialog(null, inputPanel,"Please Enter IP Address and Port", JOptionPane.OK_CANCEL_OPTION);
				while (true) {
					if (results == JOptionPane.OK_OPTION) {
						if (!inputAddr.getText().equals("")
								&& !inputPort.getText().equals("")) {
							System.out.println("Address: " + inputAddr.getText());
							System.out.println("Port: " + inputPort.getText());
							Main.ip = inputAddr.getText();
							Main.port = inputPort.getText();
							Main.currentState = Main.GameState.GAME_PLAYING_CLIENT;
							break;
						} else {
							results = JOptionPane.showConfirmDialog(null, inputPanel,
									"Please Input all the fields",
									JOptionPane.OK_CANCEL_OPTION);
						}

					} else {
						break;
					}
				}
				if(inputAddr.getText().equals("") && inputPort.getText().equals("") ){
					///// Connect to this server, check whether connect successful or not
					///// if success => pass info and CHANGE STATE TO PLAYERCONFIGUI// GAME IF NOT (CHANGE TO SAME STATE)
					/////CHANGE STATE,
					 
				}
				
			}
			
			
		});
		
		
		
		serverButton = new JButton("Create Room");
		serverButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				Main.currentState = Main.GameState.GAME_PLAYING_SERVER;
			}
			
		});
		panel.add(clientButton);
		panel.add(serverButton);
		add(panel,BorderLayout.CENTER);
		//add(scrolledTextView,BorderLayout.CENTER);
		textView.setEditable(false);
		textView.setForeground(Color.CYAN);
		textView.setBackground(Color.BLACK);
		textView.setFont(new Font("Arirl",Font.ITALIC,20));
		this.setVisible(true);
	}
	
	
	
	
//	public static void createAndRunChatClient(){
//		MyMineWelcome mainFrame = new MyMineWelcome(FRAME_TITLE);
//		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		mainFrame.setPreferredSize(new Dimension(400,400));
//        mainFrame.pack();
//        mainFrame.setVisible(true);
//	}
//	
//	public static void main(String[] args) {
//		javax.swing.SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
//                createAndRunChatClient();
//            }
//        });
//	}
//	public static void main(String[] args){
//		MyMineWelcome main = new MyMineWelcome();
//		JFrame mainFrame = new JFrame();
//		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		mainFrame.setPreferredSize(new Dimension(400,400));
//		mainFrame.add(main);
//		mainFrame.pack();
//		mainFrame.setVisible(true);
//	}
	
	
}
