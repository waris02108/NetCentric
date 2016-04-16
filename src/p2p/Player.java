package p2p;
import javax.swing.ImageIcon;


public class Player {
	private String name;
	private ImageIcon profilePic;
	private int score;
	private boolean first;
	private boolean myTurn;
	public Player(String name){
		this.name = name;
		score = 0;
	}
	public void setName(String name){
		this.name = name;
	}
	public void setImage(ImageIcon img){
		this.profilePic = img;
	}
	public void setScore(int score){
		this.score = score;
	}
	public void setFirst(boolean isFirst){
		this.first = isFirst;
	}
	public void setMyTurn(boolean myTurn){
		this.myTurn = myTurn;
	}
	public ImageIcon getImage(){
		return this.profilePic;
	}
	public int getScore(){
		return this.score;
	}
	public void addScore(){
		this.score++;
	}
	public String getName(){
		return this.name;
	}
	public boolean isFirst(){
		return this.first;
	}
	public boolean isMyTurn(){
		return this.myTurn;
	}
		
}
