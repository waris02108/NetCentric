package clientServer;

import java.net.*;
import java.util.ArrayList;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.io.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;

import org.json.JSONObject;

import p2p.ResultPanel;


public class GameUIClient extends JPanel implements Runnable {
	private BombPanel bombField[];
	private JPanel bombGrid;
	private JPanel gameHUD;
	private JLabel timerLabel;
	boolean isMusicOn = true;
	private JLabel playerName;
	private JLabel opponentName;
	private JLabel playerScore;
	private JLabel opponentScore;
	private JLabel maxMineCount;
	private JLabel mineLeft;
	static Clip[] soundClip = new Clip[4];
	static int soundIndex = 1;
	private static int seconds = 10;
	boolean myTurn;
	boolean confirmRematch;
	JSONObject exString;
	int maxMine;
	Player player;
	Player opponent;
	boolean isConnected;
	Timer turnTimer;
	boolean isOpponentNull = true;
	int mineCount = 0;
//	ObjectOutputStream out;
//	ObjectInputStream in;
	PrintWriter out;
	BufferedReader in;
	Socket con;
	Thread outputThread;
	private int totaltime = 0;
	Image bgImage = null;
	ImageIcon close;
	ImageIcon open;
	JComboBox musicList;
	JButton closeMusicBtn;
	///SERVER PART
	boolean isServer;
	ServerSocket server;
	public GameUIClient() throws IOException {

		super();
		player = new Player("");
		this.createSound();
		this.soundClip[1].loop(-1);
		isConnected = false;
		setGUI();

	}
	public void createSound() {
		
		File soundFile1 = new File("bensound-littleidea.wav");
		File soundFile2 = new File("bgm2.wav");
		File soundFile3 = new File("bensound-cute.wav");
		File soundFile4 = new File("battle.wav");
		AudioInputStream audioIn = null;
		AudioInputStream audioIn2 = null;
		AudioInputStream audioIn3 = null;
		AudioInputStream audioIn4 = null;
		try {
			audioIn = AudioSystem.getAudioInputStream(soundFile1);
			soundClip[0] = AudioSystem.getClip();
			soundClip[0].open(audioIn);
			
			audioIn2 = AudioSystem.getAudioInputStream(soundFile2);
			soundClip[1] = AudioSystem.getClip();
			soundClip[1].open(audioIn2);
			
			audioIn3 = AudioSystem.getAudioInputStream(soundFile3);
			soundClip[2] = AudioSystem.getClip();
			soundClip[2].open(audioIn3);
			
			audioIn4 = AudioSystem.getAudioInputStream(soundFile4);
			soundClip[3] = AudioSystem.getClip();
			soundClip[3].open(audioIn4);
			
			
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public static void turnOnMusic(){
		soundClip[soundIndex].loop(-1);
	}
	public static void turnOffMusic(){
		soundClip[soundIndex].stop();
		
	}
	public void start() {
		try {
			con = new Socket("127.0.0.1", 1256);
			in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			out = new PrintWriter(con.getOutputStream(), true);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Error during initial connection");
			System.exit(1);
		}
		isConnected = true;
		outputThread = new Thread(this);
		outputThread.start();

	}

	private void resetScore() {
		this.mineCount = 0;
		player.setScore(0);
		playerScore.setText("Score:0");
		opponent.setScore(0);
		opponentScore.setText("Score:0");
	}

	private void setGUI() {

		this.setLayout(new BorderLayout());
		createBombGrid();
		add(bombGrid, BorderLayout.CENTER);
		createGameHUD();
		gameHUD.setPreferredSize(new Dimension(400, 1000));
		add(gameHUD, BorderLayout.EAST);

		this.setVisible(true);
	}
	public static void insertBGM(String sound) {
		File soundFile = new File(sound);
		AudioInputStream audioIn4 = null;
		try {
			audioIn4 = AudioSystem.getAudioInputStream(soundFile);
			Clip clip2 = AudioSystem.getClip();
			clip2.open(audioIn4);
			clip2.start();
				
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}
	private void createGameHUD() {
		gameHUD = new JPanel(){
			 @Override
			   public void paintComponent(Graphics g) {
			    super.paintComponent(g);
			   
			    	setBackground(Color.black);
			   }
			   
			 
		};	
		gameHUD.setLayout(new GridLayout(5,1));
		JPanel profile = new JPanel();
		profile.setLayout(new GridLayout(1,2));
		Image home = null;
		Image scoreboard = null;
		
		Image box = null;
		Image score = null;	
		Image away = null;
		Image totalmine = null;
		Image mineleft = null;
		Image timerB = null;
		Image secondsLeft = null;
		ImageIcon clock = null;
		Image closemusic = null;
		Image openmusic = null;
		try {
			closemusic = ImageIO.read(new File("image/musicoff.gif"));
			openmusic = ImageIO.read(new File("image/musicon.gif"));
			home = ImageIO.read(new File("image/home.png"));
			away = ImageIO.read(new File("image/away.png"));
			scoreboard = ImageIO.read(new File("image/scoreboard.png"));
			score = ImageIO.read(new File("image/score.gif"));
			box = ImageIO.read(new File("image/box1.gif"));
			totalmine = ImageIO.read(new File("image/totalmine.gif"));
			mineleft = ImageIO.read(new File("image/mineleft.gif"));
			timerB = ImageIO.read(new File("image/timer.gif"));
			secondsLeft = ImageIO.read(new File("image/secondsleft.gif"));
			clock = new ImageIcon(new URL("file:/C:/Users/Waris/workspace/Netcentric/image/clock.gif"));
		} catch (IOException e) {
			System.out.println(e.toString());
		} 
		
		JLabel playerBanner = new JLabel();
		//clock = timerB.getScaledInstance(100, , arg2)
		closemusic = closemusic.getScaledInstance(75, 75, Image.SCALE_SMOOTH);
		openmusic = openmusic.getScaledInstance(75, 75, Image.SCALE_SMOOTH);
		timerB = timerB.getScaledInstance(150, 60, Image.SCALE_SMOOTH);
		totalmine = totalmine.getScaledInstance(150, 60, Image.SCALE_SMOOTH);
		mineleft = mineleft.getScaledInstance(150, 60,Image.SCALE_SMOOTH);
		home = home.getScaledInstance(150, 60, Image.SCALE_SMOOTH);
		box = box.getScaledInstance(75, 75, Image.SCALE_SMOOTH);
		secondsLeft = secondsLeft.getScaledInstance(150, 75, Image.SCALE_SMOOTH);
		playerBanner.setIcon(new ImageIcon(home));
		JPanel playerNamePanel = new JPanel(new BorderLayout());
		
		playerName = new JLabel("Player1");
		playerName.setFont(new Font("Arial",Font.ITALIC,20));
		playerName.setForeground(Color.WHITE);
		playerName.setIcon(new ImageIcon(box));
		playerName.setHorizontalTextPosition(JLabel.CENTER);
		playerNamePanel.add(playerBanner,BorderLayout.NORTH);
		playerNamePanel.add(playerName,BorderLayout.CENTER);
		
		playerName.setHorizontalAlignment(JLabel.CENTER);
		playerBanner.setHorizontalAlignment(JLabel.CENTER);
		
		
		
		
		JLabel scoreBanner = new JLabel();
		
		score = score.getScaledInstance(150, 60, Image.SCALE_SMOOTH);
		playerScore = new JLabel("0");
		playerScore.setForeground(Color.WHITE);
		playerScore.setFont(new Font("Arial",Font.ITALIC,20));
		playerScore.setIcon(new ImageIcon(box));
		playerScore.setHorizontalTextPosition(JLabel.CENTER);
		scoreBanner.setIcon(new ImageIcon(score));
		JPanel scorePanelPlayer = new JPanel(new BorderLayout());
		scorePanelPlayer.add(scoreBanner,BorderLayout.NORTH);
		scorePanelPlayer.add(playerScore,BorderLayout.CENTER);
		
		scoreBanner.setHorizontalAlignment(JLabel.CENTER);
		playerScore.setHorizontalAlignment(JLabel.CENTER);
		scorePanelPlayer.setOpaque(false);
	
		
		
		
		

		
		JLabel opponentBanner = new JLabel();
		
		away = away.getScaledInstance(150, 60, Image.SCALE_SMOOTH);
		opponentBanner.setIcon(new ImageIcon(away));
		JPanel opponentNamePanel = new JPanel(new BorderLayout());
		opponentName = new JLabel("Player2");
		opponentName.setForeground(Color.WHITE);
		opponentName.setFont(new Font("Arial",Font.ITALIC,20));
		opponentName.setIcon(new ImageIcon(box));
		opponentName.setHorizontalTextPosition(JLabel.CENTER);
		opponentNamePanel.add(opponentBanner,BorderLayout.NORTH);
		opponentNamePanel.add(opponentName,BorderLayout.CENTER);
		opponentName.setHorizontalAlignment(JLabel.CENTER);
		opponentBanner.setHorizontalAlignment(JLabel.CENTER);
		
		opponentScore = new JLabel("0");
		opponentScore.setForeground(Color.WHITE);
		opponentScore.setFont(new Font("Arial",Font.ITALIC,20));
		opponentScore.setPreferredSize(new Dimension(75,75));
		opponentScore.setIcon(new ImageIcon(box));
		opponentScore.setHorizontalTextPosition(JLabel.CENTER);
		JLabel scoreBanner2 = new JLabel(new ImageIcon(score));
		JPanel opponentScorePanel = new JPanel(new BorderLayout());
		opponentScorePanel.add(scoreBanner2,BorderLayout.NORTH);
		opponentScorePanel.add(opponentScore,BorderLayout.CENTER);
		
		scoreBanner2.setHorizontalAlignment(JLabel.CENTER);
		opponentScore.setHorizontalAlignment(JLabel.CENTER);
		opponentScorePanel.setOpaque(false);
		profile.setOpaque(false);
		profile.add(playerNamePanel);
		profile.add(opponentNamePanel);
		playerNamePanel.setOpaque(false);
		opponentNamePanel.setOpaque(false);
		gameHUD.add(profile);
		
		
		//JPanel scorePanel = new JPanel(new BorderLayout());
		
		scoreboard = scoreboard.getScaledInstance(300, 75, Image.SCALE_SMOOTH);
		
		
		//scorePanel.add(scoreboardBanner,BorderLayout.NORTH);
		JPanel scorePlayerOpponent = new JPanel(new GridLayout(1,2));
		scorePlayerOpponent.add(scorePanelPlayer);
		scorePlayerOpponent.add(opponentScorePanel);
		//scorePanel.add(scorePlayerOpponent,BorderLayout.CENTER);
		scorePlayerOpponent.setOpaque(false);
		gameHUD.add(scorePlayerOpponent);
		//gameHUD.add(scorePanel);
		
		
		
		
		//Mine count + Maximum Mine
		JPanel minePanel = new JPanel(new GridLayout(1,2));
		JLabel totalMinelbl = new JLabel(new ImageIcon(totalmine));
		totalMinelbl.setHorizontalAlignment(JLabel.CENTER);
		JLabel mineLeftlbl = new JLabel(new ImageIcon(mineleft));
		mineLeftlbl.setHorizontalAlignment(JLabel.CENTER);
		
		JPanel totalMinePanel = new JPanel(new GridLayout(2,1));
		JPanel mineLeftPanel = new JPanel(new GridLayout(2,1));
		mineLeft = new JLabel((this.maxMine-this.mineCount)+"");
		mineLeft.setFont(new Font("Arial",Font.ITALIC,20));
		mineLeft.setForeground(Color.WHITE);
		maxMineCount = new JLabel(this.maxMine+"");
		maxMineCount.setFont(new Font("Arial",Font.ITALIC,20));
		maxMineCount.setForeground(Color.WHITE);
		mineLeft.setIcon(new ImageIcon(box));
		maxMineCount.setIcon(new ImageIcon(box));
		this.mineLeft.setHorizontalTextPosition(JLabel.CENTER);
		this.mineLeft.setHorizontalAlignment(JLabel.CENTER);
		this.maxMineCount.setHorizontalTextPosition(JLabel.CENTER);
		this.maxMineCount.setHorizontalAlignment(JLabel.CENTER);
		totalMinePanel.add(totalMinelbl);
		totalMinePanel.add(maxMineCount);
		mineLeftPanel.add(mineLeftlbl);
		mineLeftPanel.add(mineLeft);
		totalMinePanel.setOpaque(false);
		mineLeftPanel.setOpaque(false);
		minePanel.add(totalMinePanel);
		minePanel.add(mineLeftPanel);
		
		
		
		
		//TIMER LABEL
		JPanel timerPanel = new JPanel();
		JPanel timerTitlePanel = new JPanel(new GridLayout(2,1));
		timerTitlePanel.setOpaque(false);
		JLabel timerTitle = new JLabel();
		JLabel timerGif = new JLabel(clock);
		timerGif.setHorizontalAlignment(JLabel.CENTER);
		timerTitle.setIcon(new ImageIcon(timerB));
		timerTitle.setHorizontalAlignment(JLabel.CENTER);
		JPanel secondPanel = new JPanel(new GridLayout(2,1));
		JLabel secondBanner = new JLabel(new ImageIcon(secondsLeft));
		timerLabel = new JLabel(new ImageIcon(box));
		timerLabel.setHorizontalAlignment(JLabel.CENTER);
		timerLabel.setHorizontalTextPosition(JLabel.CENTER);
		timerLabel.setText("10");
		timerLabel.setFont(new Font("Arial",Font.ITALIC,20));
		timerLabel.setForeground(Color.WHITE);
		secondPanel.add(secondBanner);
		secondPanel.add(timerLabel);
		secondPanel.setOpaque(false);
		timerPanel.setLayout(new GridLayout(1,2));
		timerPanel.add(timerTitlePanel);
		timerPanel.add(secondPanel);
		timerPanel.setOpaque(false);
		minePanel.setOpaque(false);
		timerTitlePanel.add(timerTitle);
		timerTitlePanel.add(timerGif);
		gameHUD.add(timerPanel);
		gameHUD.add(minePanel);
		createTimer();
		
		
		
		closeMusicBtn = new JButton();
		close = new ImageIcon(closemusic);
		open = new ImageIcon(openmusic);
		closeMusicBtn.setIcon(close);
		closeMusicBtn.setPreferredSize(new Dimension(75,75));
		closeMusicBtn.setOpaque(false);
		closeMusicBtn.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if(isMusicOn){
					isMusicOn = false;
					closeMusicBtn.setIcon(open);
					insertBGM("clickS.wav");
					turnOffMusic();
					
				}else {
					isMusicOn = true;
					closeMusicBtn.setIcon(close);
					insertBGM("clickS.wav");
					turnOnMusic();
				}
			}
			
		});
		JPanel musicPanel = new JPanel(new GridLayout(1,2));
		musicPanel.setOpaque(false);
		musicPanel.add(closeMusicBtn);
		
		String[] musicIndex = { "BGM1", "BGM2", "BGM3"};
		musicList = new JComboBox(musicIndex);
		musicList.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				String selected = (String)musicList.getSelectedItem();
				switch(selected){
				case "BGM1":
					turnOffMusic();
					soundIndex = 1;
					turnOnMusic();
					isMusicOn = true;
					closeMusicBtn.setIcon(close);
					break;
				case "BGM2":
					turnOffMusic();
					soundIndex = 2;
					turnOnMusic();
					isMusicOn = true;
					closeMusicBtn.setIcon(close);
					break;
				case "BGM3":
					turnOffMusic();
					soundIndex = 3;
					turnOnMusic();
					isMusicOn = true;
					closeMusicBtn.setIcon(close);
					break;
				}
			}
			
		});
		musicPanel.add(musicList);
		musicPanel.setPreferredSize(new Dimension(150,150));
		gameHUD.add(musicPanel);
			
	}

	public void tempPromptName() {
		String name = JOptionPane.showInputDialog("Please Input your name");
		this.player = new Player(name);
		this.playerName.setText(this.player.getName());
		JOptionPane.showMessageDialog(this, "Welcome " + this.player.getName(),
				"Welcome", JOptionPane.INFORMATION_MESSAGE);
		// out.println("NAME:"+this.player.getName());
	}

	private void createNewGridPanel() {
		bombGrid = new JPanel();
		bombGrid.setLayout(new GridLayout(6, 6));
	}

	private void createTimer() {
		// GameUIClient.seconds = 1;
		ActionListener timerAct = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				timerLabel.setText("Time Left:" + seconds + "s");

				if (seconds < 0) {
					// turnTimer.stop();
					seconds = 10;
					// startTimer();
					// opponent turn
					myTurn = false;
					setFieldTurn();
					out.println("NextTurn");
				}
				seconds--;
			}

		};
		turnTimer = new Timer(1000, timerAct);
		// if(myTurn)turnTimer.start();
	}

	private void createBombGrid() {
		createNewGridPanel();
		resetBombGrid(11);
		for (BombPanel panel : bombField) {
			bombGrid.add(panel);
		}
		repaint();
	}

	private void resetBombGrid(int mine) {
		int count = 0;
		maxMine = mine;
		bombField = new BombPanel[36];
		for (int i = 0; i < 36; i++) {
			bombField[i] = new BombPanel();
			bombField[i].setButtonListener(new BombListener(this, i));
			// bombField[i].addBombListener();
			if (count == mine) {
				bombField[i].setBomb(false);
				continue;
			}
			if (bombField[i].checkBomb())
				count++;
		}
		if (count < mine) {
			resetBombGrid(mine);
		}
		if (isConnected)
			this.sendSameBombGrid();

	}

	private void setBombGrid(BombPanel grid[]) {
		createNewGridPanel();
		for (int i = 0; i < this.bombField.length; i++) {
			// bombField[i] = grid[i];

			bombGrid.add(bombField[i]);
		}
		this.add(bombGrid, BorderLayout.CENTER);
		repaint();
	}

	public void processBombGrid(int index) {

		myTurn = true;
		setFieldTurn();
		this.bombField[index].clickButton();
		this.computeScore(index, false);

	}

	public void sendCurrentBombGrid(int index) {
		// if(myTurn){
		this.myTurn = false;

		setFieldTurn();

		out.println(index);
		// System.out.println(index);

		// }
	}

	public void computeScore(int panel, boolean isPlayer) {
		if (bombField[panel].checkBomb()) {
			if (isPlayer) {
				this.player.addScore();
				this.playerScore.setText("" + player.getScore());
				this.mineCount++;
			} else {
				this.opponent.addScore();
				this.opponentScore.setText("" + this.opponent.getScore());
				this.mineCount++;
			}
		}
		this.checkScore();
	}

	private void checkScore() {
		ResultPanel result = new ResultPanel();
		if(this.mineCount >= this.maxMine){
			out.println("END");
			this.turnTimer.stop();
			result.setPlayerScore(this.player.getScore());
			result.setAwayScore(this.opponent.getScore());
			result.settotalTime(this.totaltime);
			Object options[] = {"Quit", "Rematch"};
			if(this.player.getScore()>this.opponent.getScore()){
				result.setVictory(true);
				Object selected = JOptionPane.showInputDialog(this,result,"You Win!!!",
						JOptionPane.PLAIN_MESSAGE,null,options,options[0]);
				if(selected.equals(options[0])){
					System.exit(0);
				} else {
					
					this.confirmRematch = true;
					out.println("Rematch");
				}
			} else if(this.player.getScore() == this.opponent.getScore() ){
				result.setVictory(false);
				Object selected = JOptionPane.showInputDialog(this,result,"It a Tie!!!",
						JOptionPane.PLAIN_MESSAGE,null,options,options[0]);
				if(selected.equals(options[0])){
					System.exit(0);
				} else {
				
					this.confirmRematch = true;
					out.println("Rematch");
				}
			}
			
			
			else {
				result.setVictory(false);
				Object selected = JOptionPane.showInputDialog(this,result,"You Lose!!!",
						JOptionPane.PLAIN_MESSAGE,null,options,options[0]);
				if(selected.equals(options[0])){
					out.println("Quit");
					System.exit(0);
				} else {
					this.confirmRematch = true;
					out.println("Rematch");
					//this.createBombGrid();
				}
			} 
		}
	}

	private void sendSameBombGrid() {
		String bombIndex = "F";
		for (int i = 0; i < this.bombField.length; i++) {
			if (bombField[i].checkBomb()) {
				bombIndex = bombIndex + i + " ";
			}
		}
		out.println(bombIndex);
	}

	private void setReceiveField(String indexString) {
		// TODO Auto-generated method stub
		bombGrid.setVisible(false);
		ArrayList<Integer> bomb = new ArrayList<Integer>();
		String temp = indexString.substring(1, indexString.length());
		for (int i = 0; i < 11; i++) {
			if (temp.indexOf(" ") == -1)
				break;
			bomb.add(Integer.parseInt(temp.substring(0, temp.indexOf(" "))));
			temp = temp.substring(temp.indexOf(" ") + 1);
		}
		for(int i =0;i<this.bombField.length;i++){
			bombField[i] = new BombPanel();
			bombField[i].setButtonListener(new BombListener(this,i));
			bombField[i].setBomb(false);
		}
		for (int i = 0; i < bomb.size(); i++) {
			bombField[bomb.get(i)].setBomb(true);
		}
		this.setBombGrid(this.bombField);
	}

	public void setFieldTurn() {
		if (myTurn) {
			// this.opponentName.setText("Your Turn");
			this.turnTimer.restart();
			for (int i = 0; i < this.bombField.length; i++) {
				bombField[i].setButtonEnable();
			}
		} else {
			// this.opponentName.setText("Your Opponent Turn");
			this.timerLabel.setText("Wait for your opponent");
			this.turnTimer.stop();
			GameUIClient.seconds = 10;
			for (int i = 0; i < this.bombField.length; i++) {
				bombField[i].setButtonDisable();
			}
		}
		repaint();
	}

	public void run() {
		// TODO Auto-generated method stub
		boolean stillOn = true;
		while (stillOn) {
			try {
				String indexString = in.readLine();
				System.out.println("In run: " + indexString);
				if (indexString.equals("Reset")) {
					turnTimer.stop();
					seconds = 10;
					this.resetScore();
					out.println("#FinishReset");
				} else if (indexString.equals("ResetCommandFromServer")) {
					if(myTurn){
						bombGrid.setVisible(false);
						createBombGrid();
						out.println("Reset");
						add(bombGrid,BorderLayout.CENTER);
						bombGrid.setVisible(true);
						resetScore();
					}
				} else if (indexString.equals("ScoreCommandFromServer")){
					out.println("Score"+this.player.getName()+": "+this.player.getScore());
				} else if (indexString.equals("Start")) {
					this.isOpponentNull = true;
					sendSameBombGrid();
				} else if (indexString.startsWith("Waiting")) {
					//System.out.println("Wait for grid");
				} else if (indexString.startsWith("F")) {
					setReceiveField(indexString);
					this.isOpponentNull = true;
					if (isOpponentNull)
						this.sendPlayerName();
					this.isOpponentNull = false;
					this.setFieldTurn();
				} else if (indexString.equals("TimeYourTurn")) {
					this.myTurn = true;
					this.setFieldTurn();
				} else if (indexString.startsWith("Opponent")) {
					this.opponent = new Player(
							indexString.substring(indexString
									.indexOf("Opponent") + 8));
					this.opponentName.setText(""
							+ this.opponent.getName());
					if (isOpponentNull)
						this.sendPlayerName();
					this.isOpponentNull = false;
				} else if (indexString.startsWith("T")) {
					String testTurn = indexString.substring(1);
					if (testTurn.equals("First")) {
						this.myTurn = true;
						this.setFieldTurn();
					} else {
						this.myTurn = false;
						this.setFieldTurn();
					}
				} else if (indexString.equals("END")) {
					this.checkScore();
				} else if (indexString.equals("Quit")) {
					JOptionPane.showMessageDialog(this, "Your Opponent Quit");
					System.exit(0);
				} else if (indexString.equals("Rematch")) {
					if (confirmRematch) {
						this.createBombGrid();
						this.bombGrid.setVisible(true);
						this.sendSameBombGrid();
					} else {
						JOptionPane.showMessageDialog(this,
								"Your Opponent Quit");
						System.exit(0);
					}
				} else {
					int index = Integer.parseInt(indexString);
					processBombGrid(index);
				}
			} catch (IOException e) {
				System.err
						.println("Problem with Communication Server in Client");
				stillOn = false;
			}
		}
	}

	private void sendPlayerName() {
		out.println("NAME:" + this.player.getName());
	}

	public static void main(String[] args) throws IOException {
		JFrame frame = new JFrame();
		GameUIClient s = new GameUIClient();
		frame.add(s);
		frame.setSize(new Dimension(1000, 800));
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		s.tempPromptName();
		s.start();
	}
}
