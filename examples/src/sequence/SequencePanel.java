package sequence;

public class SequencePanel {
	public static final int BOARD_WIDTH	   = 335;
	public static final int BOARD_HEIGHT	= 335;
	public static final int TILE_SIZE		= 28;
	public static final int GAP_SIZE		   = 5;
	public static final int CARD_STAGGER	= 15;
	public static final int CARD_WIDTH		= 71;
	public static final int CARD_HEIGHT		= 96;
	
   //	public static final Font FONT = new Font("SansSerif", Font.BOLD, 60);

   /*	
	private Image BOARD_IMG;
	private Image TILE_IMGS[];
	private Image CARD_IMGS[][];
	private Image CARD_BACK_IMAGE;
	private Image SPLASH_IMAGE;
	private Image MARKED_CHIP_IMAGE;
   */
	
	private Element deadCardButton, newGameButton;
	private Element cardsLeftLabel;
   //	private Image offimg;
   //	private Graphics gg;
	private Dimension mySize;
	
	private SequenceGame game;

	public SequencePanel() {//Image cards[][], Image images[]){
		CARD_IMGS = cards;
		BOARD_IMG = images[0];
		TILE_IMGS = new Image[]{images[1], images[2]};
		CARD_BACK_IMAGE = images[3];
		SPLASH_IMAGE = images[4];
		MARKED_CHIP_IMAGE = images[5];
		
		game = new SequenceGame();
		
		offimg = null;
		gg = null;
		mySize = new Dimension();
		
		deadCardButton = new Button("Dead card");
		deadCardButton.addActionListener(this);
		deadCardButton.setEnabled(false);
		
		newGameButton = new Button("New Game");
		newGameButton.addActionListener(this);
		
		cardsLeftLabel = new Label("Cards left: ", Label.CENTER);

		Panel pane1 = new Panel();
		pane1.setLayout(new FlowLayout(FlowLayout.CENTER));
		pane1.setBackground(Color.green);
		pane1.add(newGameButton);
		pane1.add(deadCardButton);
		pane1.add(cardsLeftLabel);

		setLayout(new BorderLayout());
		add(pane1, BorderLayout.SOUTH);

		addMouseListener(this);
	}
	
	public Dimension getPreferredSize(){
		return new Dimension(BOARD_WIDTH+100, BOARD_HEIGHT+30+CARD_HEIGHT+100);
	}
	public Dimension getMinimumSize(){
		return new Dimension(BOARD_WIDTH+100, BOARD_HEIGHT+30+CARD_HEIGHT+100);
	}
	public Dimension getMaximumSize(){
		return new Dimension(BOARD_WIDTH+100, BOARD_HEIGHT+30+CARD_HEIGHT+100);
	}
	
	private void startGame(){
		game.startGame();
		resetButtons();
		cardsLeftLabel.setText("Cards left: "+game.getCardsLeft());
		validate();
		doLayout();
		repaint();
	}
	
	private void resetButtons(){
		deadCardButton.setEnabled(false);
		Card[] playerHand = game.getPlayerHand();
		Tile[] board = game.getBoard();
		
		if (game.getState() == SequenceGame.STATE_PLAYER_TURN){
			if (game.getSelectedCardIndex() != -1 && playerHand[game.getSelectedCardIndex()].getValue() != SequenceGame.JACK){
				int temp[] = SequenceGame.BOARD_POS[playerHand[game.getSelectedCardIndex()].getSuit()][playerHand[game.getSelectedCardIndex()].getValue()];
				if (!(board[temp[0]]==null || board[temp[1]]==null)){
					deadCardButton.setEnabled(true);
				}
			}
		}
	}
	
