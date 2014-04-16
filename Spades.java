import java.awt.*;

public class Spades {
	
	//set up variables for use by whole program
	public static final Color BACKGROUND_GREEN       = new Color(  10,  150, 70);
	static int XWIDTH = 800;
	static int YWIDTH = 800;
	static int DEAL_RATE = 50;
	static Deck deck;
	static String[] suits = {"diamonds", "clubs", "hearts", "spades"};
	static String[] rankings = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
	static int scoreNS = 0;
	static int bagsNS = 0;
	static int scoreEW = 0;
	static int bagsEW = 0;
	static Deck[] decks = new Deck[4];
	static Card[] cards = new Card[4];
	static int[] taken = new int[4];
	static int[] bids = new int[4];

	public static void main(String[] args) {
		StdDraw.setCanvasSize(XWIDTH, YWIDTH);
		StdDraw.setXscale(0, XWIDTH);
		StdDraw.setYscale(0, YWIDTH);
		welcomeScreen();
		initializeDeck();
		initializeScreen();
		promptUserToBid();
		String dealer = "user";
		
		while (!StdDraw.mousePressed()) {	
			readBid();
		}
		getRidOfBidBox();
		
		while (decks[0].size() > 0) {
		cards[0] = whichCard(dealer);
		cards[1] = compCard(1);
		cards[2] = compCard(2);
		cards[3] = compCard(3);
		moveCardsToCenter();
		//draw();
		//compareCards();
		
		StdDraw.show(DEAL_RATE*25);
		StdDraw.show();
		moveCardsOffScreen();
		}
		StdDraw.setPenColor(StdDraw.WHITE);
		StdDraw.text(XWIDTH/2, YWIDTH/2, "GAME OVER");
		
		
		
	}

	//create a deck of 52 standard playing cards
	//set canvas size and background color
	//create scoreboard
	//create user/computer titles
	//shuffle, separate into 4 13-card decks, and sort those decks
	public static void initializeDeck() {
		deck = new Deck();
		for (int s = 0; s < 4; s++) {
			for (int r = 0; r < 13; r++) {
				deck.addToEnd(new Card(rankings[r], suits[s]));
			}
		}
		
		//shuffle();		
		deal();
		sort(decks[0]);
		sort(decks[1]);
		sort(decks[2]);
		sort(decks[3]);
	}
	
	public static void initializeScreen() {
		StdDraw.clear(BACKGROUND_GREEN);
		StdDraw.setPenColor(StdDraw.BLACK);
		StdDraw.filledRectangle(50, YWIDTH - 10, XWIDTH/10, YWIDTH/20);
		StdDraw.setPenColor(StdDraw.WHITE);
		StdDraw.text(50, YWIDTH + 15, "NORTH/SOUTH: ");
		StdDraw.text(50, YWIDTH, "Score: " + scoreNS + " Bags: " + bagsNS);
		StdDraw.text(50, YWIDTH - 20, "EAST/WEST: ");
		StdDraw.text(50, YWIDTH - 35, "Score: " + scoreEW + " Bags: " + bagsEW);	
		
		StdDraw.text(XWIDTH/2, 100, "South: USER");
		StdDraw.text(XWIDTH/2, 80, "Bid: " + bids[0] + "\t Taken: " + taken[0]);
		StdDraw.text(XWIDTH/2, YWIDTH - 100, "North: COMPUTER 2");
		StdDraw.text(XWIDTH/2, YWIDTH - 80, "Bid: " + bids[2] + "\t Taken: " + taken[2]);
		StdDraw.text(100, YWIDTH/2, "West: COMPUTER 1", 270);
		StdDraw.text(80, YWIDTH/2, "Bid: " + bids[1] + "\t Taken: " + taken[1], 270);
		StdDraw.text(XWIDTH - 100, YWIDTH/2, "East: COMPUTER 3", 90);
		StdDraw.text(XWIDTH - 80, YWIDTH/2, "Bid: " + bids[3] + "\t Taken: " + taken[3], 90);
		
		firstDraw();
	}

	//swap two random cards 1000 times
	public static void shuffle() {
		for (int k = 0; k < 1000; k ++) {
			int i = (int) (Math.random() * 52);
			int j = (int) (Math.random() * 52);

			Card temp = deck.atIndex(i);
			deck.setCard(i, deck.atIndex(j));
			deck.setCard(j, temp);
		}
	}

