package rummy.tokenizer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import rummy.core.Card;
import rummy.core.Card.Face;
import rummy.parts.Part;

/**
 * Generates rummy-related {@link Part} tokens for a given hand.
 */
public class RummyTokenizer extends MultiDeckTokenizer {

  private static final Comparator<Card> COMPARE_BY_VALUE = new Comparator<Card>() {
    @Override
    public int compare(Card c1, Card c2) {
      return c1.value - c2.value;
    }
  };

  /**
   * Finds the Rummy-related partTypes for this hand of cards. It sorts the cards by value, which
   * sorts by suit then face. Iterates through the list, maintaining a growing cardSet list which
   * gets reset once a rummy run is broken. This set is then converted to rummy parts. QKA runs are
   * checked explicitly, as those will not be found in the sort order sequence.
   */
  @Override
  public List<Part> generateParts(List<Card> cards, List<Card> jokers) {
    List<Part> parts = new ArrayList<>();
    cards.sort(COMPARE_BY_VALUE);

    Map<Face, Stack<Card>> qkaStacks = new HashMap<>();
    qkaStacks.put(Face.ACE, new Stack<>());
    qkaStacks.put(Face.KING, new Stack<>());
    qkaStacks.put(Face.QUEEN, new Stack<>());

    Card prev = null;
    List<Set<Card>> prevCardSets = new ArrayList<>();
    List<Set<Card>> cardSets = new ArrayList<>();
    for (int i = 0; i < cards.size(); i++) {
      Card card = cards.get(i);

      // This rummy run has ended, convert the run sets into parts.
      if ((prev != null
          && (card.suit != prev.suit || card.face.ordinal() != prev.face.ordinal() + 1)
          && (card.value != prev.value))
          || cardSets.size() == 5) {
        // Convert the card sets into parts
        parts.addAll(partsFromCardSets(prevCardSets, cardSets, jokers));

        // Reset the sets
        prevCardSets = cardSets;
        cardSets = new ArrayList<>();
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
        cardSets.add(new HashSet<>());
        cardSets.get(cardSets.size() - 1).add(card);
      }

      prev = card;
    }
    parts.addAll(partsFromCardSets(prevCardSets, cardSets, jokers));
    return parts;
  }

  private static List<Part> partsFromCardSets(
      List<Set<Card>> prevCardSets, List<Set<Card>> runningCardSets, List<Card> jokers) {
    List<Part> parts = new ArrayList<>();
    List<List<Card>> rummyRuns = expandCardSets(runningCardSets, 2);
    parts.addAll(rummyRunsToParts(rummyRuns, jokers));
    parts.addAll(combineRummyRunsWithJoker(prevCardSets, runningCardSets, jokers));
    return parts;
  }

  // Converts a list of rummyRuns to rummy parts.
  // Eg. Given [2H 3H],[2H 3H 4Ha],[2Hb 3H 4Hb], return NatRummy[2H 3H 4Ha], NatRummy[2H 3H 4Hb],
  // PartialRummy[2H 3H].
  static List<Part> rummyRunsToParts(List<List<Card>> rummyRuns, List<Card> jokers) {
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

  // Set if jokers can be used to combine run sets. Eg [(2Ha 2Hb)],(3H)] + [(5H),(6H)] + [jk1] can
  // form rummys [2Ha-3H-jk1-5H, 2Hb-3H-jk1-5H].
  static List<Part> combineRummyRunsWithJoker(
      List<Set<Card>> cardSets1, List<Set<Card>> cardSets2, List<Card> jokers) {
    List<Part> parts = new ArrayList<>();
    if (jokers.isEmpty()
        || cardSets1 == null
        || cardSets2 == null
        || cardSets1.isEmpty()
        || cardSets2.isEmpty()) {
      return parts;
    }

    // Check that two sets can join to form a rummy with a joker inserted in-between
    Card sets1Last = cardSets1.get(cardSets1.size() - 1).iterator().next();
    Card sets2First = cardSets2.get(0).iterator().next();
    if (sets1Last.suit != sets2First.suit || sets1Last.value + 1 != sets2First.value - 1) {
      return parts;
    }

    // Grab the last 2 sets from set 1, and first 2 sets from set 2.
    int sets1Size = cardSets1.size();
    int sets2Size = cardSets2.size();
    List<Set<Card>> sets1Tail = cardSets1.subList(Math.max(0, sets1Size - 2), sets1Size);
    List<Set<Card>> sets2Head = cardSets2.subList(0, Math.min(sets2Size, 2));

    // Create a new card set with jokers in the middle
    List<Set<Card>> cardSetsWithJoker = new ArrayList<>();
    cardSetsWithJoker.addAll(sets1Tail);
    List<Set<Card>> jokerSet = new ArrayList<>();
    jokerSet.add(new HashSet<>(jokers));
    cardSetsWithJoker.addAll(jokerSet);
    cardSetsWithJoker.addAll(sets2Head);

    // Convert the card set with jokers to rummys
    List<List<Card>> jokerRuns = expandCardSets(cardSetsWithJoker, 3);
    for (List<Card> jokerRun : jokerRuns) {
      parts.add(Part.rummyWithJoker(jokerRun));
    }
    return parts;
  }
}
