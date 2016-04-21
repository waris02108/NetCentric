package p2p;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.io.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.json.JSONObject;


public class GameUIClient extends JPanel implements Runnable {
	private BombPanel bombField[];
	private JPanel bombGrid;
	private JPanel gameHUD;
	private JLabel timerLabel;
	
	private JLabel playerName;
	private JLabel opponentName;
	private JLabel playerScore;
	private JLabel opponentScore;
	private JLabel maxMineCount;
	private JLabel mineLeft;
	
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
	///SERVER PART
	boolean isServer;
	ServerSocket server;
	public GameUIClient() throws IOException{
		
		super();
		bgImage = ImageIO.read(new File("image/board.gif"));
		//player = new Player("Por");
		repaint();
		isConnected = false;
		setGUI();
		
		
	}
	public GameUIClient(boolean isServer) throws IOException {
		super();
		
		isConnected = false;
		this.isServer = isServer;
		setGUI();
	}
	
	public void setServer(boolean isServer){
		this.isServer = isServer;
	}
	
	public void start(){
		if(isServer){
			//SERVER
			try {
				this.server = new ServerSocket(1256);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			while(true){
				System.out.println("Waiting for a Client ...");
				try {
					con = server.accept();
					in = new BufferedReader(new InputStreamReader(
							con.getInputStream()));
					out = new PrintWriter(con.getOutputStream(),true);
					break;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			//client
			try {
				con = new Socket("127.0.0.1", 1256);
				//con = new Socket(Main.ip, Integer.parseInt(Main.port));
				in = new BufferedReader(new InputStreamReader(
						con.getInputStream()));
				out = new PrintWriter(con.getOutputStream(), true);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.err.println("Error during initial connection");
				Main.currentState = Main.GameState.WELCOME;
			}
		}
		isConnected = true;
		outputThread = new Thread(this);
		outputThread.start();
		if(isServer) {
			this.isOpponentNull = true;
			out.println("MaxMine:"+this.maxMine);
			this.sendSameBombGrid();
			this.randomTurn();
			
		}
	
	}
	public void randomTurn(){
		int random = (int)(Math.random()*1.99999);
		if(random == 0){
			// you go 2nd
			out.println("TFirst");
			this.myTurn = false;
			
			// opponent 1st
			
		} else {
			//you go first
			out.println("TSecond");
			this.myTurn = true;
		
		}
		this.setFieldTurn();
	}
	private void setGUI(){
		this.setPreferredSize(new Dimension(1000,800));
		this.setLayout(new BorderLayout());
		if(isServer)this.promptMineAmount();
		else this.maxMine = 11;
		createBombGrid();
		add(bombGrid,BorderLayout.CENTER);
		JButton reset =new JButton("RESET");
		reset.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				
				bombGrid.setVisible(false);
				createBombGrid();
				out.println("Reset");
				add(bombGrid,BorderLayout.CENTER);
				bombGrid.setVisible(true);
				resetScore();
				
				//sendSameBombGrid();
				
				//repaint();
				
			}	
			
		});
		add(reset,BorderLayout.SOUTH);
		createGameHUD();
		gameHUD.setPreferredSize(new Dimension(400,1000));
		add(gameHUD,BorderLayout.EAST);		
		this.setVisible(true);
	}
	