	//order the cards in the user's deck so that they are looking at an organized hand
	public static void sort(Deck d) {
		//FIRST: sort by suit
		int[] indexCards = {0, 0, 0, 0};
		int index = 0;
		for (int s = 0; s < 4; s++) {
			for (int p = 0; p < d.size(); p++) {
				if(d.peek(p).suit.equals(suits[s])) {
					Card temp = d.atIndex(index);
					d.setCard(index, d.atIndex(p));
					d.setCard(p, temp);
					index++;
					indexCards[s]++;
				}
			}
		}

		//NEXT: sort by number
		index = 0;
		indexCards[1] += indexCards[0];
		indexCards[2] += indexCards[1];
		indexCards[3] += indexCards[2];

		for (int k = 0; k < 4; k ++) {
			for (int r = 0; r < 13; r ++) {
				for (int j = index; j < indexCards[k]; j++) {
					if(d.peek(j).number.equals(rankings[r])) {
						Card temp = d.atIndex(index);
						d.setCard(index, d.atIndex(j));
						d.setCard(j, temp);
						index++;
					}
				}
			}
		}
	}
	
	//place the cards on the screen in the proper positions for each player
	public static void firstDraw() {
		int u = (int) ((XWIDTH - (13*XWIDTH/20))/2);
		int c3 = (int) ((YWIDTH - (13*YWIDTH/20))/2);
		int c2 = (int) (XWIDTH - ((XWIDTH - (13*XWIDTH/20))/2));
		int c1 = (int) (YWIDTH - ((YWIDTH - (13*YWIDTH/20))/2));
		for (int j = 0; j < 13; j++) {
			decks[0].draw(j, u, 20, "user");
			StdDraw.show(DEAL_RATE);
			decks[3].draw(j, YWIDTH - 20, c3, "comp3");
			StdDraw.show(DEAL_RATE);
			decks[2].draw(j, c2, XWIDTH - 20, "comp2");
			StdDraw.show(DEAL_RATE);
			decks[1].draw(j, 20, c1, "comp1");
			StdDraw.show(DEAL_RATE);
			u+= XWIDTH/20;
			c3 += YWIDTH/20;
			c2 -= XWIDTH/20;
			c1 -= YWIDTH/20;
		}
		
		//turns off animation mode so everything else will appear on the screen
		StdDraw.show();
	}
	
	//deals the cards in the randomized deck to each player counter-clockwise
	public static void deal() {
		for (int j = 0; j < 4; j++) {
			decks[j] = new Deck(13);
		}
		for (int i = 0; i < 13; i++) {
			for (int j = 0; j < 4; j++) {
				decks[j].addToEnd(deck.deal());
			}
		}
	}
	
