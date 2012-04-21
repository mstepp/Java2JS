import java.awt.*;
import java.awt.event.*;
import java.applet.*;

public class SequenceApplet extends Applet{
	private SequencePanel panel;
	
	public void init(){
		setLayout(new BorderLayout());
	
		Image[] images = new Image[6];
		images[0] = getImage(getCodeBase(), "board2.gif");
		images[1] = getImage(getCodeBase(), "newchip0.gif");
		images[2] = getImage(getCodeBase(), "newchip1.gif");
		images[3] = getImage(getCodeBase(), "cardback.gif");
		images[4] = getImage(getCodeBase(), "splash.gif");
		images[5] = getImage(getCodeBase(), "marked.gif");

		Image cards = getImage(getCodeBase(), "cards.gif");

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
				cardImgs[i][j] = createImage(SequencePanel.CARD_WIDTH, SequencePanel.CARD_HEIGHT);
				Graphics g = cardImgs[i][j].getGraphics();
				g.drawImage(cards, -((j-1)*71), -(i*96), this);
			}
		}
		
		cards = null;
		panel = new SequencePanel(cardImgs, images);
		
		add(panel, BorderLayout.CENTER);
	}
}
