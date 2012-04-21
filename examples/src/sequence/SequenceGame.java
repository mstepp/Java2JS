package sequence;

public class SequenceGame{
	public static final int HEARTS		= 0;
	public static final int DIAMONDS		= 1;
	public static final int CLUBS			= 2;
	public static final int SPADES		= 3;
	
	public static final int ACE			= 1;
	public static final int JACK			= 11;
	public static final int QUEEN			= 12;
	public static final int KING			= 13;
	
	public static final int PLAYER_ID	= 0;
	public static final int COMPUTER_ID	= 1;
	
	public static final int STATE_PLAYER_TURN		= 0;
	public static final int STATE_SPLASH			= 1;
	public static final int STATE_COMPUTER_TURN	= 2;
	public static final int STATE_GAME_OVER		= 3;
	
	public static final int BOARD_POS[][][] = 
		new int[][][]
		{
			{//HEARTS
				{-1, -1},// filler
				{46, 53},// ACE
				{47, 52},// 2
				{37, 62},// 3
				{48, 51},// 4
				{38, 61},// 5
				{28, 71},// 6
				{49, 50},// 7
				{39, 60},// 8
				{29, 70},// 9
				{19, 80},// 10
				{-1, -1},// JACK
				{27, 72},// QUEEN
				{18, 81} // KING
			},
			
			{//DIAMONDS
				{-1, -1},// filler
				{34, 65},// ACE
				{24, 75},// 2
				{23, 76},// 3
				{14, 85},// 4
				{13, 86},// 5
				{12, 87},// 6
				{ 4, 95},// 7
				{ 3, 96},// 8
				{ 2, 97},// 9
				{ 1, 98},// 10
				{-1, -1},// JACK
				{33, 66},// QUEEN
				{44, 55} // KING
			},
			
			{//CLUBS
				{-1, -1},// filler
				{43, 56},// ACE
				{42, 57},// 2
				{32, 67},// 3
				{41, 58},// 4
				{31, 68},// 5
				{21, 78},// 6
				{40, 59},// 7
				{30, 69},// 8
				{20, 79},// 9
				{10, 89},// 10
				{-1, -1},// JACK
				{22, 77},// QUEEN
				{11, 88} // KING
			},
			
			{//SPADES
				{-1, -1},// filler
				{35, 64},// ACE
				{25, 74},// 2
				{26, 73},// 3
				{15, 84},// 4
				{16, 83},// 5
				{17, 82},// 6
				{ 5, 94},// 7
				{ 6, 93},// 8
				{ 7, 92},// 9
				{ 8, 91},// 10
				{-1, -1},// JACK
				{36, 63},// QUEEN
				{45, 54}// KING
			}
		};

	private Card[] playerHand;
	private Card[] compHand;
	private Card[] masterDeck, currentDeck;
	private Tile[] board;
	private int cardsLeft;
	private int currentPlayer;
	private int selectedCardIndex;
	private int state;
	private int winner;
	private int mySequences[];
	private Card discard;
	
	public SequenceGame() {
		masterDeck = new Card[104];
		winner = -1;
		int count = 0;
		for (int suit = HEARTS; suit <= SPADES; suit++) {
			for (int val = ACE; val <= KING; val++) {
				masterDeck[count] = new Card(val, suit);
				masterDeck[count + 52] = new Card(val, suit);
				count++;
			}
		}
		state = STATE_SPLASH;
		board = new Tile[100];
		currentDeck = new Card[104];
		playerHand = new Card[5];
		compHand = new Card[5];
		mySequences = new int[]{0, 0};
	}

	public int		getCardsLeft()			{return cardsLeft;}	
	public int		getCurrentPlayer()		{return currentPlayer;}
	public int		getSelectedCardIndex()	{return selectedCardIndex;}
	public int		getState()				{return state;}
	public int		getWinner()				{return winner;}
	public Card		getDiscard()			{return discard;}
	public Card[]	getPlayerHand()			{return playerHand;}
	public Card[]	getComputerHand()		{return playerHand;}
	public Tile[]	getBoard()				{return board;}
	