	//displays all possible bid values to user and records how many the user makes
	public static void promptUserToBid() {
		StdDraw.setPenColor(StdDraw.BLACK);
		StdDraw.filledRectangle(XWIDTH/2, 200, XWIDTH/4, 60);
		StdDraw.setPenColor(StdDraw.WHITE);
		StdDraw.text(XWIDTH/2, 245, "BID:");
		String[] bids = {"Nil", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13"};
		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 2; j ++) {
				StdDraw.rectangle(XWIDTH/4 + XWIDTH/28 + (i*XWIDTH/14), YWIDTH/5 + 60 - 40*j, 25, 15);
				StdDraw.text(XWIDTH/4 + XWIDTH/28 + (i*XWIDTH/14), YWIDTH/5 + 60 - 40*j, bids[(7*j)+i]);
			}
		}
		
	}
	
	//IF THE MOUSE IS PRESSED, COMPARE X AND Y MOUSE POSITION TO THE BOX AREA AND DETERMINE WHICH BOX IT IS. THEN SAVE THAT AS THE USER'S BID.
	public static void readBid() {
		if (StdDraw.mousePressed()) {
			double xPos = StdDraw.mouseX();
			double yPos = StdDraw.mouseY();
		}
		
		//TODO: now we want to know which box that xPos and yPos correspond to and save that number as userBid
	}
	
	public static void getRidOfBidBox() {
		StdDraw.setPenColor(BACKGROUND_GREEN);
		StdDraw.filledRectangle(XWIDTH/2, 200, XWIDTH/4 + 20, 80);
	}
	
	//welcomes user to Spades and explains the rules
	//moves on to actual game once user has pressed a specific key
	public static void welcomeScreen() {
		//enter code here
	}
	
	public static Card whichCard(String dealer) {
		//this is hard-coded for now, but will need to be changed to user input
		//by using Std.mousepressed()
		
		Card card = new Card("A", "diamonds");
		
		for (int i = 0; i < decks[0].size(); i++) {
			if (decks[0].peek(i).equals(card)) {
				return decks[0].removeCard(i);
			}
		}
		return decks[0].removeCard(0);
		
	}
	
	public static Card compCard(int comp) {
		switch (comp){
		case 1:
			
			return decks[1].removeCard(0);
			
		case 2:
			return decks[2].removeCard(0);
			
		case 3:
			return decks[3].removeCard(0);
			
		}
		return decks[1].removeCard(0);
		
	}
	
	public static void moveCardsToCenter() {
		int u = (int) ((XWIDTH - (13*XWIDTH/20))/2);
		int c3 = (int) ((YWIDTH - (13*YWIDTH/20))/2);
		int c2 = (int) (XWIDTH - ((XWIDTH - (13*XWIDTH/20))/2));
		int c1 = (int) (YWIDTH - ((YWIDTH - (13*YWIDTH/20))/2));
		cards[0].draw(XWIDTH/2, YWIDTH/2 - 100, 0);
		StdDraw.setPenColor(BACKGROUND_GREEN);
		StdDraw.filledRectangle(XWIDTH/2, 20, XWIDTH/2.5, 50);
		for (int j = 0; j < decks[0].size(); j++) {
			decks[0].draw(j, u, 20, "user");
			u+= XWIDTH/20;
		}
		StdDraw.show(DEAL_RATE*10);
		StdDraw.show();
		
		cards[3].draw(XWIDTH/2 + 100, YWIDTH/2, 3);
		StdDraw.setPenColor(BACKGROUND_GREEN);
		StdDraw.filledRectangle(XWIDTH - 20, YWIDTH/2+20, 50, YWIDTH/2.5);
		for (int j = 0; j < decks[3].size(); j++) {
			decks[3].draw(j, YWIDTH - 20, c3, "comp3");
			c3 += YWIDTH/20;
		}
		StdDraw.show(DEAL_RATE*10);
		StdDraw.show();
		
		cards[2].draw(XWIDTH/2, YWIDTH/2 + 100, 2);
		StdDraw.setPenColor(BACKGROUND_GREEN);
		StdDraw.filledRectangle(XWIDTH/2+50, YWIDTH-20, XWIDTH/2.5, 50);
		for (int j = 0; j < decks[2].size(); j++) {
			decks[2].draw(j, c2, XWIDTH - 20, "comp2");
			c2 -= XWIDTH/20;
		}
		StdDraw.show(DEAL_RATE*10);
		StdDraw.show();
		
		cards[1].draw(XWIDTH/2 - 100, YWIDTH/2, 1);
		StdDraw.setPenColor(BACKGROUND_GREEN);
		StdDraw.filledRectangle(20, YWIDTH/2, 50, YWIDTH/2.5);
		for (int j = 0; j < decks[1].size(); j++) {
			decks[1].draw(j, 20, c1, "comp1");
			c1 -= YWIDTH/20;
		}
		StdDraw.show(DEAL_RATE*10);
		StdDraw.show();
	}
	
	public static void moveCardsOffScreen() {
		for (int i = 0; i < 20; i+=5) {
			StdDraw.picture(XWIDTH - 20 + i, 40, "playing-card.jpg", 60, 84);
		}
		StdDraw.setPenColor(BACKGROUND_GREEN);
		StdDraw.filledRectangle(XWIDTH/2, YWIDTH/2, 200, 200);
	}

	/*public static void draw() {
		int u = (int) ((XWIDTH - (13*XWIDTH/20))/2);
		int c3 = (int) ((YWIDTH - (13*YWIDTH/20))/2);
		int c2 = (int) (XWIDTH - ((XWIDTH - (13*XWIDTH/20))/2));
		int c1 = (int) (YWIDTH - ((YWIDTH - (13*YWIDTH/20))/2));
		//draw a green box, then redraw the deck again without the card that was played
		StdDraw.setPenColor(BACKGROUND_GREEN);
		StdDraw.filledRectangle(XWIDTH/2, 20, XWIDTH/2.5, 50);
		for (int j = 0; j < decks[0].size(); j++) {
			decks[0].draw(j, u, 20, "user");
			u+= XWIDTH/20;
		}
		StdDraw.setPenColor(BACKGROUND_GREEN);
		StdDraw.filledRectangle(XWIDTH - 20, YWIDTH/2+20, 50, YWIDTH/2.5);
		for (int j = 0; j < decks[3].size(); j++) {
			decks[3].draw(j, YWIDTH - 20, c3, "comp3");
			c3 += YWIDTH/20;
		}
		StdDraw.setPenColor(BACKGROUND_GREEN);
		StdDraw.filledRectangle(XWIDTH/2+50, YWIDTH-20, XWIDTH/2.5, 50);
		for (int j = 0; j < decks[2].size(); j++) {
			decks[2].draw(j, c2, XWIDTH - 20, "comp2");
			c2 -= XWIDTH/20;
		}
		StdDraw.setPenColor(BACKGROUND_GREEN);
		StdDraw.filledRectangle(20, YWIDTH/2, 50, YWIDTH/2.5);
		for (int j = 0; j < decks[1].size(); j++) {
			decks[1].draw(j, 20, c1, "comp1");
			c1 -= YWIDTH/20;
		}
	}*/
	
	public static void compareCards() {
		
	}
	
}
