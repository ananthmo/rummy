package rummy.tokenizer;

import java.util.List;

import rummy.core.Hand;
import rummy.parts.Part;
import rummy.parts.PartsSolver;

/**
 * Tokenizes a hand of cards into a list of {@link Part}s. The cards can belong to any number of
 * decks. The created parts can then be processed by a {@link PartsSolver}, to find a combination
 * that forms the best hand (via a scoring metric).
 */
public interface PartsTokenizer {

  /**
   * Generate an exhaustive list of part tokens for this hand.
   */
  List<Part> tokenize(Hand hand);
}
