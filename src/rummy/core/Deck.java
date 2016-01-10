package rummy.core;

import java.util.Random;
import java.util.Stack;

import rummy.core.Card.Face;
import rummy.core.Card.Suit;

public class Deck {

  private static final int NUM_SHUFFLE_SWAPS = 1000;

  private final Stack<Card> cards;

  /**
   * Constructs a deck of 52-cards, one card of each face/suit pair.
   */
  public Deck(int numDecks, int numJokers) {
    this.cards = new Stack<>();
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

  public Card draw() {
    return cards.pop();
  }

  public boolean empty() {
    return cards.empty();
  }

  public void shuffle() {
    int numCards = cards.size();
    Random rand = new Random(9);

    for (int i = 0; i < NUM_SHUFFLE_SWAPS; i++) {
      int c1idx = rand.nextInt(numCards);
      int c2idx = rand.nextInt(numCards);
      Card temp = cards.get(c1idx);
      cards.set(c1idx, cards.get(c2idx));
      cards.set(c2idx, temp);
    }
  }
}
