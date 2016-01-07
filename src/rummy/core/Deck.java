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
  public Deck() {
    this.cards = new Stack<>();
    for (Suit suit : Suit.SUITS) {
      for (Face face: Face.FACES) {
        cards.add(new Card(face, suit));
      }
    }
    // TODO: support jokers
    //cards.add(new Card(true));
    //cards.add(new Card(true));
  }

  public Card draw() {
    return cards.pop();
  }

  public boolean empty() {
    return cards.empty();
  }

  public void shuffle() {
    int numCards = cards.size();
    Random rand = new Random();

    for (int i = 0; i < NUM_SHUFFLE_SWAPS; i++) {
      int c1idx = rand.nextInt(numCards);
      int c2idx = rand.nextInt(numCards);
      Card temp = cards.get(c1idx);
      cards.set(c1idx, cards.get(c2idx));
      cards.set(c2idx, temp);
    }
  }
}
