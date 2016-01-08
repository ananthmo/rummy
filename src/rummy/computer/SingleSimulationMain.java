package rummy.computer;

import rummy.core.Card;
import rummy.core.Deck;

/**
 * Runs a simulation of a single computer player drawing cards to try and form a winning hand.
 */
public class SingleSimulationMain {

  public static void main(String[] args) {
    Computer comp = new Computer();
    Deck deck = new Deck(2);
    deck.shuffle();
    comp.drawNewHand(deck);

    for (int i = 0; i < 100; i++) {
      if (deck.empty()) {
        System.out.println("DECK EXHAUSTED");
        break;
      }

      System.out.println(i + ". hand:" + comp.hand + ", score:" + comp.currentHandScore);
      Card newCard = deck.draw();
      System.out.println("Stack:" + newCard);
      if (comp.drawAndDiscard(newCard) == null) {
        System.out.println("WINNER:" + comp.hand + " " + comp.currentHandScore);
        break;
      }
    }
  }
}
