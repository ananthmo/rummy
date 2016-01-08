package rummy.parts;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import rummy.core.Card;
import rummy.core.Card.Face;
import rummy.core.Hand;

/**
 * Constructs a list of Parts from a rummy hand. These parts then are arranged to form a winning
 * hand, or to choose which card to discard to increase chance of winning.
 */
public class PartsBuilder {

  private static final Comparator<Card> COMPARE_BY_VALUE = new Comparator<Card>() {
    @Override
    public int compare(Card c1, Card c2) {
      return c1.value - c2.value;
    }
  };

  private static final Comparator<Card> COMPARE_BY_FACE = new Comparator<Card>() {
    @Override
    public int compare(Card c1, Card c2) {
      return c1.face.ordinal() - c2.face.ordinal();
    }
  };

  // TODO: support jokers
  private int numJokers = 0;
  List<Card> jokers = new ArrayList<>();

  // Tokenizes a hand of cards into a list of Parts (eg Rummys, Sets, partial sets, etc).
  public List<Part> buildParts(Hand hand) {
    List<Card> cards = new ArrayList<>(removeJokers(hand.cards));

    List<Part> parts = new ArrayList<>();
    parts.addAll(findSingleParts(cards));
    parts.addAll(findRummyParts(cards));
    parts.addAll(findSetParts(cards));
    return parts;
  }

  private List<Card> removeJokers(List<Card> cards) {
    List<Card> cardsNoJ = new ArrayList<>();
    for (Card card : cards) {
      if (!card.isJoker()) {
        cardsNoJ.add(card);
      } else {
        jokers.add(card);
        numJokers++;
      }
    }
    return cardsNoJ;
  }

  /**
   * Finds the Rummy-related partTypes for this hand of cards. It sorts the cards by value, which
   * sorts by suit then face. Iterates through the list, maintaining a growing run list which is
   * reset once broken. Explicitly need to check for Q-K-A runs as those will not be found in the
   * sort order sequence.
   */
  List<Part> findRummyParts(List<Card> cards) {
    List<Part> parts = new ArrayList<>();
    cards.sort(COMPARE_BY_VALUE);

    Card prev = null;
    List<Card> run = new ArrayList<>();
    List<Card> qkaRun = new ArrayList<>();
    for (int i = 0; i < cards.size(); i++) {
      Card card = cards.get(i);
      if (prev != null
          && (card.suit != prev.suit || card.face.ordinal() != prev.face.ordinal() + 1)) {
        if (run.size() == 2 && jokers.size() > 0) {
          parts.add(Part.rummyWithJoker(run, jokers.get(0)));
          if (jokers.size() > 1) {
            parts.add(Part.rummyWithJoker(run, jokers.get(0), jokers.get(1)));
          }
        }

        run.clear();
        if (card.suit != prev.suit) {
          qkaRun.clear();
        }
      }

      // Check for wrapping Q-K-A runs
      if (card.face == Face.ACE || card.face == Face.KING || card.face == Face.QUEEN) {
        qkaRun.add(card);
        if (qkaRun.size() == 3) {
          parts.add(Part.naturalRummy(qkaRun));
        }
      }

      run.add(card);
      if (run.size() == 3 || run.size() == 4) {
        parts.add(Part.naturalRummy(run));
        for (Card joker : jokers) {
          parts.add(Part.rummyWithJoker(run, joker));
        }
        if (run.size() == 3 && jokers.size() == 2) {
          parts.add(Part.rummyWithJoker(run, jokers.get(0), jokers.get(1)));
        }
      } else if (run.size() == 2) { // Note: only adds the first 2 cards in the sequence
        parts.add(Part.partialRummy(run));
      }

      prev = card;
    }

    return parts;
  }

  /**
   * Returns each card as a SINGLE part.
   */
  List<Part> findSingleParts(List<Card> cards) {
    List<Part> parts = new ArrayList<>(cards.size());
    for (Card card : cards) {
      parts.add(Part.single(card));
    }
    for (Card joker : jokers) {
      parts.add(Part.single(joker));
    }
    return parts;
  }

  /**
   * Finds the set-related part types of this hand of cards. Sorts the cards by face, then iterates
   * through, keeping a running list of cards with same face value.
   */
  List<Part> findSetParts(List<Card> cards) {
    cards.sort(COMPARE_BY_FACE);
    List<Part> parts = new ArrayList<>();
    List<Card> run = new ArrayList<>();
    Card prev = null;
    for (int i = 0; i < cards.size(); i++) {
      Card card = cards.get(i);

      if (prev != null && card.face != prev.face) {
        if (run.size() == 2 && jokers.size() > 0) {
          for (Card joker : jokers) {
            parts.add(Part.setWithJoker(run, joker));
          }
          if (jokers.size() > 1) {
            parts.add(Part.setWithJoker(run, jokers.get(0), jokers.get(1)));
          }
        }

        run.clear();
      }

      run.add(card);
      if (run.size() == 3 || run.size() == 4) {
        parts.add(Part.set(run));
        for (Card joker : jokers) {
          parts.add(Part.setWithJoker(run, joker));
        }
      } else if (run.size() == 2) { // Note: only adds first 2 cards of group
        parts.add(Part.partialSet(run));
      }

      prev = card;
    }
    if (run.size() == 2 && jokers.size() > 0) {
      for (Card joker : jokers) {
        parts.add(Part.setWithJoker(run, joker));
      }
      if (jokers.size() > 1) {
        parts.add(Part.setWithJoker(run, jokers.get(0), jokers.get(1)));
      }
    }
    return parts;
  }
}
