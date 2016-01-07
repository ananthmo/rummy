package rummy.core;

import java.util.Random;
import java.util.Stack;

import rummy.core.Card.Face;
import rummy.core.Card.Suit;

public class Deck {

  final Stack<Card> cards;

  public Deck() {
    this.cards = new Stack<>();
    for (Suit suit : Suit.suits()) {
      for (Face face: Face.faces()) {
        cards.add(new Card(face, suit));
      }
    }
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
    int numSwaps = 1000;
    int numCards = cards.size();
    Random rand = new Random();

    for (int i = 0; i < numSwaps; i++) {
      int c1idx = rand.nextInt(numCards);
      int c2idx = rand.nextInt(numCards);
      Card temp = cards.get(c1idx);
      cards.set(c1idx, cards.get(c2idx));
      cards.set(c2idx, temp);
    }
  }
}
