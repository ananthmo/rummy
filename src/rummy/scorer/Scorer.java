package rummy.scorer;

import java.util.Set;

import rummy.parts.Part;

/**
 * Evaluates how good a set of parts is. Scores can be compared to construct the best hand.
 */
public interface Scorer {

  public int scoreParts(Set<Part> parts);
}
