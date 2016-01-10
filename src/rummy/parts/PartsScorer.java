package rummy.parts;

import java.util.Collection;
import java.util.Set;

import rummy.core.Card;

/**
 * Evaluates how good a set of parts is. Scores can be compared to construct the best hand.
 */
public class PartsScorer {

  public static final int WIN_SCORE = 1_000_000;
  public static final int FULL_HAND_POINTS = 80;

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

  /**
   * Points for a rummy hand is scored as follows. If the hand doesn't contain a natural rummy plus
   * some other rummy (ie natural or with jokers), it is a FULL hand and gets 80 points. If the hand
   * does contain a natural and rummy, points for any cards that do not belong to a rummy or set is
   * totaled. Card points are at face value (eg 2=2 points, ... 9=9 points, 10,J,Q,K,A=10 points).
   */
  public static int calculatePoints(Collection<Part> parts) {
    int points = 0;
    boolean natural = false;
    boolean rummy = false;
    for (Part part : parts) {
      if (part.type == PartType.NATURAL_RUMMY) {
        if (natural == false) {
          natural = true;
        } else {
          rummy = true;
        }
      } else if (part.type == PartType.RUMMY) {
        rummy = true;
      } else if (part.type != PartType.SET) {
        for (Card card : part.cards) {
          points += card.face.points;
        }
      }
    }
    return (natural && rummy) ? points : FULL_HAND_POINTS;
  }
}
