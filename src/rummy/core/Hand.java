package rummy.core;

import java.util.ArrayList;

public class Hand {

  public final ArrayList<Card> cards = new ArrayList<>();

  public Hand(Card... cards) {
    for (Card card : cards) {
      this.cards.add(card);
    }
  }

  public Hand(Hand hand) {
    this(hand.cards.toArray(new Card[hand.cards.size()]));
  }

  public String toString() {
    String result = "";
    for (int i = 0; i < cards.size(); i++) {
      if (i > 0) {
        result += " ";
      }
      result += cards.get(i);
    }
    return result;
  }
}
