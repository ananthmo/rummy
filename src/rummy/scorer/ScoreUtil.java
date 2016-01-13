package rummy.scorer;

import java.util.Collection;

import rummy.core.Card;
import rummy.parts.Part;
import rummy.parts.PartType;

public final class ScoreUtil {

  public static final int FULL_HAND_POINTS = 80;

  private ScoreUtil() {
    // static class - can not instantiate
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
    return (natural && rummy) ? points : ScoreUtil.FULL_HAND_POINTS;
  }
}
