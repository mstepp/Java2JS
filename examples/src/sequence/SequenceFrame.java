import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

public class SequenceFrame extends Frame implements WindowListener{
	private SequencePanel panel;
	
	public SequenceFrame(){
		addWindowListener(this);
		setTitle("Sequence");
		setLayout(new BorderLayout());
	
		Toolkit tool = Toolkit.getDefaultToolkit();
		
		Image[] images = new Image[6];
		images[0] = tool.getImage("board2.gif");
		images[1] = tool.getImage("newchip0.gif");
		images[2] = tool.getImage("newchip1.gif");
		images[3] = tool.getImage("cardback.gif");
		images[4] = tool.getImage("splash.gif");
		images[5] = tool.getImage("marked.gif");

		Image cards = tool.getImage("cards.gif");

		MediaTracker med = new MediaTracker(this);
		med.addImage(cards, 0);
		med.addImage(images[0], 0);
		med.addImage(images[1], 0);
		med.addImage(images[2], 0);
		med.addImage(images[3], 0);
		med.addImage(images[4], 0);
		med.addImage(images[5], 0);
		try{
			med.waitForAll();
		}catch(Exception ex){}
		
		Image cardImgs[][] = new Image[4][14];
		for (int i=0;i<4;i++){
			for (int j=1;j<=13;j++){
				cardImgs[i][j] = new BufferedImage(SequencePanel.CARD_WIDTH, SequencePanel.CARD_HEIGHT, BufferedImage.TYPE_INT_RGB);//bcards.getSubimage((j-1)*SequencePanel.CARD_WIDTH, i*SequencePanel.CARD_HEIGHT, SequencePanel.CARD_WIDTH, SequencePanel.CARD_HEIGHT);//
				Graphics g = cardImgs[i][j].getGraphics();
				g.drawImage(cards, -((j-1)*71), -(i*96), this);
			}
		}
		
		cards = null;
		panel = new SequencePanel(cardImgs, images);
		
		add(panel, BorderLayout.CENTER);
		
		setResizable(false);
		pack();
		show();
	}
	

	public void windowClosing(WindowEvent e){
		System.exit(0);
	}

	public void windowClosed(WindowEvent e){}
	public void windowActivated(WindowEvent e){}
	public void windowDeactivated(WindowEvent e){}
	public void windowIconified(WindowEvent e){}
	public void windowDeiconified(WindowEvent e){}
	public void windowOpened(WindowEvent e){}
	
	public static void main(String args[]){
		SequenceFrame f = new SequenceFrame();
	}
}