	public int getCardCount(int id) {
		Card[] temp = null;
		if (id == PLAYER_ID)
			temp = playerHand;
		else
			temp = compHand;
		
		int count = 0;
		for (int i = 0; i < 5; i++) {
			if (temp[i] != null)
				count++;
		}
		return count;
	}

	public void setSelectedCardIndex(int i) {
		if (i < -1 || i >= 5 || (i >= 0 && playerHand[i] == null))
			return;
		selectedCardIndex = i;
	}
	
	public void startGame() {
		cardsLeft = 104;
		discard = null;
		mySequences = new int[]{0, 0};
		
		for (int i = 0; i < 104; i++)
			currentDeck[i] = masterDeck[i];

		for (int i = 0; i < 1000; i++) {
			int n = (int)Math.floor(Math.random() * 104.0);
			int m = (int)Math.floor(Math.random() * 104.0);
			Card temp = currentDeck[n];
			currentDeck[n] = currentDeck[m];
			currentDeck[m] = temp;
		}
		
		for (int i = 0; i < 5; i++) {
			playerHand[i] = drawCard();
		}
		
		for (int i = 0; i < 5; i++) {
			compHand[i] = drawCard();
		}
	
		board = new Tile[100];
		currentPlayer = PLAYER_ID;
		selectedCardIndex = -1;
		state = STATE_PLAYER_TURN;
	}
	
	private Card drawCard() {
		int n = (int)Math.floor(Math.random() * 104.0);
		int count = 0;
		while (currentDeck[n] == null && count < 104) {
			n = (n+1) % 104;
			count++;
		}
		Card temp = currentDeck[n];
		currentDeck[n] = null;
		
		if (temp != null)
			cardsLeft--;
		return temp;
	}
	
	public boolean replaceDeadCard() {
		if (state != STATE_PLAYER_TURN || currentPlayer != PLAYER_ID || selectedCardIndex == -1)
			return false;
		discard = playerHand[selectedCardIndex];
		playerHand[selectedCardIndex] = drawCard();
		adjustHand(PLAYER_ID);
		if (getCardCount(PLAYER_ID) == 0) {
			winner = COMPUTER_ID;
			state = STATE_GAME_OVER;
		}
		selectedCardIndex = -1;
		return true;
	}
	
	private void adjustHand(int id) {
		Card[] hand = (id == PLAYER_ID ? playerHand : compHand);
		
		for (int i = 0; i < 4; i++) {
			if (hand[i] == null) {
				int last = -1;
				for (int j = i + 1; j < 5; j++) {
					if (hand[j] != null) {
						last = j;
						break;
					}
				}
				if (last == -1)
					return;
				
				hand[i] = hand[last];
				hand[last] = null;
			}
		}
	}

