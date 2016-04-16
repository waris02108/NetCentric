package p2p;
import java.net.*;
import java.util.concurrent.Executors;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

public class BombPanel extends JPanel{
	private boolean isBomb;
	private boolean isClickable;
	private Image setImage = null;
	private Image bombImg;
	private Image freeImg;
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
			ques = ImageIO.read(new File("block.gif"));
			bombImg = ImageIO.read(new File("bombex.gif"));
			freeImg = ImageIO.read(new File("d.gif"));
		} catch (IOException e) {
			System.out.println(e.toString());
		}
		Image newImg = ques.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
		bombImg = bombImg.getScaledInstance(150,150, Image.SCALE_SMOOTH);
		freeImg = freeImg.getScaledInstance(150,150, Image.SCALE_SMOOTH);
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
				saEffect();
				button.setVisible(false);
				showImage.setVisible(true);
				
				isClickable=false;
				//System.out.println((isBomb? "bomb": "free" ) + " Score:" + getScore());
			}
		});
		this.add(button);
		this.randomBomb();
		
	}
	
	public void saEffect(){
		Main.insertBGM("button-30.wav");
		Executors.newSingleThreadExecutor().execute(new Runnable(){
			@Override
			
			public void run() {
				float alpha = 0.0f;
				Graphics g = button.getGraphics();
				if(g==null)return;
				Graphics2D g2d = (Graphics2D) g;
				g2d.setColor(Color.red);
				Image t = null;
		try {
					t = ImageIO.read(new File("boom.png"));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				while(alpha <=1.0f){
					g2d.setComposite(AlphaComposite.getInstance(
							AlphaComposite.SRC_OVER, alpha));
					g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
					g2d.setColor(Color.red);
					//g2d.fillOval(0, 0, 50, 50);
					g2d.drawImage(t, 0, 0, button.getWidth(),button.getHeight(), null);
					alpha += 0.02f;
					try {
						Thread.sleep(40);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				//	repaint();
				}
				repaint();
			}
		});
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
