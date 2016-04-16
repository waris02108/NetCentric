import java.io.*;
import java.net.Socket;


public class ClientThread extends Thread{
	private Socket con;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private Server server;
	private GameUIClient game;
	int indexClicked;
	
	public ClientThread(Socket s){
		con = s;
		
		try {
			out = new ObjectOutputStream(con.getOutputStream());
			in = new ObjectInputStream(con.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void run(){
		System.out.println("ClientThreadRunning");
		//play game
		while(true){
			try {
				int index = in.readInt();
				System.out.println("PRINT");
				System.out.println(index);
				game.processBombGrid(index);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
