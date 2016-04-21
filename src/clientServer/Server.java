package clientServer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Server extends JPanel {
	static ArrayList<PrintWriter> clientList;
	static ArrayList<Integer> clientID;
	static ArrayList<TestRunnable> clientRunnable;
	static final int POOL_SIZE = 10;
	static Executor execs = Executors.newFixedThreadPool(POOL_SIZE);
	static int numClients = 0;
	static String inputLine;
	static JTextArea showUser;
	JButton btnReset, btnScore;
	static ServerSocket server;

	public Server() throws IOException {
		setGUI();
		server = new ServerSocket(1256);
	}

	public void setGUI() {
		showUser = new JTextArea();
		showUser.setPreferredSize(new Dimension(300, 300));
		JScrollPane scroll = new JScrollPane(showUser);
		this.add(scroll);
		btnReset = new JButton("RESET");
		this.add(btnReset, BorderLayout.SOUTH);
		btnScore = new JButton("Update Score");
		this.add(btnScore, BorderLayout.SOUTH);
		this.setVisible(true);
	}

	public void startPlay() throws IOException {
		while (true) {
			System.out.println("Waiting for a Client ...");
			Socket con = server.accept();
			// System.out.println(clientList.toString());

			TestRunnable r = new TestRunnable(numClients, con);
			numClients++;
			execs.execute(r);
			clientRunnable.add(r);

		}

	}

	public static void main(String[] args) throws IOException {
		clientList = new ArrayList<PrintWriter>();
		clientID = new ArrayList<Integer>();
		clientRunnable = new ArrayList<TestRunnable>();
		JFrame mainFrame = new JFrame();
		Server s = new Server();
		mainFrame.add(s);
		mainFrame.setSize(new Dimension(400, 400));
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setVisible(true);
		s.startPlay();
	}

	// server for each one to handle dataflow
	class TestRunnable implements Runnable {
		int id;
		Socket con;
		int pairId;
		String name;

		final int NUM_REPEAT = 5;

		public TestRunnable(int id, Socket con) {
			this.pairId = -1;
			this.id = id;
			this.con = con;
			System.out.println("Client #" + id + " is connected.");
			Server.clientID.add(id);
		}

		public String echo(String msg) {
			return "Client #" + id + ": " + msg;
		}

		public void setPairID(int pairId) {
			this.pairId = pairId;
		}

		public void broadcast(String msg) {
			for (PrintWriter p : Server.clientList) {
				p.println(msg);
			}
		}

		public void randomTurn() {
			int random = (int) (Math.random() * 1.99999);
			if (random == 0) {
				// you go 2nd
				Server.clientList.get(pairId).println("TFirst");
				// opponent 1st
				Server.clientList.get(id).println("TSecond");
			} else {
				// you opponent last , you go first
				Server.clientList.get(pairId).println("TSecond");
				Server.clientList.get(id).println("TFirst");
			}
		}

		public void sendToPair(String msg) {
			Server.clientList.get(pairId).println(msg);
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			boolean stillOn = true;

			while (stillOn) {
				try {
					PrintWriter out = new PrintWriter(con.getOutputStream(),
							true);
					btnReset.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							sendToPair("ResetCommandFromServer");
						}
					});
					btnScore.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							sendToPair("ScoreCommandFromServer");
						}
					});
					Server.clientList.add(out);
					if (Server.numClients < 2) {
						out.println("Waiting for Another Player");
					}
					if (Server.numClients == 2) {
						Server.clientRunnable.get(0).setPairID(1);
						Server.clientRunnable.get(1).setPairID(0);
						// Start will synchronize the bombBoard
						out.println("Start");
						// Send random Turn
						this.randomTurn();
					}
					Server.showUser
							.append("Client #" + id + " is connected.\n");
					Server.showUser.append("Concurrent Online: " + numClients
							+ "\n");
					BufferedReader in = new BufferedReader(
							new InputStreamReader(con.getInputStream()));
					while ((Server.inputLine = in.readLine()) != null) {
						if (Server.inputLine.startsWith("F")) {
							sendToPair(Server.inputLine);
						} else if (Server.inputLine.equals("NextTurn")) {
							sendToPair("TimeYourTurn");
						} else if (Server.inputLine.startsWith("NAME:")) {
							// Server.showUser.append("Client #"+id+": "+Server.inputLine+"\n");
							String temp = "Opponent"
									+ Server.inputLine
											.substring(Server.inputLine
													.indexOf("NAME:") + 5);
							sendToPair(temp);
						} else if (Server.inputLine.equals("Reset")) {
							sendToPair(Server.inputLine);
						} else if (Server.inputLine.equals("#FinishReset"))
							this.randomTurn();
						else if (Server.inputLine.startsWith("Score")) {
							Server.showUser.append("===Update Score===\n"
									+ Server.inputLine.substring(5) + "\n");
						} else {
							sendToPair(Server.inputLine);
						}
					}
					System.out.println("Client#" + id + " has left.");
					out.close();
					in.close();
					con.close();
				} catch (IOException e) {
					System.err.println("Problem with Communication Server");
					stillOn = false;
				}
			}
			System.out.println("Client #" + id + " has lost its connection.");
			numClients--;
			for (int i = 0; true; i++) {
				if (Server.clientID.get(i) == id) {
					Server.clientList.remove(i);
					Server.clientID.remove(i);
					Server.clientRunnable.remove(i);
					broadcast("Client #" + id + " has left.");
					return;
				}
			}
		}
	}

	public static void insertBGM(String sound) {
		File soundFile = new File(sound);
		AudioInputStream audioIn4 = null;
		try {
			audioIn4 = AudioSystem.getAudioInputStream(soundFile);
			Clip clip2 = AudioSystem.getClip();
			clip2.open(audioIn4);
			clip2.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static Clip welcomeClip, gameClip;

	public void createSound() {
		File soundFile1 = new File("bensound-littleidea.wav");
		File soundFile2 = new File("bgm2.wav");
		AudioInputStream audioIn = null;
		AudioInputStream audioIn2 = null;
		try {
			audioIn = AudioSystem.getAudioInputStream(soundFile1);
			welcomeClip = AudioSystem.getClip();
			welcomeClip.open(audioIn);
			audioIn2 = AudioSystem.getAudioInputStream(soundFile2);
			gameClip = AudioSystem.getClip();
			gameClip.open(audioIn2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