	public boolean selectTile(int boardNum) {
		// boolean indicates didWin
		if (boardNum < 1 || boardNum > 98 || boardNum == 9 || boardNum == 90)
			return false;
		
		if (playerHand[selectedCardIndex].getValue() != JACK) {
			// not a jack
			int temp[] = BOARD_POS[playerHand[selectedCardIndex].getSuit()][playerHand[selectedCardIndex].getValue()];
			if ((temp[0] == boardNum || temp[1] == boardNum) && board[boardNum] == null) {
				if (layTile(boardNum, PLAYER_ID)) {
					discard = playerHand[selectedCardIndex];
					adjustHand(PLAYER_ID);
					selectedCardIndex = -1;
					winner = PLAYER_ID;
					state = STATE_GAME_OVER;
				} else {
					discard = playerHand[selectedCardIndex];
					playerHand[selectedCardIndex] = drawCard();
					adjustHand(PLAYER_ID);
					if (getCardCount(PLAYER_ID) == 0) {
						winner = COMPUTER_ID;
						state = STATE_GAME_OVER;
					}
					selectedCardIndex = -1;
				}
				return true;
			}
		} else {
			// is a jack
			if (board[boardNum] == null) {
				if (layTile(boardNum, PLAYER_ID)) {
					discard = playerHand[selectedCardIndex];
					adjustHand(PLAYER_ID);
					selectedCardIndex = -1;
					winner = PLAYER_ID;
					state = STATE_GAME_OVER;
				} else {
					discard = playerHand[selectedCardIndex];
					playerHand[selectedCardIndex] = drawCard();
					adjustHand(PLAYER_ID);
					if (getCardCount(PLAYER_ID) == 0) {
						winner = COMPUTER_ID;
						state = STATE_GAME_OVER;
					}
					selectedCardIndex = -1;
				}
				return true;
			} else if (board[boardNum].tile == COMPUTER_ID && board[boardNum].movable) {
				// remove a card
				board[boardNum] = null;
				discard = playerHand[selectedCardIndex];
				playerHand[selectedCardIndex] = drawCard();
				adjustHand(PLAYER_ID);
				if (getCardCount(PLAYER_ID) == 0) {
					winner = COMPUTER_ID;
					state = STATE_GAME_OVER;
				}
				selectedCardIndex = -1;
				return true;
			}
		}
		return false;
	}	
	
	private boolean layTile(int boardNum, int id) {
		board[boardNum] = new Tile(true, id);
		int firsts[] = new int[]{boardNum, boardNum, boardNum, boardNum};
		int lasts[] = new int[]{boardNum, boardNum, boardNum, boardNum};
		
		int r = boardNum / 10;
		int c = boardNum % 10;

		final int rinc[] = new int[]{0, 1, 1, -1};
		final int cinc[] = new int[]{1, 0, 1, 1};
		int inarow[] = new int[]{0, 0, 0, 0};
		
		for (int i = 0; i < 4; i++) {
			int oldwhich = 0;
			for (int j = 1; ((r + j * rinc[i]) >= 0 && (r + j * rinc[i]) < 10 && (c + j * cinc[i]) >= 0 && (c + j * cinc[i] < 10)); j++) {
				int btemp = 10 * (r + j * rinc[i]) + (c + j * cinc[i]);
				if (board[btemp] != null && board[btemp].tile == id && (oldwhich & board[btemp].which) == 0) {
					inarow[i]++;
					lasts[i] = btemp;
					oldwhich = board[btemp].which;
				} else if (isCorner(btemp)) {
					inarow[i]++;
					lasts[i] = btemp;
					oldwhich = 0;
				} else
					break;
			}

			for (int j = -1; ((r + j * rinc[i]) >= 0 && (r + j * rinc[i]) < 10 && (c + j * cinc[i]) >= 0 && (c + j * cinc[i] < 10)); j--) {
				int btemp = 10 * (r + j * rinc[i]) + (c + j * cinc[i]);
				if (board[btemp] != null && board[btemp].tile == id && (oldwhich & board[btemp].which) == 0) {
					inarow[i]++;
					firsts[i] = btemp;
					oldwhich = board[btemp].which;
				} else if (isCorner(btemp)) {
					inarow[i]++;
					firsts[i] = btemp;
					oldwhich = 0;
				} else
					break;
			}
		}
		
		for (int i = 0; i < 4; i++) {
			if (mySequences[id] >= 3)
				break;
			if (inarow[i] >= 4) {
				// got a sequence
				int fr = firsts[i] / 10;
				int fc = firsts[i] % 10;
				
				for (int j = 0; j < 5; j++) {
					int btemp = 10 * (fr + j * rinc[i]) + (fc + j * cinc[i]);
					if (board[btemp] != null) {
						board[btemp].movable = false;
						board[btemp].which |= Tile.MASKS[mySequences[id]];
					}
				}
				mySequences[id]++;
			}
		}
		
		return (mySequences[id] >= 3);
	}
	
