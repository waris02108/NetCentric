package p2p;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;


class BombListener extends MouseAdapter {
	private GameUIClient game;
	private int panel;
	
	public BombListener(GameUIClient g, int p){
		this.game = g;
		this.panel = p;
	}
	
	public void mouseReleased(MouseEvent e){
		
		System.out.println("block"+panel);
		game.sendCurrentBombGrid(panel);
		game.computeScore(panel,true);
	
		
		//game.processBombGrid(panel);
	}
}
