import java.net.*;
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

public class GameUI extends JPanel implements Runnable {
	private BombPanel bombField[];
	private JPanel bombGrid;
	JPanel gameHUD;
	///client & server info
	////FOR CLIENT
	boolean isClient;
	ObjectOutputStream out;
	ObjectInputStream in;
	Socket con;
	Thread serverListener;
	///FOR SERVER
	static final int POOL_SIZE = 10;
	static Executor execs = Executors.newFixedThreadPool(POOL_SIZE);
	ServerSocket soc;
	/////Server info
	
	public GameUI() throws IOException{
		super();
		setGUI();
//		
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
				
			}	
			
		});
		add(reset,BorderLayout.SOUTH);
		createGameHUD();
		add(gameHUD,BorderLayout.EAST);
		this.setVisible(true);
	}
	private void createGameHUD(){
		gameHUD = new JPanel();
		gameHUD.setLayout(new GridLayout(1,3));
		JPanel profile = new JPanel();
		profile.setLayout(new GridLayout(2,2));
		JLabel playerName = new JLabel("Player1");
		JLabel opponentName = new JLabel("Player2");
		profile.add(playerName);
		profile.add(opponentName);
		JLabel playerScore = new JLabel("Score:");
		JLabel opponentScore = new JLabel("Score:");
		profile.add(playerScore);
		profile.add(opponentScore);
		gameHUD.add(profile);
		
	}
	private void createNewGridPanel(){
		bombGrid = new JPanel();
		bombGrid.setLayout(new GridLayout(6,6));
	}
	private void createBombGrid(){
		createNewGridPanel();
		resetBombGrid(11);
		for(BombPanel panel:bombField){
			bombGrid.add(panel);
		}
		
	}
	private void resetBombGrid(int mine){
		int count = 0;
		bombField = new BombPanel[36];
		for(int i = 0; i<36 ;i++){
			bombField[i] = new BombPanel();
			if(count == mine) {
				bombField[i].setBomb(false); continue;
			}
			if(bombField[i].checkBomb()) count++;
		}
		if(count < mine){
			resetBombGrid(mine);
		}
	}
	private void setBombGrid(BombPanel grid[]){
		for(int i = 0;i<this.bombField.length;i++){
			bombField[i] = grid[i];
		}
		createNewGridPanel();
		for(BombPanel panel:bombField){
			bombGrid.add(panel);
		}
	}
	
	//////CLIENT SIDE //////////////
	private void connectToServer(String host,int port){
		try {
			con = new Socket("127.0.0.1",1256);
			out = new ObjectOutputStream(con.getOutputStream());
	        in = new ObjectInputStream(con.getInputStream());
	        out.writeObject(this.bombField);
//	processEchoedMsg(in.readLine());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Error during initial connection");
			System.exit(1);
		}
	}
	private void actAsClient(){
		serverListener = new Thread(new Runnable(){
			public void run(){
				int receiveIndex;
				try {
					receiveIndex = in.readInt();
				}catch (SocketException e) {
					System.err.println("reports connection reset");
				} catch (IOException e) {
					e.printStackTrace();
				} 
			}
		});
		serverListener.start();
	}
	private void processBombGrid(BombPanel recGrid[]){
		this.bombGrid.setVisible(false);
		this.setBombGrid(recGrid);
		add(bombGrid,BorderLayout.CENTER);
	}
	//////// END OF CLIENT SIDE/////////
	
	
	
	////////SERVER SIDE, implement RUNNABLE//////////////
	private void createServer(int port) throws IOException{
		soc = new ServerSocket(1256);
		while(true){
			System.out.println("Waiting for user....");
			con = soc.accept();
			execs.execute(this);
		}
	}
	public static void main(String [] args) throws IOException{
		JFrame frame = new JFrame();
		frame.add(new GameUI());
		frame.pack();
		frame.setSize(new Dimension(400,400)); frame.setVisible(true);
	}
	
	public void run() {
		// TODO Auto-generated method stub
		boolean stillOn = true;
		while(stillOn){
			try {
				ObjectOutputStream out = new ObjectOutputStream(con.getOutputStream());
				ObjectInputStream in = new ObjectInputStream(con.getInputStream());
				out.writeObject(this.bombField);
				int receiveIndex = 0;
				/*while ((recGrid = (BombPanel[])in.readObject()) != null) {
					processBombGrid(recGrid);
				}*/
//	            out.close();
//	           
//	            con.close();
	        } catch (IOException e) {
	            System.err.println("Problem with Communication Server");
	            stillOn = false;
	        }
        }
		
	}	
	
}
/*class ServerRunnable implements Runnable{
	//int id;
	Socket con;
	
	public ServerRunnable(Socket con){
		//this.id = id;
		this.con = con;
		System.out.println("Client is connected.");
		
	}
	
	public String echo(String msg){
		return "Client #"+id+": "+msg;
	}
	
	public void broadcast(String msg){
    	for(PrintWriter p:TestServer.clientList){
    		p.println(msg);
    	}
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		boolean stillOn = true;
		while(stillOn){
			try {
				ObjectOutputStream out = new ObjectOutputStream(con.getOutputStream());
				//ObjectInputStream in = new ObjectInputStream(con.getInputStream());
				out.writeObject(GameUI.bombField);
				
	            out.close();
	           
	            con.close();
	        } catch (IOException e) {
	            System.err.println("Problem with Communication Server");
	            stillOn = false;
	        }
        }
		
	}	
}*/

