package rummy.parts;

import java.util.Set;

/**
 * Scorer that looks at each part independently.
 */
public class SimpleScorer implements Scorer {

  private int scorePart(Part part) {
    switch (part.type) {
      case NATURAL_RUMMY: return 1000;
      case PARTIAL_RUMMY: return part.containsAce ? 50 : 75;
      case PARTIAL_SET: return 50;
      case RUMMY: return part.containsAce ? 250 : 300;
      case SET: return 200;
      case SINGLE: return part.cards.get(0).isJoker() ? 105 : -5;
      default: throw new IllegalStateException("bad card");
    }
  }

  public int scoreParts(Set<Part> parts) {
    int score = 0;
    for (Part part : parts) {
      score += scorePart(part);
    }
    return score;
  }
}