	private int getBoardSquare(int x, int y){
		int xstart = (getSize().width-BOARD_WIDTH)/2;
		
		x-=xstart;
		
		if (x<0 || x>=BOARD_WIDTH || y<0 || y>=BOARD_HEIGHT)
			return -1;
			
		int r = y/(GAP_SIZE+TILE_SIZE);
		int c = x/(GAP_SIZE+TILE_SIZE);
		
		x -= c*(GAP_SIZE+TILE_SIZE);
		y -= r*(GAP_SIZE+TILE_SIZE);
		
		if (x<10 || y<10)
			return -1;

		int answer = (10*r+c);
		if (answer == 0 || answer == 9 || answer == 90 || answer == 99)
			return -1;

		return answer;
	}

	public void update(Graphics g){paint(g);}
	
	private Image makeImage(int wid, int hei){
		Image temp = null;
		while(temp == null)
			temp = createImage(wid, hei);
		return temp;
	}
	
	public void paint(Graphics g){
		if (!mySize.equals(getSize()) || offimg == null || gg == null){
			mySize = new Dimension(getSize());
			offimg = makeImage(mySize.width, mySize.height);
			gg = offimg.getGraphics();
		}
		gg.setColor(Color.green);
		gg.fillRect(0, 0, mySize.width, mySize.height);

		Card[] playerHand = game.getPlayerHand();
		Card[] compHand = game.getComputerHand();
		Tile[] board = game.getBoard();

		int xstart = (mySize.width-BOARD_WIDTH)/2;

		switch(game.getState()){
			case SequenceGame.STATE_PLAYER_TURN:{
				gg.drawImage(BOARD_IMG, xstart, 0, this);
				gg.setColor(Color.black);
				gg.drawRect(xstart, 0, BOARD_WIDTH, BOARD_HEIGHT);

				for (int i=0;i<100;i++){
					if (board[i] != null){
						int r = i/10;
						int c = i%10;

						gg.drawImage(TILE_IMGS[board[i].tile], xstart + GAP_SIZE + (GAP_SIZE+TILE_SIZE)*c, GAP_SIZE + (GAP_SIZE+TILE_SIZE)*r, this);
						if (!board[i].movable)
							gg.drawImage(MARKED_CHIP_IMAGE, xstart + GAP_SIZE + (GAP_SIZE+TILE_SIZE)*c, GAP_SIZE + (GAP_SIZE+TILE_SIZE)*r, this);
					}
				}
				
				int xcount=0;
				for (int i=0;i<5;i++){
					if (playerHand[i]!=null){
						int xstart2 = 30;
						int ystart2 = BOARD_HEIGHT+30+50;

						if (i==game.getSelectedCardIndex())
							gg.drawImage(CARD_IMGS[playerHand[i].getSuit()][playerHand[i].getValue()], xstart2+xcount*CARD_STAGGER, ystart2-15, this);
						else
							gg.drawImage(CARD_IMGS[playerHand[i].getSuit()][playerHand[i].getValue()], xstart2+xcount*CARD_STAGGER, ystart2, this);
						xcount++;
					}
				}
				
				xcount = 0;
				for (int i=0;i<5;i++){
					if (compHand[i]!=null){
						int xstart2 = mySize.width-(4*CARD_STAGGER+CARD_WIDTH+30);
						int ystart2 = BOARD_HEIGHT+30+50;

						gg.drawImage(CARD_BACK_IMAGE, xstart2+xcount*CARD_STAGGER, ystart2, this);
						xcount++;
					}
				}
				
				Card discard = game.getDiscard();
				if (discard != null)
					gg.drawImage(CARD_IMGS[discard.getSuit()][discard.getValue()], (mySize.width-CARD_WIDTH)/2, BOARD_HEIGHT+30, this);
				break;
			}
			
			case SequenceGame.STATE_SPLASH:{
				gg.drawImage(BOARD_IMG, xstart, 0, this);
				gg.drawImage(SPLASH_IMAGE, xstart, 0, this);
				gg.setColor(Color.black);
				gg.drawRect(xstart, 0, BOARD_WIDTH, BOARD_HEIGHT);
				break;
			}

			case SequenceGame.STATE_GAME_OVER:{
				gg.drawImage(BOARD_IMG, xstart, 0, this);
				gg.setColor(Color.black);
				gg.drawRect(xstart, 0, BOARD_WIDTH, BOARD_HEIGHT);

				for (int i=0;i<100;i++){
					if (board[i] != null){
						int r = i/10;
						int c = i%10;

						gg.drawImage(TILE_IMGS[board[i].tile], xstart + GAP_SIZE + (GAP_SIZE+TILE_SIZE)*c, GAP_SIZE + (GAP_SIZE+TILE_SIZE)*r, this);
						if (!board[i].movable)
							gg.drawImage(MARKED_CHIP_IMAGE, xstart + GAP_SIZE + (GAP_SIZE+TILE_SIZE)*c, GAP_SIZE + (GAP_SIZE+TILE_SIZE)*r, this);
					}
				}

				FontMetrics fmet = g.getFontMetrics(FONT);
				int xstart3 = (mySize.width-fmet.stringWidth("Game Over"))/2;
				String winstring = (game.getWinner() == SequenceGame.PLAYER_ID ? "You win!" : "You lose!");
				int xstart4 = (mySize.width-fmet.stringWidth(winstring))/2;

				gg.setColor(Color.black);
				gg.setFont(FONT);
				gg.drawString("Game Over", xstart3, BOARD_HEIGHT+30+fmet.getAscent());
				gg.drawString(winstring, xstart4, BOARD_HEIGHT +30+ 2*fmet.getAscent());
				break;
			}
		}
		
		g.drawImage(offimg, 0,0, this);
	}
	
