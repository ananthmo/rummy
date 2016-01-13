package rummy.scorer;

import java.util.Set;

import rummy.parts.Part;

/**
 * State-less scorer that looks at each part independently.
 */
public class SimpleScorer implements Scorer {

  public static final SimpleScorer INSTANCE = new SimpleScorer();

  private SimpleScorer() {
    // Singleton, can't instantiate
  }

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
