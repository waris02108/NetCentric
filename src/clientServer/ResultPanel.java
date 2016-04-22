package clientServer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
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

import clientServer.GameUIClient;
public class ResultPanel extends JPanel {
	private int yourscore = 0;
	private int awayscore = 0;
	Image backgroundImage = null;
	ImageIcon youwinIcon;
	ImageIcon youloseIcon;
	JLabel victory = new JLabel();
	JLabel totaltime = new JLabel();
	JLabel totalLbl = new JLabel("s");
	JLabel awayScore = new JLabel("0");
	JLabel playerScore = new JLabel("0");
	public ResultPanel(){
		super();
		setGUI();
		
	}
	
	public void setGUI(){
		this.setLayout(new GridLayout(1,3));
		this.setPreferredSize(new Dimension(400,200));
		JPanel homepanel = new JPanel(new GridLayout(2,1));
		Image home = null;
		Image youwin = null;
		Image youlose = null;
		Image away = null;
		Image box = null;
		Image totaltime = null;
		
		try {
			youwin = ImageIO.read(new File("image/youwin.png"));
			youlose = ImageIO.read(new File("image/youlost.png"));
			home = ImageIO.read(new File("image/home.png"));
			away = ImageIO.read(new File("image/away.png"));
			box = ImageIO.read(new File("image/box1.gif"));
			backgroundImage = ImageIO.read(new File("image/board.gif"));
			totaltime = ImageIO.read(new File("image/totaltime.png"));
		} catch (IOException e) {
			System.out.println(e.toString());
		}
		box = box.getScaledInstance(75, 75, Image.SCALE_SMOOTH);
		totaltime = totaltime.getScaledInstance(150, 40, Image.SCALE_SMOOTH);
		home = home.getScaledInstance(150, 60, Image.SCALE_SMOOTH);
		away = away.getScaledInstance(150, 60, Image.SCALE_SMOOTH);
		youwin = youwin.getScaledInstance(150,150,Image.SCALE_SMOOTH);
		youlose = youlose.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
		youwinIcon = new ImageIcon(youwin);
		youloseIcon = new ImageIcon(youlose);
		JLabel playerBanner = new JLabel();
		playerBanner.setIcon(new ImageIcon(home));
		
		playerScore.setForeground(Color.WHITE);
		playerScore.setFont(new Font("Arial",Font.ITALIC,20));
		playerScore.setIcon(new ImageIcon(box));
		playerScore.setHorizontalTextPosition(JLabel.CENTER);
		playerScore.setHorizontalAlignment(JLabel.CENTER);
		playerBanner.setHorizontalAlignment(JLabel.CENTER);
		homepanel.add(playerBanner);
		homepanel.add(playerScore);
		homepanel.setOpaque(false);
		
		JPanel middlePanel = new JPanel(new GridLayout(2,1));
		middlePanel.add(this.victory);
		victory.setIcon(youloseIcon);		
		JPanel totalPanel = new JPanel(new GridLayout(2,1));
		totalPanel.setOpaque(false);
		JLabel total = new JLabel(new ImageIcon(totaltime));
		totalPanel.add(total);
	
		totalLbl.setForeground(Color.WHITE);
		totalLbl.setFont(new Font("Arial",Font.ITALIC,20));
		totalLbl.setIcon(new ImageIcon(box));
		totalLbl.setHorizontalTextPosition(JLabel.CENTER);
		totalLbl.setHorizontalAlignment(JLabel.CENTER);
		total.setHorizontalAlignment(JLabel.CENTER);
		totalPanel.add(total);
		totalPanel.add(totalLbl);
		middlePanel.add(totalPanel);
		middlePanel.setOpaque(false);
		
		
		
		
		
		
		
		
		JPanel awayPanel = new JPanel(new GridLayout(2,1));
		JLabel awayBanner = new JLabel();
		awayBanner.setIcon(new ImageIcon(away));
		
		awayScore.setForeground(Color.WHITE);
		awayScore.setFont(new Font("Arial",Font.ITALIC,20));
		awayScore.setIcon(new ImageIcon(box));
		
		awayScore.setHorizontalAlignment(JLabel.CENTER);
		awayScore.setHorizontalTextPosition(JLabel.CENTER);
		awayBanner.setHorizontalAlignment(JLabel.CENTER);
		awayPanel.add(awayBanner);
		awayPanel.add(awayScore);
		awayPanel.setOpaque(false);
		
		this.add(homepanel);
		this.add(middlePanel);
		this.add(awayPanel);
		this.setVisible(true);
		
	}
	protected void paintComponent(Graphics g){
		  super.paintComponent(g);
		    ((Graphics2D)g.create()).drawImage(backgroundImage, 0, 0, this);
		   
	}
	public void setVictory(boolean isWin){
		if(isWin){
			victory.setIcon(youwinIcon);
		} else {
			victory.setIcon(youloseIcon);
		}
	}
	public void settotalTime(int seconds){
		totalLbl.setText(seconds+"s");
	}
	public void setPlayerScore(int score){
		this.playerScore.setText(""+score);
	}
	public void setAwayScore(int score){
		this.awayScore.setText(""+score);
	}
	
//	public static void main(String[] args) throws IOException {
//		JFrame frame = new JFrame();
//		
//		frame.add(new ResultPanel(3));
//		// frame.pack();
//		frame.setSize(new Dimension(600, 300));
//		frame.setVisible(true);
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		
//
//	}
}
