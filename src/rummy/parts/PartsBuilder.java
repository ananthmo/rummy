package rummy.parts;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

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

  private List<Card> jokers = new ArrayList<>();

  // Tokenizes a hand of cards into a list of Parts (eg Rummys, Sets, partial sets, etc).
  public List<Part> buildParts(Hand hand) {
    List<Card> cards = new ArrayList<>(registerAndRemoveJokers(hand.cards));

    List<Part> parts = new ArrayList<>();
    parts.addAll(findSingleParts(cards));
    parts.addAll(findRummyParts(cards));
    parts.addAll(findSetParts(cards));
    return parts;
  }

  private List<Card> registerAndRemoveJokers(List<Card> cards) {
    List<Card> cardsNoJ = new ArrayList<>();
    for (Card card : cards) {
      if (!card.isJoker()) {
        cardsNoJ.add(card);
      } else {
        jokers.add(card);
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

    Map<Face, Stack<Card>> qkaStacks = new HashMap<>();
    qkaStacks.put(Face.ACE, new Stack<>());
    qkaStacks.put(Face.KING, new Stack<>());
    qkaStacks.put(Face.QUEEN, new Stack<>());

    Card prev = null;
    List<List<Card>> cardSets = new ArrayList<>();
    for (int i = 0; i < cards.size(); i++) {
      Card card = cards.get(i);
      if (prev != null
          && (card.suit != prev.suit || card.face.ordinal() != prev.face.ordinal() + 1)
          && (card.value != prev.value)) {
        List<List<Card>> setRuns = expandCardSets(cardSets);
        parts.addAll(rummyRunsToParts(setRuns));

        cardSets.clear();
        if (card.suit != prev.suit) {
          // Reset the QKA stacks only on suit change
          for (Stack<Card> stack : qkaStacks.values()) {
            stack.clear();
          }
        }
      }

      // Check for wrapping Q-K-A runs
      if (card.face == Face.ACE || card.face == Face.KING || card.face == Face.QUEEN) {
        qkaStacks.get(card.face).add(card);
        if (!qkaStacks.get(Face.ACE).isEmpty()
            && !qkaStacks.get(Face.KING).isEmpty()
            && !qkaStacks.get(Face.QUEEN).isEmpty()) {
          List<Card> qkaRun = new ArrayList<Card>();
          qkaRun.add(qkaStacks.get(Face.ACE).pop());
          qkaRun.add(qkaStacks.get(Face.KING).pop());
          qkaRun.add(qkaStacks.get(Face.QUEEN).pop());
          parts.add(Part.naturalRummy(qkaRun));
        }
      }

      if (prev != null && card.value == prev.value && !cardSets.isEmpty()) {
        cardSets.get(cardSets.size() - 1).add(card);
      } else {
        cardSets.add(new ArrayList<>());
        cardSets.get(cardSets.size() - 1).add(card);
      }

      prev = card;
    }

    return parts;
  }

  private static List<List<Card>> expandCardSets(List<List<Card>> cardSets) {
    //System.out.println("input:" + cardSets);
    List<List<Card>> runs = new ArrayList<>();
    List<List<Card>> selectedRuns = new ArrayList<>();
    for (List<Card> cardSet : cardSets) {
      if (runs.isEmpty()) {
        // Initialize run to first card set
        for (Card card : cardSet) {
          List<Card> singleCard = new ArrayList<Card>();
          singleCard.add(card);
          runs.add(singleCard);
        }
      } else {
        // Multiply the previous run by this card set
        List<List<Card>> expandedRuns = new ArrayList<>();
        for (List<Card> run : runs) {
          for (Card card : cardSet) {
            List<Card> newRun = new ArrayList<>();
            newRun.addAll(run);
            newRun.add(card);
            expandedRuns.add(newRun);
            selectedRuns.add(newRun);
          }
        }
        runs = expandedRuns;
      }
    }
    //System.out.println("returning cardSets:" + selectedRuns);
    return selectedRuns;
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
    List<List<Card>> runCardSets = new ArrayList<>();
    Card prev = null;
    for (int i = 0; i < cards.size(); i++) {
      Card card = cards.get(i);

      if (prev != null && card.face != prev.face && card.value != prev.value) {
        List<List<Card>> setRuns = expandCardSets(runCardSets);
        parts.addAll(setRunsToParts(setRuns));
        runCardSets.clear();
      }

      if (prev != null && card.value == prev.value) {
        runCardSets.get(runCardSets.size() - 1).add(card);
      } else {
        runCardSets.add(new ArrayList<>());
        runCardSets.get(runCardSets.size() - 1).add(card);
      }

      prev = card;
    }
    List<List<Card>> setRuns = expandCardSets(runCardSets);
    parts.addAll(setRunsToParts(setRuns));
    return parts;
  }

  List<Part> setRunsToParts(List<List<Card>> sets) {
    List<Part> parts = new ArrayList<>();
    for (List<Card> run : sets) {
      if (run.size() >= 3) {
        parts.add(Part.set(run));
        if (run.size() == 3) {
          for (Card joker : jokers) {
            parts.add(Part.setWithJoker(run, joker));
          }
        }
      } else if (run.size() == 2) {
        parts.add(Part.partialSet(run));
        for (Card joker : jokers) {
          parts.add(Part.setWithJoker(run, joker));
        }
        if (jokers.size() > 1) {
          parts.add(Part.setWithJoker(run, jokers.get(0), jokers.get(1)));
        }
      }
    }
    return parts;
  }

  List<Part> rummyRunsToParts(List<List<Card>> rummyRuns) {
    List<Part> parts = new ArrayList<>();
    for (List<Card> run : rummyRuns) {
      if (run.size() >= 3) {
        parts.add(Part.naturalRummy(run));
        if (run.size() == 3) {
          for (Card joker : jokers) {
            parts.add(Part.rummyWithJoker(run, joker));
          }
        }
      } else if (run.size() == 2) {
        parts.add(Part.partialRummy(run));
        for (Card joker : jokers) {
          parts.add(Part.rummyWithJoker(run, joker));
        }
        if (jokers.size() > 1) {
          parts.add(Part.rummyWithJoker(run, jokers.get(0), jokers.get(1)));
        }
      }
    }
    return parts;
  }
}
