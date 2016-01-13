package rummy.tokenizer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import rummy.core.Card;
import rummy.parts.Part;

/**
 * Generates set-related {@link Part} tokens for a given hand.
 */
public class SetTokenizer extends MultiDeckTokenizer {

  private static final Comparator<Card> COMPARE_BY_FACE = new Comparator<Card>() {
    @Override
    public int compare(Card c1, Card c2) {
      return c1.face.ordinal() - c2.face.ordinal();
    }
  };

  /**
   * Finds the set-related part types of this hand of cards. Sorts the cards by face, then iterates
   * through, keeping a running list of cards with same face value. Those are then converted to
   * set parts.
   */
  @Override
  public List<Part> generateParts(List<Card> cards, List<Card> jokers) {
    cards.sort(COMPARE_BY_FACE);
    List<Part> parts = new ArrayList<>();
    List<Set<Card>> runCardSets = new ArrayList<>();
    Card prev = null;
    for (int i = 0; i < cards.size(); i++) {
      Card card = cards.get(i);

      // This set run as ended, convert the run into set parts.
      if (prev != null && card.face != prev.face && card.value != prev.value) {
        List<List<Card>> setRuns = expandCardSets(runCardSets, 2);
        parts.addAll(setRunsToParts(setRuns, jokers));
        runCardSets.clear();
      }

      // Add this card to the current card set if the set is growing (eg Set[2H 2S] + 2C), otherwise
      // start a new set.
      if (prev != null && card.value == prev.value) {
        runCardSets.get(runCardSets.size() - 1).add(card);
      } else {
        runCardSets.add(new LinkedHashSet<>());
        runCardSets.get(runCardSets.size() - 1).add(card);
      }

      prev = card;
    }

    List<List<Card>> setRuns = expandCardSets(runCardSets, 2);
    parts.addAll(setRunsToParts(setRuns, jokers));
    return parts;
  }

  // Converts a list of setRuns to set parts.
  // Eg. Given [2H 2S],[2H 2S 2Ca],[2H 2S 2Cb], return Set[2H 2S 2Ca], Set[2H 2S 2Cb],
  // PartialSet[2H 2S].
  List<Part> setRunsToParts(List<List<Card>> sets, List<Card> jokers) {
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
