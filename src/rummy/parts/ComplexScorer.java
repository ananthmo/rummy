package rummy.parts;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import rummy.core.Card.Face;

/**
 * Scorer which keeps track of state when scoring a part. For instance, weighs  the second natural
 * rummy less than the first.
 */
public class ComplexScorer implements Scorer {

  // The points for each type, with diminishing points with more types. Eg the first natural rummy
  // is worth 1000, second worth 500, third and more worth 500.
  private static final Map<PartType, int[]> POINT_MAP = new HashMap<>();
  static {
    POINT_MAP.put(PartType.NATURAL_RUMMY, new int[] {1000, 500, 500});
    POINT_MAP.put(PartType.RUMMY, new int[] {300, 300, 300});
    POINT_MAP.put(PartType.SET, new int[] {200, 200, 0});
    POINT_MAP.put(PartType.PARTIAL_RUMMY, new int[] {75, 75, 75});
    POINT_MAP.put(PartType.PARTIAL_SET, new int[] {50, 50, 25});
  }

  // Update counts of these types together as one atomic group.
  private static final Map<PartType, Set<PartType>> GROUPED_TYPES = new HashMap<>();
  static {
    GROUPED_TYPES.put(
        PartType.SET, new HashSet<>(Arrays.asList(PartType.SET, PartType.PARTIAL_SET)));
  }

  private static final int SINGLE_POINT = -5;
  private static final int JOKER_POINT = 100;

  private final Map<PartType, Integer> typeCounts;
  private boolean has4Run;

  public ComplexScorer() {
    this.typeCounts = new HashMap<>();
    this.has4Run = false;

    for (PartType type : PartType.values()) {
      typeCounts.put(type, 0);
    }
  }

  private int getTypeScore(Part part) {
    PartType type = part.type;
    if (type == PartType.SINGLE) {
      return part.cards.get(0).face == Face.JOKER ? JOKER_POINT : SINGLE_POINT;
    }

    // Use a multiplier to encourage one run of 4, invalidate multiple runs of 4 and any runs of 5.
    double multiplier = 1;
    if (part.cards.size() == 4) {
      if (!has4Run) {
        has4Run = true;
        multiplier = 1.1;
      } else {
        multiplier = 0;
      }
    }
    if (part.cards.size() == 5) {
      multiplier = 0;
    }

    // Discount ace rummys
    if (type == PartType.PARTIAL_RUMMY || type == PartType.RUMMY && part.containsAce) {
      multiplier *= 0.50;
    }

    // Return the score value for this part type, considering how many has been seen so far.
    int[] pointMap = POINT_MAP.get(type);
    int count = typeCounts.get(type);
    if (count < pointMap.length) {
      return (int)(pointMap[count] * multiplier);
    } else {
      return (int)(pointMap[pointMap.length - 1] * multiplier);
    }
  }

  public int scoreParts(Set<Part> parts) {
    int score = 0;
    for (Part part : parts) {
      score += getTypeScore(part);

      // Increment the count of this part type, or this group of types.
      Collection<PartType> types = Arrays.asList(part.type);
      if (GROUPED_TYPES.containsKey(part.type)) {
        types = GROUPED_TYPES.get(part.type);
      }
      for (PartType type : types) {
        typeCounts.put(type, typeCounts.get(type) + 1);
      }
    }
    return score;
  }
}
