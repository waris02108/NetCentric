import java.net.*;
import java.awt.GridLayout;
import java.io.*;
import java.awt.*;
import javax.swing.*;

public class PlayerConfigUI extends JPanel {
	public PlayerConfigUI(){
		 JTextField xField = new JTextField(5);
	     JTextField yField = new JTextField(5);
		 JPanel myPanel = new JPanel();
	      myPanel.add(new JLabel("x:"));
	      myPanel.add(xField);
	      myPanel.add(Box.createHorizontalStrut(15)); // a spacer
	      myPanel.add(new JLabel("y:"));
	      myPanel.add(yField); 
	}
}