	private boolean isCorner(int b) {
      return (b == 0 || b == 9 || b == 90 || b == 99);
   }
	
	private int gainWeight(int boardNum) {
		int inarow[] = new int[]{0, 0, 0, 0};
		// horiz, vert, main diag, other diag
		final int rinc[] = new int[]{0, 1, 1, -1};
		final int cinc[] = new int[]{1, 0, 1, 1};
		int firsts[] = new int[]{boardNum, boardNum, boardNum, boardNum};
		int lasts[] = new int[]{boardNum, boardNum, boardNum, boardNum};
		
		int r = boardNum / 10;
		int c = boardNum % 10;
		
		for (int i = 0; i < 4; i++) {
			int oldwhich = 0;
			for (int j = 1; ((r + j * rinc[i]) >= 0 && (r + j * rinc[i]) < 10 && (c + j * cinc[i]) >= 0 && (c + j * cinc[i]) < 10); j++) {
				int btemp = 10 * (r + j * rinc[i]) + (c + j * cinc[i]);
				if (board[btemp] != null && board[btemp].tile == COMPUTER_ID && (oldwhich&board[btemp].which) == 0) {
					inarow[i]++;
					lasts[i] = btemp;
					oldwhich = board[btemp].which;
				} else if (isCorner(btemp)) {
					inarow[i]++;
					lasts[i] = btemp;
					oldwhich = 0;
				} else
					break;
			}
			oldwhich = 0;
			for (int j = -1; ((r + j * rinc[i]) >= 0 && (r + j * rinc[i]) < 10 && (c + j * cinc[i]) >= 0 && (c + j * cinc[i]) < 10); j--) {
				int btemp = 10 * (r + j * rinc[i]) + (c + j * cinc[i]);
				if (board[btemp] != null && board[btemp].tile == COMPUTER_ID && (oldwhich&board[btemp].which) == 0) {
					inarow[i]++;
					firsts[i] = btemp;
					oldwhich = board[btemp].which;
				} else if (isCorner(btemp)) {
					inarow[i]++;
					firsts[i] = btemp;
					oldwhich = 0;
				} else
					break;
			}
		}
		
		int result = 0;
		
		for (int i = 0; i < 4; i++) {
			if (inarow[i] >= 4) {
				// can make a 5+ with this play
				if (mySequences[COMPUTER_ID] == 2)
					result += 1000;
				else
					result += 10;
			} else if (inarow[i] == 3) {
				// can make a 4 with this play
				int fr = firsts[i] / 10;
				int fc = firsts[i] % 10;
				int lr = lasts[i] / 10;
				int lc = lasts[i] % 10;
				
				fr -= rinc[i];
				fc -= cinc[i];
				lr += rinc[i];
				lc += cinc[i];
				
				if (fr >= 0 && fr < 10 && fc >= 0 && fc < 10 && board[10 * fr + fc] == null) {
					// could later make a 5
					result += 4;
				} else if (lr >= 0 && lr < 10 && lc >= 0 && lc < 10 && board[10 * lr + lc] == null) {
					result += 4;
				}
			} else 
				result += inarow[i];
		}
		return result;
	}
	
