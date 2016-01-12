package rummy.tokenizer;

import java.util.ArrayList;
import java.util.List;

import rummy.core.Card;
import rummy.parts.Part;

/**
 * Generates the {@link Part} single tokens for a given hand.
 */
public class SinglesTokenizer extends AbstractPartsTokenizer {

  @Override
  public List<Part> generateParts(List<Card> cards, List<Card> jokers) {
    List<Part> parts = new ArrayList<>(cards.size() + jokers.size());
    for (Card card : cards) {
      parts.add(Part.single(card));
    }
    for (Card joker : jokers) {
      parts.add(Part.single(joker));
    }
    return parts;
  }
}
