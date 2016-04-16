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
	
	
	private static int seconds = 10;
	boolean myTurn;
	JSONObject exString;
	int maxMine;
	int mineCount = 0;
	Player player;
	Player opponent;
	boolean isConnected;
	Timer turnTimer;
	boolean isOpponentNull = false;
	
//	ObjectOutputStream out;
//	ObjectInputStream in;
	PrintWriter out;
	BufferedReader in;
	Socket con;
	Thread outputThread;
	
	 
	
	public GameUIClient() throws IOException{
		
		super();
		player = new Player("Por");
		
		isConnected = false;
		setGUI();
		
		
	}
	
	public void start(){
		try{
			con = new Socket("127.0.0.1",1256);
			in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
			out = new PrintWriter(con.getOutputStream(),true);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Error during initial connection");
			System.exit(1);
		}
		isConnected = true;
		outputThread = new Thread(this);
		outputThread.start();
	
	}
	private void setGUI(){
		
		this.setLayout(new BorderLayout());
		createBombGrid();
		add(bombGrid,BorderLayout.CENTER);
		JButton reset =new JButton("RESET");
		reset.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				bombGrid.setVisible(false);
				createBombGrid();
				add(bombGrid,BorderLayout.CENTER);
				repaint();
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
		gameHUD.setLayout(new GridLayout(3,1));
		JPanel profile = new JPanel();
		profile.setLayout(new GridLayout(1,2));
		JPanel playerPanel = new JPanel(new GridLayout(2,1));
		playerName = new JLabel("Player1:"+this.player.getName());
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
		
		//TIMER LABEL
		JPanel timerPanel = new JPanel();
		JLabel timerTitle = new JLabel("Timer");
		timerLabel = new JLabel();
		timerLabel.setText("Seconds Left:");
		timerPanel.setLayout(new GridLayout(1,2));
		timerPanel.add(timerTitle);
		timerPanel.add(timerLabel);
		gameHUD.add(timerPanel);
		createTimer();
	}
	public void tempPromptName(){
		String name = JOptionPane.showInputDialog("Please Input your name");
		this.player = new Player(name);
		this.playerName.setText("Player1:"+this.player.getName());
		JOptionPane.showMessageDialog(this, "Welcome "+this.player.getName(),"Welcome",JOptionPane.INFORMATION_MESSAGE);
		//out.println("NAME:"+this.player.getName());
	}
	private void synchronizeStart(){
		
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
				
				if(seconds >10) {
					//turnTimer.stop();
					seconds = 1;
					//startTimer();
					// opponent turn
					myTurn = false;
					setFieldTurn();
					out.println("NextTurn");
				} 
				seconds++;
			}
			
		};
		turnTimer = new Timer(1000,timerAct);
		//if(myTurn)turnTimer.start();
	}
	private void createBombGrid(){
		createNewGridPanel();
		resetBombGrid(11);
		for(BombPanel panel:bombField){
			bombGrid.add(panel);
		}
		repaint();
	}
	private void resetBombGrid(int mine){
		int count = 0;
		maxMine = mine;
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
			
			bombGrid.add(bombField[i]);
		}
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
		if(this.mineCount >= this.maxMine){
			Object options[] = {"Quit", "Rematch"};
			if(this.player.getScore()>this.opponent.getScore()){
				
				Object selected = JOptionPane.showInputDialog(this,"Congratulation","You Win!!!",
						JOptionPane.INFORMATION_MESSAGE,null,options,options[0]);
				if(selected.equals(options[0])){
					System.exit(0);
				} else {
					this.createBombGrid();
				}
			} else {
				Object selected = JOptionPane.showInputDialog(this,"Defeat","You Lose!!!",
						JOptionPane.INFORMATION_MESSAGE,null,options,options[0]);
				if(selected.equals(options[0])){
					System.exit(0);
				} else {
					this.createBombGrid();
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
		for(int i =0; i<11;i++){
			if(temp.indexOf(" ") == -1) break;
			bomb.add(Integer.parseInt(temp.substring(0, temp.indexOf(" "))));
			temp = temp.substring(temp.indexOf(" ")+1);
		}
		for(int i =0;i<this.bombField.length;i++){
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
			GameUIClient.seconds = 1;
			
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
				} else if (indexString.equals("TimeYourTurn")){
					
					
					this.myTurn = true;
					this.setFieldTurn();
					
				} else if (indexString.startsWith("Opponent")){
					
					this.opponent = new Player(indexString.substring(indexString.indexOf("Opponent")+8));
					this.opponentName.setText("Player2:"+this.opponent.getName());
					if(isOpponentNull)this.sendPlayerName();
					this.isOpponentNull = false;
				}
				
				else if (indexString.startsWith("T")){
					//if(this.opponent.equals(null)) this.sendPlayerName();
					String testTurn = indexString.substring(1);
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

	public static void main(String [] args) throws IOException{
		JFrame frame = new JFrame();
		GameUIClient s = new GameUIClient();
		frame.add(s);
		//frame.pack();
		frame.setSize(new Dimension(1000,800)); frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		s.tempPromptName();
		s.start();
		
	}
	
	
	
}


