package p2p;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
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
	boolean isOpponentNull = false;
	int mineCount = 0;
//	ObjectOutputStream out;
//	ObjectInputStream in;
	PrintWriter out;
	BufferedReader in;
	Socket con;
	Thread outputThread;
	boolean resetGrid = false;
	///SERVER PART
	boolean isServer;
	ServerSocket server;
	public GameUIClient() throws IOException{
		
		super();
		//player = new Player("Por");
		
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
				resetGrid = true;
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
		gameHUD = new JPanel();
		gameHUD.setLayout(new GridLayout(4,1));
		JPanel profile = new JPanel();
		profile.setLayout(new GridLayout(1,2));
		JPanel playerPanel = new JPanel(new GridLayout(2,1));
		playerName = new JLabel("");
		playerScore = new JLabel("Score:"+0);
		
		playerPanel.add(playerName);
		playerPanel.add(playerScore);
		
		JPanel opponentPanel = new JPanel(new GridLayout(2,1));
		opponentName = new JLabel("Player2");
		opponentScore = new JLabel("Score:0");
		opponentPanel.add(opponentName);
		opponentPanel.add(opponentScore);
		
		profile.add(playerPanel);
		profile.add(opponentPanel);
		gameHUD.add(profile);
		
		//Mine count + Maximum Mine
		JPanel minePanel = new JPanel();
		this.mineLeft = new JLabel("Mine Left:"+(this.maxMine-this.mineCount));
		this.maxMineCount = new JLabel("Total Mine:"+this.maxMine);
		minePanel.add(mineLeft);
		minePanel.add(maxMineCount);
		
		
		
		
		//TIMER LABEL
		JPanel timerPanel = new JPanel();
		JLabel timerTitle = new JLabel("Timer");
		timerLabel = new JLabel();
		timerLabel.setText("Seconds Left:");
		timerPanel.setLayout(new GridLayout(1,2));
		timerPanel.add(timerTitle);
		timerPanel.add(timerLabel);
		gameHUD.add(timerPanel);
		gameHUD.add(minePanel);
		createTimer();
	}
	public void tempPromptName(){
		String name = JOptionPane.showInputDialog("Please Input your name");
		this.player = new Player(name);
		this.playerName.setText("Player1:"+this.player.getName());
		JOptionPane.showMessageDialog(this, "Welcome "+this.player.getName(),"Welcome",JOptionPane.INFORMATION_MESSAGE);
		//out.println("NAME:"+this.player.getName());
	}
	public void promptMineAmount(){
		String mine = JOptionPane.showInputDialog("Please Specify your number of mine <36");
		if(mine.equals("")){
			maxMine = 11;
		} else {
			this.maxMine = Integer.parseInt(mine);
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
				timerLabel.setText("Time Left:"+seconds+"s");
				
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
		playerScore.setText("Score:0");
		opponent.setScore(0);
		opponentScore.setText("Score:0");
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
	private void randomBomb(int mine){
		
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
			this.playerScore.setText("Score:"+player.getScore());
			this.mineCount++;
			
			//out.println("Score"+player.getScore());
			} else {
				this.opponent.addScore();
				this.opponentScore.setText("Score:"+this.opponent.getScore());
				this.mineCount++;
			}
		}
		this.mineLeft.setText("Mine Left:"+(this.maxMine-this.mineCount));
		this.checkScore();
		
	}
	private void checkScore(){
		if(this.mineCount >= this.maxMine){
			out.println("END");
			this.turnTimer.stop();
			Object options[] = {"Quit", "Rematch"};
			if(this.player.getScore()>this.opponent.getScore()){
				
				Object selected = JOptionPane.showInputDialog(this,"Congratulation, You got"+this.player.getScore(),"You Win!!!",
						JOptionPane.INFORMATION_MESSAGE,null,options,options[0]);
				if(selected.equals(options[0])){
					System.exit(0);
				} else {
					this.createBombGrid();
					this.confirmRematch = true;
					out.println("Rematch");
				}
			} else if(this.player.getScore() == this.opponent.getScore() ){
				
				Object selected = JOptionPane.showInputDialog(this,"Draw, You got"+this.player.getScore(),"It a Tie!!!",
						JOptionPane.INFORMATION_MESSAGE,null,options,options[0]);
				if(selected.equals(options[0])){
					System.exit(0);
				} else {
					this.createBombGrid();
					this.confirmRematch = true;
					out.println("Rematch");
				}
			}
			
			
			else {
				
				Object selected = JOptionPane.showInputDialog(this,"Defeat, You got"+this.player.getScore(),"You Lose!!!",
						JOptionPane.INFORMATION_MESSAGE,null,options,options[0]);
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
			this.turnTimer.restart();
			for(int i = 0; i<this.bombField.length;i++){
				bombField[i].setButtonEnable();
			}
		} else {
			//this.opponentName.setText("Your Opponent Turn");
			this.timerLabel.setText("Wait for your opponent");
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
					this.isOpponentNull = true;
					if(isOpponentNull)this.sendPlayerName();
					this.isOpponentNull = false;
					this.setFieldTurn();
				} else if (indexString.equals("TimeYourTurn")){
					this.myTurn = true;
					this.setFieldTurn();
					
				} else if (indexString.startsWith("MaxMine:")){
					String mine = indexString.substring(8);
					this.maxMine = Integer.parseInt(mine);
					maxMineCount.setText("Total Mine:"+this.maxMine);
					this.mineLeft.setText("Mine Left:"+(this.maxMine-this.mineCount));
				} 
				else if (indexString.startsWith("NAME:")){
					
					this.opponent = new Player(indexString.substring(indexString.indexOf("NAME:")+5));
					this.opponentName.setText("Player2:"+this.opponent.getName());
					if(isOpponentNull)this.sendPlayerName();
					this.isOpponentNull = false;
				} else if (indexString.equals("END")){
					this.checkScore();
				} else if (indexString.equals("Quit")){
					JOptionPane.showMessageDialog(this, "Your Opponent Quit");
					System.exit(0);
				} else if (indexString.equals("Rematch")){
					if(confirmRematch){
						this.createBombGrid();
						this.bombGrid.setVisible(true);
						this.sendSameBombGrid();
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
	
	/*public static void main(String [] args) throws IOException{
		JFrame frame = new JFrame();
		GameUIClient s = new GameUIClient();
		frame.add(s);
		//frame.pack();
		frame.setSize(new Dimension(1000,800)); frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		s.tempPromptName();
		s.start();
		
	}*/
	
	
	
	
}


