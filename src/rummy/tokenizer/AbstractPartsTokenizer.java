package rummy.tokenizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import rummy.core.Card;
import rummy.core.Hand;
import rummy.parts.Part;

/**
 * Base class for tokenizing a hand. Provides helper methods, in particular {@link #expandCardSets}
 * should be used manage cards from multiple decks.
 */
public abstract class AbstractPartsTokenizer implements PartsTokenizer {

  protected abstract List<Part> generateParts(List<Card> cards, List<Card> jokers);

  /**
   * Tokenizes a hand of cards into an exhaustive list of Parts (eg rummys, sets, partial sets,
   * singles, etc).
   */
  @Override
  public List<Part> tokenize(Hand hand) {
    List<Card> cards = new ArrayList<>();
    List<Card> jokers = new ArrayList<>();
    splitIntoCardsAndJokers(hand.cards, cards, jokers);
    return generateParts(cards, jokers);
  }

  private static void splitIntoCardsAndJokers(
      List<Card> original, List<Card> cards, List<Card> jokers) {
    for (Card card : original) {
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
  // had a run of [2Ha 2Hb 3Ha 3Hb 4H], this forms the card set list [[2Ha 2Hb] [3Ha 3Hb] [4H]], it
  // expands to 4 runs [[2Ha 3Ha 4H],[2Ha 3Hb 4H],[2Hb 3Ha 4H],[2Hb 3Hb 4H]].
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

  // Multiplication means [[a],[bc]] * (d,e,f) = [[ad],[ae],[af],[bcd],[bce],[bcf]].
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