	private void createGameHUD(){
		
		
		
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
		try {
			home = ImageIO.read(new File("image/home.png"));
			away = ImageIO.read(new File("image/away.png"));
			scoreboard = ImageIO.read(new File("image/scoreboard.png"));
			score = ImageIO.read(new File("image/score.gif"));
			box = ImageIO.read(new File("image/box1.gif"));
			totalmine = ImageIO.read(new File("image/totalmine.gif"));
			mineleft = ImageIO.read(new File("image/mineleft.gif"));
			timerB = ImageIO.read(new File("image/timer.gif"));
			secondsLeft = ImageIO.read(new File("image/secondsleft.gif"));
		} catch (IOException e) {
			System.out.println(e.toString());
		}
		
		JLabel playerBanner = new JLabel();
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
		JLabel timerTitle = new JLabel();
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
		timerPanel.add(timerTitle);
		timerPanel.add(secondPanel);
		timerPanel.setOpaque(false);
		minePanel.setOpaque(false);
		gameHUD.add(timerPanel);
		gameHUD.add(minePanel);
		createTimer();
	}
	public void tempPromptName(){
		String name = JOptionPane.showInputDialog("Please Input your name");
		this.player = new Player(name);
		this.playerName.setText(this.player.getName());
		JOptionPane.showMessageDialog(this, "Welcome "+this.player.getName(),"Welcome",JOptionPane.INFORMATION_MESSAGE);
		//out.println("NAME:"+this.player.getName());
	}
	public void promptMineAmount(){
		String[] options = {"OK"};
		JPanel panel = new JPanel();
		JLabel lbl = new JLabel("Please Specify your number of mine < 36 ");
		JTextField txt = new JTextField(10);
		panel.add(lbl);
		panel.add(txt);
		int mine = JOptionPane.showOptionDialog(null, panel, "Mine Input", JOptionPane.NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options , options[0]);
		if(mine == 0){
			if(txt.getText().equals("")){
				maxMine = 11;
			} else {
				this.maxMine = Integer.parseInt(txt.getText());
			}
			
		}
		
		
		
	}
	private void createNewGridPanel(){
		bombGrid = new JPanel();
		
		bombGrid.setLayout(new GridLayout(6,6));
	}
	private void createTimer(){
		//GameUIClient.seconds = 1;
		ActionListener timerAct = new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if(myTurn) timerLabel.setText(seconds+"s");
				else timerLabel.setText("Waiting:"+seconds+"s");
				
				if(seconds <0) {
					//turnTimer.stop();
					seconds = 10;
					//startTimer();
					// opponent turn
					myTurn = false;
					setFieldTurn();
					out.println("TimeYourTurn");
					
				} 
				seconds--;
			}
			
		};
		turnTimer = new Timer(1000,timerAct);
		//if(myTurn)turnTimer.start();
	}
	private void resetScore(){
		this.mineCount = 0;
		player.setScore(0);
		playerScore.setText("0");
		opponent.setScore(0);
		opponentScore.setText("0");
	}
	private void createBombGrid(){
		createNewGridPanel();
		resetBombGrid(this.maxMine);
		for(BombPanel panel:bombField){
			bombGrid.add(panel);
		}
		repaint();
	}
	private void resetBombGrid(int mine){
		int count = 0;
		//this.maxMine = mine;
		bombField = new BombPanel[36];
		for(int i = 0; i<36 ;i++){
			bombField[i] = new BombPanel();
			bombField[i].setButtonListener(new BombListener(this,i));
		//	bombField[i].addBombListener();
			if(count == mine) {
				bombField[i].setBomb(false); continue;
			}
			if(bombField[i].checkBomb()) count++;
		}
		if(count < mine){
			resetBombGrid(mine);
		}
		if(isConnected)this.sendSameBombGrid();
		
	}

	private void setBombGrid(BombPanel grid[]){
		createNewGridPanel();
		for(int i = 0;i<this.bombField.length;i++){
			//bombField[i] = grid[i];
			
			bombGrid.add(grid[i]);
		}
		bombGrid.setVisible(true);
		this.add(bombGrid,BorderLayout.CENTER);
		repaint();
	}
	
	

	public void processBombGrid(int index){
		
		myTurn = true;
		setFieldTurn();
		this.bombField[index].clickButton();
		this.computeScore(index,false);
		
		 
	}
	public void sendCurrentBombGrid(int index){
//		if(myTurn){
			this.myTurn=false;
			
			
			setFieldTurn();
			
			
			out.println(index);
			//System.out.println(index);
			
			
//		}
	}
	public void computeScore(int panel,boolean isPlayer) {
	
		if(bombField[panel].checkBomb()){
			if(isPlayer){
			this.player.addScore();
			this.playerScore.setText(""+player.getScore());
			this.mineCount++;
			
			//out.println("Score"+player.getScore());
			} else {
				this.opponent.addScore();
				this.opponentScore.setText(""+this.opponent.getScore());
				this.mineCount++;
			}
		}
		this.mineLeft.setText(""+(this.maxMine-this.mineCount));
		this.checkScore();
		
	}
	
	private void checkScore(){
		ResultPanel result = new ResultPanel();
		if(this.mineCount >= this.maxMine){
			out.println("END");
			this.turnTimer.stop();
			result.setPlayerScore(this.player.getScore());
			result.setAwayScore(this.opponent.getScore());
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
	private void sendSameBombGrid(){
		String bombIndex = "F";
		for(int i=0; i<this.bombField.length;i++){
			if(bombField[i].checkBomb()){
				bombIndex = bombIndex + i + " ";
			}
		}
		out.println(bombIndex);
	}
	private void setReceiveField(String indexString) {
		// TODO Auto-generated method stub
		bombGrid.setVisible(false);
		
		ArrayList<Integer> bomb = new ArrayList<Integer>();
		String temp = indexString.substring(1,indexString.length());
		for(int i =0; i<this.maxMine;i++){
			if(temp.indexOf(" ") == -1) break;
			bomb.add(Integer.parseInt(temp.substring(0, temp.indexOf(" "))));
			temp = temp.substring(temp.indexOf(" ")+1);
		}
		for(int i =0;i<this.bombField.length;i++){
			bombField[i] = new BombPanel();
			bombField[i].setButtonListener(new BombListener(this,i));
			bombField[i].setBomb(false);
		}
		for(int i =0;i < bomb.size();i++){
			bombField[bomb.get(i)].setBomb(true);
		}
		this.setBombGrid(this.bombField);
		
	}
	public void setFieldTurn(){
		
		if(myTurn){
			//this.opponentName.setText("Your Turn");
			GameUIClient.seconds = 10;
			this.turnTimer.restart();
			for(int i = 0; i<this.bombField.length;i++){
				bombField[i].setButtonEnable();
			}
		} else {
			this.timerLabel.setText("Wait");
			this.totaltime = this.totaltime + seconds;
			
			this.turnTimer.stop();
			GameUIClient.seconds = 10;
			
			for(int i = 0; i<this.bombField.length;i++){
				bombField[i].setButtonDisable();
			}
		}
		repaint();
	}
	
	public void run() {

		// TODO Auto-generated method stub
		boolean stillOn = true;
		while(stillOn){
			try {
			
				String indexString = in.readLine();
				System.out.println("In run"+indexString);
				if(indexString.equals("Start")){
					this.isOpponentNull = true;
					sendSameBombGrid();
				} else if (indexString.startsWith("Waiting")){
					System.out.println("Wait for grid");
					//this.isOpponentNull = true;
				} else if(indexString.startsWith("F")){
					
					setReceiveField(indexString);
					if(isOpponentNull)this.sendPlayerName();
					this.isOpponentNull = false;
					this.setFieldTurn();
				} else if (indexString.equals("TimeYourTurn")){
					this.myTurn = true;
					this.setFieldTurn();
					
				} else if (indexString.startsWith("MaxMine:")){
					String mine = indexString.substring(8);
					this.maxMine = Integer.parseInt(mine);
					maxMineCount.setText(""+this.maxMine);
					this.mineLeft.setText(""+(this.maxMine-this.mineCount));
				} 
				else if (indexString.startsWith("NAME:")){
					
					this.opponent = new Player(indexString.substring(indexString.indexOf("NAME:")+5));
					this.opponentName.setText(this.opponent.getName());
					if(isOpponentNull)this.sendPlayerName();
					this.isOpponentNull = false;
				} else if (indexString.equals("END")){
					this.checkScore();
				} else if (indexString.equals("Quit")){
					JOptionPane.showMessageDialog(this, "Your Opponent Quit");
					System.exit(0);
				} else if (indexString.equals("Rematch")){
					if(confirmRematch){
						bombGrid.setVisible(false);
						createBombGrid();
						out.println("Reset");
						add(bombGrid,BorderLayout.CENTER);
						bombGrid.setVisible(true);
						resetScore();
					} else {
						JOptionPane.showMessageDialog(this, "Your Opponent Quit");
						System.exit(0);
					}
				} else if (indexString.equals("Reset")){
					turnTimer.stop();
					seconds = 10;
					this.randomTurn();
					this.resetScore();
				}
				
				else if (indexString.startsWith("T")){
					//if(this.opponent.equals(null)) this.sendPlayerName();
					String testTurn = indexString.substring(1);
					turnTimer.stop();
					seconds = 10;
					if(testTurn.equals("First")){
						
						this.myTurn = true;
					
						this.setFieldTurn();
						
					} else {
						this.myTurn = false;
						this.setFieldTurn();
					}
					//this.playerName.setText(testTurn);
				} else {
					
					int index = Integer.parseInt(indexString);
//					System.out.println(index);
					
					processBombGrid(index);
				}
				
				
				
	        } catch (IOException e) {
	            System.err.println("Problem with Communication Server in Client");
	            stillOn = false;
	        }
        }
		
	}	
	
	
	
	
	
	private void sendPlayerName() {
		// TODO Auto-generated method stub
		//if(isOpponentNull){
			out.println("NAME:"+this.player.getName());
		//}
	}
	
	
	
	
	
	
}


