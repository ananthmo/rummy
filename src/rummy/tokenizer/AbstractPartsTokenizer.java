package rummy.tokenizer;

import java.util.ArrayList;
import java.util.List;

import rummy.core.Card;
import rummy.core.Hand;
import rummy.parts.Part;
import rummy.parts.PartsSolver;

/**
 * Tokenizes a hand of cards into a list of {@link Part}s. The cards can belong to any number of
 * decks. The created parts can then be processed by a {@link PartsSolver}, to find a combination
 * that forms the best hand (via a scoring metric).
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

  private void splitIntoCardsAndJokers(List<Card> original, List<Card> cards, List<Card> jokers) {
    for (Card card : original) {
      if (card.isJoker()) {
        jokers.add(card);
      } else {
        cards.add(card);
      }
    }
  }

  // Given a list of cardSets, multiplies them in sequence. For instance if the user has a run
  // of 2H 2H 3H 3H 4H, which froms the card set [2Ha 2Hb] [3Ha 3Hb] [4H], it expands to 4 runs
  // [2Ha 3Ha 4H],[2Ha 3Hb 4H],[2Hb 3Ha 4H],[2Hb 3Hb 4H].
  protected static List<List<Card>> expandCardSets(List<List<Card>> cardSets, int minRunSize) {
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
            if (newRun.size() >= minRunSize) {
              selectedRuns.add(newRun);
            }
          }
        }
        runs = expandedRuns;
      }
    }
    return selectedRuns;
  }
}
