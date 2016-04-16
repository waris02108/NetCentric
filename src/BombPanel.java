import java.net.*;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

public class BombPanel extends JPanel{
	private boolean isBomb;
	private boolean isClickable;
	private BufferedImage setImage = null;
	private BufferedImage bombImg;
	private BufferedImage freeImg;
	private JLabel showImage;
	private ImageIcon question;
	private JButton button;
	private BombListener bombListener;
	public BombPanel(){
		super();
		isBomb = false;
		isClickable = true;
		Image ques = null;
		try {
			ques = ImageIO.read(new File("question2.gif"));
			bombImg = ImageIO.read(new File("bomb.gif"));
			freeImg = ImageIO.read(new File("free.gif"));
		} catch (IOException e) {
			System.out.println(e.toString());
		}
		Image newImg = ques.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
		question = new ImageIcon(newImg);
		button = new JButton();
		Border thickBorder = new LineBorder(Color.WHITE, 1);
		button.setBorder(thickBorder);
		button.setPreferredSize(new Dimension(150,150));
		
		button.setIcon(question);
		//button.setDisabledIcon(question);
		
		button.addActionListener(new ActionListener(){
		

			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				button.setVisible(false);
				showImage.setVisible(true);
				isClickable=false;
				//System.out.println((isBomb? "bomb": "free" ) + " Score:" + getScore());
			}
		});
		this.add(button);
		this.randomBomb();
		
	}
	
	public void setBomb(boolean bomb){
		this.isBomb = bomb;
		this.resetShowImage();
	}
	public void setButtonDisable(){
		this.button.setEnabled(false);
		this.removeBombListener();
	}
	public void setButtonEnable(){
		this.button.setEnabled(true);
		this.addBombListener();
	}
	public void resetShowImage(){
		if(isBomb)setImage = bombImg;
		else setImage = freeImg;
		showImage.setIcon(new ImageIcon(setImage));
	}
	public void randomBomb(){
		isBomb = (int)(Math.random()*1.55555)== 1? true:false ;
		if(isBomb)setImage = bombImg;
		else setImage = freeImg;
		//repaint();
		showImage = new JLabel( new ImageIcon(setImage));
		showImage.setPreferredSize(new Dimension(150,150));
		this.add(showImage);
		showImage.setVisible(false);
	}
	public boolean checkBomb(){
		return isBomb;
	}
	public void clickButton(){
		if(isClickable){
			this.removeBombListener();
			button.doClick();
			isClickable = false;
		}
	}
	public boolean isClickable(){
		return this.isClickable;
	}
	public void removeBombListener(){
		this.button.removeMouseListener(this.bombListener);
	}
	public void addBombListener(){
		this.button.addMouseListener(this.bombListener);
	}
	public void setButtonListener(BombListener b){
		this.bombListener = b;
		//this.addBombListener();
	}
	/*protected void paintComponent(Graphics g){
		super.paintComponent(g);
		g.drawImage(setImage,0,0,null);
	}*/
	public int getScore(){
		return isBomb == true? 1:0 ;
	}
	
}
