package rummy.computer;

import rummy.core.Card;
import rummy.core.Hand;
import rummy.core.Card.Face;
import rummy.core.Card.Suit;
import rummy.core.Deck;
import rummy.parts.PartsBuilder;

/**
 * Runs a simulation of a single computer player drawing cards to try and form a winning hand.
 */
public class SingleSimulationMain {

  public static void main(String[] args) {
    Hand hand = new Hand(
        Card.build(Face.TWO, Suit.HEARTS),
        Card.build(Face.THREE, Suit.HEARTS),
        Card.build(Face.FOUR, Suit.HEARTS),
        Card.build(Face.FIVE, Suit.HEARTS),
        Card.build(Face.SEVEN, Suit.HEARTS),
        Card.build(Face.SEVEN, Suit.SPADES),
        Card.build(Face.SEVEN, Suit.CLUBS),
        Card.build(Face.EIGHT, Suit.HEARTS),
        Card.build(Face.NINE, Suit.DIAMONDS),
        Card.build(Face.TEN, Suit.DIAMONDS),
        Card.build(Face.KING, Suit.HEARTS),
        Card.build(Face.KING, Suit.SPADES),
        Card.build(Face.KING, Suit.CLUBS)
    );

    System.out.println(hand);
    System.out.println(new PartsBuilder().buildParts(hand));

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