package rummy.tokenizer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import rummy.core.Card;
import rummy.core.Card.Face;
import rummy.core.Hand;
import rummy.parts.Part;

/**
 * Base class for tokenizing a hand, which is capable of handling more than one deck.
 *
 * Uses the notion of CardSets, a set of identical cards, to manage multiple decks. Subclasses can
 * create CardSets from runs, then use {@link #expandCardSets} to remake the runs using combinations
 * of each identical card.
 */
public abstract class MultiDeckTokenizer implements PartsTokenizer {

  /**
   * Generate a list of parts using the given cards and jokers.
   */
  protected abstract Set<Part> generateParts(List<Card> cards, List<Card> jokers);

  /**
   * Tokenizes a hand of cards into an exhaustive list of Parts (eg rummys, sets, partial sets,
   * singles, etc).
   */
  @Override
  public Set<Part> tokenize(Hand hand, Face faceJoker) {
    Set<Part> parts = new HashSet<>();

    // Parts using face jokers
    List<Card> cards = new ArrayList<>();
    List<Card> jokers = new ArrayList<>();
    splitPictureAndFaceJokers(hand, cards, jokers, faceJoker);
    parts.addAll(generateParts(cards, jokers));

    // Parts without face jokers (they are used as real cards)
    // TODO: possible improvement, only add NatRummy and Set parts
    cards.clear();
    jokers.clear();
    splitOnlyPictureJokers(hand, cards, jokers);
    parts.addAll(generateParts(cards, jokers));

    // Return the combined unique parts
    return parts;
  }

  private static void splitPictureAndFaceJokers(
      Hand hand, List<Card> cards, List<Card> jokers, Face faceJoker) {
    for (Card card : hand.cards) {
      if (card.isJoker() || (faceJoker != null && card.face == faceJoker)) {
        jokers.add(card);
      } else {
        cards.add(card);
      }
    }
  }

  private static void splitOnlyPictureJokers(
      Hand hand, List<Card> cards, List<Card> jokers) {
    for (Card card : hand.cards) {
      if (card.isJoker()) {
        jokers.add(card);
      } else {
        cards.add(card);
      }
    }
  }

  // Helper method to manage identical cards from multiple decks (a group of identical cards is
  // a CardSet).
  // Given a list of cardSets, multiplies them in sequence to form runs. For instance if the user
  // had a run of [2Ha 2Hb 3Ha 3Hb 4H], this forms the card set list [[2Ha 2Hb] [3Ha 3Hb] [4H]],
  // which expands to 4 runs [[2Ha 3Ha 4H],[2Ha 3Hb 4H],[2Hb 3Ha 4H],[2Hb 3Hb 4H]].
  protected static List<List<Card>> expandCardSets(List<Set<Card>> cardSets, int minRunSize) {
    // Expand the card sets
    List<List<Card>> runs = new ArrayList<>();
    runs.add(new ArrayList<>());
    for (Set<Card> cardSet : cardSets) {
      runs = multiply(runs, cardSet);
    }

    // Pick out appropriate runs.
    List<List<Card>> runsWithMinSize = new ArrayList<>();
    for (List<Card> run : runs) {
      for (int i = run.size(); i >= minRunSize; i--) {
        runsWithMinSize.add(run.subList(0, i));
      }
    }
    return runsWithMinSize;
  }

  // Multiplication means [[ab],[cd]] * (e,f,g) = [[abe],[abf],[abg],[cde],[cdf],[cdg]].
  static List<List<Card>> multiply(List<List<Card>> runs, Set<Card> operand) {
    List<List<Card>> expandedRuns = new ArrayList<>();
    for (List<Card> run : runs) {
      for (Card card : operand) {
        List<Card> newRun = new ArrayList<>();
        newRun.addAll(run);
        newRun.add(card);
        expandedRuns.add(newRun);
      }
    }
    return expandedRuns;
  }
}
