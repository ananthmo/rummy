package rummy.parts;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Evaluates how good a set of parts is. Scores can be compared to construct the best hand.
 */
public class PartsScorer {

  private int scorePart(Part part) {
    switch (part.type) {
      case NATURAL_RUMMY: return 1000;
      case PARTIAL_RUMMY: return 75;
      case PARTIAL_SET: return 50;
      case RUMMY: return 300;
      case SET: return 200;
      case SINGLE: return part.cards.get(0).isJoker() ? 105 : -5;
      default: throw new IllegalStateException("bad card");
    }
  }

  // TODO: possible improvements
  // - weigh second natural lower
  // - multiplier for single/partials
  // - weigh Ace partial runs lower
  // - weigh first part of 4 higher?
  public int scoreParts(Set<Part> parts) {
    int score = 0;
    for (Part part : parts) {
      score += scorePart(part);
    }
    return score;
  }

  // This is a winning 13-card hand if it contains a natural rummy, a rummy, and remaining cards
  // are rummys or sets.
  public boolean isWinning(Collection<Part> parts) {
    boolean natural = false;
    boolean rummy = false;
    boolean allSetsOrRuns = true;
    int numCards = 0;
    for (Part part : parts) {
      numCards += part.cards.size();
      if (part.type == PartType.NATURAL_RUMMY) {
        if (natural == false) {
          natural = true;
        } else {
          rummy = true;
        }
      } else if (part.type == PartType.RUMMY) {
        rummy = true;
      } else if (part.type != PartType.SET) {
        allSetsOrRuns = false;
      }
    }
    return numCards == 13 && natural && rummy && allSetsOrRuns;
  }
}
