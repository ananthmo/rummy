package rummy.parts;

import java.util.Set;

/**
 * Evaluates how good a set of parts is. Scores can be compared to construct the best hand.
 */
public interface Scorer {

  public int scoreParts(Set<Part> parts);
}
