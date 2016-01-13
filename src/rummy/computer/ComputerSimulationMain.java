package rummy.computer;

import java.util.ArrayList;
import java.util.List;

import rummy.computer.Computer.PickupResult;
import rummy.core.Card;
import rummy.core.Card.Face;
import rummy.scorer.ScorerFactory;
import rummy.core.Deck;

/**
 * Have a set of computers play a game of rummy against each other.
 */
public class ComputerSimulationMain {

  public static void main(String args[]) {
    int numComputers = 2;
    int numDecks = 2;
    int numJokers = 4;

    Deck deck = new Deck(numDecks, numJokers);
    deck.shuffle();
    Face faceJoker = deck.draw().face;
    System.out.println("Face joker: " + faceJoker);

    List<Computer> computers = new ArrayList<>(numComputers);
    for (int i = 0; i < numComputers; i++) {
      Computer computer =
          new Computer(faceJoker, i == 0 ? ScorerFactory.COMPLEX : ScorerFactory.SIMPLE);
      computer.drawNewHand(deck);
      computers.add(computer);
    }

    Card top = deck.draw();
    int turn = 1;
    while (true) {
      for (int i = 0; i < computers.size(); i++) {
        if (deck.empty()) {
          deck.reshuffleDiscardPile();
          System.out.println("RESHUFFLING !!");
        }

        Computer computer = computers.get(i);
        System.out.println(
            "T" + turn++ + " Computer: " + (i + 1) + ": " + computer.hand + " top:" + top);

        PickupResult pickupResult = computer.checkPickup(top);
        if (pickupResult.keepCard) {
          top = pickupResult.freeCard;
          System.out.println("drew top, discared " + top);
        } else {
          deck.addToDiscard(top);
          Card deckCard = deck.draw();
          top = computer.drawAndDiscard(deckCard);
          System.out.println("drew from deck " + deckCard + ", discarded " + top);
        }

        if (top == null) {
          break;
        }
      }
      if (top == null) {
        break;
      }
    }

    System.out.println();
    System.out.println("winner!");
    for (int i = 0; i < computers.size() ; i++) {
      Computer computer = computers.get(i);
      System.out.println(
          "computer " + (i + 1) + ": " + computer.hand + " " + computer.currentPoints);
    }
  }
}
