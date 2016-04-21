package p2p;
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
public class ResultPanel extends JPanel {
	private int yourscore = 0;
	Image backgroundImage = null;
	ImageIcon youwinIcon;
	ImageIcon youloseIcon;
	JLabel victory = new JLabel();
	JLabel totaltime = new JLabel();
	JLabel totalLbl = new JLabel("s");
	public ResultPanel(int score){
		super();
		setGUI();
		yourscore = score;
	}
	public void setScore(int score){
		this.yourscore = score;
	}
	public void setGUI(){
		this.setLayout(new GridLayout(1,3));
		this.setPreferredSize(new Dimension(500,300));
		JPanel homepanel = new JPanel(new GridLayout(2,1));
		Image home = null;
		Image youwin = null;
		Image youlose = null;
		Image away = null;
		Image box = null;
		Image totaltime = null;
		
		try {
			youwin = ImageIO.read(new File("image/youwin.gif"));
			youlose = ImageIO.read(new File("image/youlose.gif"));
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
		youwin = youwin.getScaledInstance(200,200,Image.SCALE_SMOOTH);
		youlose = youlose.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
		youwinIcon = new ImageIcon(youwin);
		youloseIcon = new ImageIcon(youlose);
		JLabel playerBanner = new JLabel();
		playerBanner.setIcon(new ImageIcon(home));
		JLabel playerScore = new JLabel("0");
		playerScore.setForeground(Color.WHITE);
		playerScore.setFont(new Font("Arial",Font.ITALIC,20));
		playerScore.setIcon(new ImageIcon(box));
		playerScore.setHorizontalTextPosition(JLabel.CENTER);
		playerScore.setHorizontalAlignment(JLabel.CENTER);
		playerBanner.setHorizontalAlignment(JLabel.CENTER);
		homepanel.add(playerBanner);
		homepanel.add(playerScore);
		homepanel.setOpaque(true);
		
		JPanel middlePanel = new JPanel(new GridLayout(2,1));
		middlePanel.add(this.victory);
		JPanel totalPanel = new JPanel(new GridLayout(2,1));
		totalPanel.setOpaque(true);
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
		middlePanel.setOpaque(true);
		
		
		
		
		
		
		
		
		JPanel awayPanel = new JPanel(new GridLayout(2,1));
		JLabel awayBanner = new JLabel();
		awayBanner.setIcon(new ImageIcon(away));
		JLabel awayScore = new JLabel("0");
		awayScore.setForeground(Color.WHITE);
		awayScore.setFont(new Font("Arial",Font.ITALIC,20));
		awayScore.setIcon(new ImageIcon(box));
		awayScore.setHorizontalTextPosition(JLabel.CENTER);
		awayScore.setHorizontalAlignment(JLabel.CENTER);
		awayBanner.setHorizontalAlignment(JLabel.CENTER);
		awayPanel.setOpaque(true);
		
		this.add(homepanel);
		this.add(middlePanel);
		this.add(awayPanel);
		this.setVisible(true);
		
	}
	protected void paintComponent(Graphics g){
		  super.paintComponent(g);
		    ((Graphics2D)g.create()).drawImage(backgroundImage, 0, 0, 1024, 768, this);
		   
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
}
