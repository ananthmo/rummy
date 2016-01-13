package rummy.tokenizer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import rummy.core.Card;
import rummy.parts.Part;

/**
 * Generates the {@link Part} single tokens for a given hand.
 */
public class SinglesTokenizer extends MultiDeckTokenizer {

  @Override
  public Set<Part> generateParts(List<Card> cards, List<Card> jokers) {
    Set<Part> parts = new HashSet<>(cards.size() + jokers.size());
    for (Card card : cards) {
      parts.add(Part.single(card));
    }
    for (Card joker : jokers) {
      parts.add(Part.single(joker));
    }
    return parts;
  }
}