	private int blockWeight(int boardNum) {
		int inarow[] = new int[]{0, 0, 0, 0};
		// horiz, vert, main diag, other diag
		final int rinc[] = new int[]{0, 1, 1, -1};
		final int cinc[] = new int[]{1, 0, 1, 1};
		
		int r = boardNum / 10;
		int c = boardNum % 10;
		
		for (int i = 0; i < 4; i++) {
			for (int j = 1; ((r + j * rinc[i]) >= 0 && (r + j * rinc[i]) < 10 && (c + j * cinc[i]) >= 0 && (c + j * cinc[i]) < 10); j++) {
				int btemp = 10 * (r + j * rinc[i]) + (c + j * cinc[i]);
				if ((board[btemp] != null && board[btemp].tile == PLAYER_ID) || isCorner(btemp))
					inarow[i]++;
				else
					break;
			}
			
			for (int j = -1; ((r + j * rinc[i]) >= 0 && (r + j * rinc[i]) < 10 && (c + j * cinc[i]) >= 0 && (c + j * cinc[i]) < 10); j--) {
				int btemp = 10 * (r + j * rinc[i]) + (c + j * cinc[i]);
				if ((board[btemp] != null && board[btemp].tile == PLAYER_ID) || isCorner(btemp))
					inarow[i]++;
				else
					break;
			}
		}
		
		int result = 0;
		for (int i = 0; i < 4; i++) {
			if (inarow[i] == 3) {
				result++;
			} else if (inarow[i] == 4) {
				result += 4 * (mySequences[PLAYER_ID] / 2 + 1);
			}
		}
		
		return result;
	}

	public void computerTurn() {
		if (state == STATE_GAME_OVER)
			return;
		
		currentPlayer = COMPUTER_ID;
		state = STATE_COMPUTER_TURN;
		
		boolean gotone = true;
		while (gotone) {
			gotone = false;
			
			for (int i = 0; i < 5; i++) {
				if (compHand[i] != null && compHand[i].getValue() != JACK) {
					int temp[] = BOARD_POS[compHand[i].getSuit()][compHand[i].getValue()];
					if (board[temp[0]] != null && board[temp[1]] != null) {
						// dead card
						compHand[i] = drawCard();
						adjustHand(COMPUTER_ID);
						gotone = true;
					}
				}
			}
		}
		
		int bestcard = -1;
		int boardnum = -1;
		int maxvalue = -1;
		
		for (int i = 0; i < 5; i++) {
			if (compHand[i] != null && compHand[i].getValue() != JACK) {
				int temp[] = BOARD_POS[compHand[i].getSuit()][compHand[i].getValue()];
				if (board[temp[0]] == null) {
					int value = gainWeight(temp[0]) + blockWeight(temp[0]);

					if (value > maxvalue) {
						bestcard = i;
						boardnum = temp[0];
						maxvalue = value;
					}
				}

				if (board[temp[1]] == null) {
					int value = gainWeight(temp[1]) + blockWeight(temp[1]);

					if (value > maxvalue) {
						bestcard = i;
						boardnum = temp[1];
						maxvalue = value;
					}
				}
			}
		}
		// got best nonjack
		
		for (int i = 0; i < 5; i++) {
			if (compHand[i]!=null && compHand[i].getValue() == JACK) {
				for (int b = 1; b < 99; b++) {
					if (board[b] == null && !isCorner(b)) {
						int value = gainWeight(b) + blockWeight(b) - 2;
						
						if (value > maxvalue) {
							bestcard = i;
							boardnum = b;
							maxvalue = value;
						}
					}
				}
				
				for (int b = 1; b < 99; b++) {
					if (board[b] != null && board[b].tile == PLAYER_ID && board[b].movable) {
						int value = gainWeight(b) + blockWeight(b) - 2;
						
						if (value > maxvalue) {
							bestcard = i;
							boardnum = b;
							maxvalue = value;
						}
					}
				}
				
				break;
			}
		}

		if (bestcard == -1) {
			winner = PLAYER_ID;
			state = STATE_GAME_OVER;
			return;
		}

		discard = compHand[bestcard];
		if (board[boardnum] != null) {
			board[boardnum] = null;
		} else if (layTile(boardnum, COMPUTER_ID)) {
			winner = COMPUTER_ID;
			state = STATE_GAME_OVER;
			return;
		}

		compHand[bestcard] = drawCard();
		adjustHand(COMPUTER_ID);
		currentPlayer = PLAYER_ID;
		state = STATE_PLAYER_TURN;
	}
}