	public void actionPerformed(ActionEvent e){
		Object source = e.getSource();
		
		if (source == newGameButton){
			startGame();
		}else if (source == deadCardButton && deadCardButton.isEnabled()){
			if (!game.replaceDeadCard())
				return;
			deadCardButton.setEnabled(false);
			cardsLeftLabel.setText("Cards left: "+game.getCardsLeft());
			validate();
			doLayout();
			repaint();
		}
	}
	
	public void mousePressed(MouseEvent e){
		int xstart2 = 30;
		int ystart2 = BOARD_HEIGHT+30+50;
		int x = e.getX();
		int y = e.getY();
		Card[] playerHand = game.getPlayerHand();

		switch(game.getState()){
			case SequenceGame.STATE_PLAYER_TURN:{
				if (game.getSelectedCardIndex() == -1){
					// no card selected
					x -= xstart2;
					y -= ystart2;
					int cardcount=game.getCardCount(SequenceGame.PLAYER_ID);

					if (x<0 || x>=((cardcount-1)*CARD_STAGGER+CARD_WIDTH) || y<0 || y>=CARD_HEIGHT)
						return;

					int card = x/CARD_STAGGER;
					if (card>(cardcount-1))
						card = cardcount-1;
					game.setSelectedCardIndex(card);
					
					resetButtons();
				}else{
					// some card selected
					
					int boardNum = getBoardSquare(x, y);
					if (boardNum == -1){
						// didn't click a square, mighta clicked the cards
						x -= xstart2;
						y -= ystart2;

						if (x<0 || x>=(4*CARD_STAGGER+CARD_WIDTH) || y>=CARD_HEIGHT)
							return;

						int card = -1;
						for (int i=0;i<5;i++){
							if (playerHand[i] != null){
								int xs = i*CARD_STAGGER;
								int ys = (i == game.getSelectedCardIndex() ? -30 : 0);

								if (xs<=x && x<(xs+CARD_WIDTH) && ys<=y && y<(ys+CARD_HEIGHT))
									card = i;
							}
						}

						if (card==-1){
							return;
						}else if (card == game.getSelectedCardIndex()){
							game.setSelectedCardIndex(-1);
						}else{
							game.setSelectedCardIndex(card);
						}
						
						resetButtons();
					}else{
						// clicked a square
						if (game.selectTile(boardNum))
							// if made a legit play, continue
							game.computerTurn();
						cardsLeftLabel.setText("Cards left: "+game.getCardsLeft());
						validate();
						doLayout();
					}
				}
				break;
			}
			
		}
		
		repaint();
	}
}
