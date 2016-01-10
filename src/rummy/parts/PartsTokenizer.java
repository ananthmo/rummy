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
 * Tokenizes a hand of cards into a list of {@link Part}s. The cards can belong to any number of
 * decks. The created parts can then be processed by a {@link PartsSolver}, to find a combination
 * that forms the best hand (via a scoring metric).
 */
public class PartsTokenizer {

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

  private final List<Card> jokers;

  public PartsTokenizer() {
    this.jokers = new ArrayList<>();
  }

  /**
   * Tokenizes a hand of cards into an exhaustive list of Parts (eg rummys, sets, partial sets,
   * singles, etc).
   */
  public List<Part> tokenize(Hand hand) {
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
   * sorts by suit then face. Iterates through the list, maintaining a growing cardSet list which
   * gets reset once a rummy run is broken. This set is then converted to rummy parts. QKA runs are
   * checked explicitly, as those will not be found in the sort order sequence.
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

      // This rummy run has ended, convert the run sets into parts.
      if (prev != null
          && (card.suit != prev.suit || card.face.ordinal() != prev.face.ordinal() + 1)
          && (card.value != prev.value)) {
        List<List<Card>> rummyRuns = expandCardSets(cardSets);
        parts.addAll(rummyRunsToParts(rummyRuns));

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

      // Add this card to the current card set if the rummy run is growing (eg Set[2H 3H] + 4H),
      // otherwise start a new set.
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

  // Given a list of cardSets, multiplies them in sequence. For instance if the user has a run
  // of 2H 2H 3H 3H 4H, which froms the card set [2Ha 2Hb] [3Ha 3Hb] [4H], it expands to 4 runs
  // [2Ha 3Ha 4H],[2Ha 3Hb 4H],[2Hb 3Ha 4H],[2Hb 3Hb 4H].
  private static List<List<Card>> expandCardSets(List<List<Card>> cardSets) {
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
        // Multiply the previous run by this card set. Multiplication means [a,b] * [c,d,e] =
        // [ac,ad,ae,bc,bd,be].
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

      // This set run as ended, convert the run into set parts.
      if (prev != null && card.face != prev.face && card.value != prev.value) {
        List<List<Card>> setRuns = expandCardSets(runCardSets);
        parts.addAll(setRunsToParts(setRuns));
        runCardSets.clear();
      }

      // Add this card to the current card set if the set is growing (eg Set[2H 2S] + 2C), otherwise
      // start a new set.
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

  // Converts a list of rummyRuns to rummy parts.
  // Eg. Given [2H 3H],[2H 3H 4Ha],[2Hb 3H 4Hb], return NatRummy[2H 3H 4Ha], NatRummy[2H 3H 4Hb],
  // PartialRummy[2H 3H].
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

  // Converts a list of setRuns to set parts.
  // Eg. Given [2H 2S],[2H 2S 2Ca],[2H 2S 2Cb], return Set[2H 2S 2Ca], Set[2H 2S 2Cb],
  // PartialSet[2H 2S].
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
}
