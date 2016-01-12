package rummy.core;

import java.util.Random;
import java.util.Stack;

import rummy.core.Card.Face;
import rummy.core.Card.Suit;

public class Deck {

  private static final int NUM_SHUFFLE_SWAPS = 1000;

  private final Random random;
  private final Stack<Card> cards;
  private final Stack<Card> discards;

  /**
   * Constructs a deck of 52-cards, one card of each face/suit pair.
   */
  public Deck(int numDecks, int numJokers, Integer seed) {
    this.random = (seed != null) ? new Random(seed) : new Random();
    this.cards = new Stack<>();
    this.discards = new Stack<>();
    for (int i = 0; i < numDecks; i++){
      for (Suit suit : Suit.SUITS) {
        for (Face face: Face.FACES) {
          cards.add(new Card(face, suit, i));
        }
      }
    }
    for (int i = 0; i < numJokers; i++) {
      cards.add(new Card(1 + i));
    }
  }

  public Deck(int numDecks, int numJokers) {
    this(numDecks, numJokers, null /* seed */);
  }

  public Card draw() {
    return cards.pop();
  }

  public boolean empty() {
    return cards.empty();
  }

  public void addToDiscard(Card card) {
    discards.add(card);
  }

  public void reshuffleDiscardPile() {
    while(!discards.isEmpty()) {
      cards.add(discards.pop());
    }
    shuffle();
  }

  public void shuffle() {
    int numCards = cards.size();

    for (int i = 0; i < NUM_SHUFFLE_SWAPS; i++) {
      int c1idx = random.nextInt(numCards);
      int c2idx = random.nextInt(numCards);
      Card temp = cards.get(c1idx);
      cards.set(c1idx, cards.get(c2idx));
      cards.set(c2idx, temp);
    }
  }
}
