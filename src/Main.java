import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;


public class Main {
	static JFrame mainframe = new JFrame();
	boolean isSever;
	Thread thread;
	static Player player1;
	static Player player2;
	MyMineWelcome welcome;
	GameState currentState;
	public static enum GameState {
		WELCOME,
		SELECT_UI,
		GAME_LOBBY,
		GAME_PLAYING,
		END_GAME
	}
	public void init(){
		//createSound();
		//fade = new fade(); *****
	}
	public void update(GameState state){
		currentState = state;
		
	}
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
//		final JButton button = new JButton(new ImageIcon(ImageIO.read(new File("question.gif"))));
//		final JButton button2 = new JButton(new ImageIcon(ImageIO.read(new File("question.gif"))));
//		JFrame main = new JFrame();
//		main.setLayout(new GridLayout(1,2));
//		button.setPreferredSize(new Dimension(300,300));
//		button.addActionListener(new ActionListener(){
//
//			@Override
//			public void actionPerformed(ActionEvent arg0) {
//				// TODO Auto-generated method stub
//				button.setEnabled(false);
//			}
//			
//		});
//		button2.setPreferredSize(new Dimension(300,300));
//		String ez = "OpponentKWAINGOP";
//		System.out.println(ez.substring(ez.indexOf("Opponent")+8));
//		main.add(button);10.202.241.190
//		main.add(button2);
//		button2.setEnabled(false);
//		main.setSize(600,600);
//		main.setVisible(true);
		InetAddress IP=InetAddress.getLocalHost();
		System.out.println(IP.toString());
		System.out.println(IP.getHostAddress());
	}

}
