package sequence;

public class Card {
   public static enum Suit {
      Hearts(0),
         Clubs(1),
         Diamonds(2),
         Spades(3);
      private final int value;
      private Suit(int value) {
         this.value = value;
      }
      public int getValue() {
         return this.value;
      }
   }

   public static final int ACE = 1;
   public static final int JACK = 11;
   public static final int QUEEN = 12;
   public static final int KING = 13;

   public final int value;
	public final Suit suit;
	
	public Card(int value, Suit suit) {
      if (!(ACE <= value && value <= KING))
         throw new IllegalArgumentException("value");
      if (suit == null)
         throw new IllegalArgumentException("suit");
		this.value = value;
		this.suit = suit;
	}
	
	public int getValue() {return value;}
	public Suit getSuit() {return suit;}
   public int hashCode() {
      return this.suit.hashCode()*3 + this.value*7;
   }
   public boolean equals(Object o) {
      if (!(o instanceof Card))
         return false;
      Card c = (Card)o;
      return this.value == c.value && this.suit.equals(c.suit);
   }
}
