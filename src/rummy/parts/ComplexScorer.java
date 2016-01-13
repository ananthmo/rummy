package rummy.parts;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;

import rummy.core.Card;
import rummy.core.Card.Face;

/**
 * Scorer which keeps track of state when scoring a part. Includes the following rules:
 * - Diminishing returns on part scores (eg first natural is 1000, second 500)
 * - Disallows more than two sets
 * - Disallows runs of five
 * - Encourages first run of four, disallows remaining
 * - Discourages duplicate single cards
 * - Slightly discourages runs with Ace
 */
public class ComplexScorer implements Scorer {

  // The points for each type, with diminishing points with more types. Eg the first natural rummy
  // is worth 1000, second worth 500, third and more worth 500.
  private static final Map<PartType, int[]> POINT_MAP =
      new ImmutableMap.Builder<PartType, int[]>()
          .put(PartType.NATURAL_RUMMY, new int[] {1000, 500, 500})
          .put(PartType.RUMMY, new int[] {300, 300, 300})
          .put(PartType.SET, new int[] {200, 100, -1000})
          .put(PartType.PARTIAL_RUMMY, new int[] {75, 75, 75})
          .put(PartType.PARTIAL_SET, new int[] {50, 50, 25})
          .build();

  // Update counts of these types together as one atomic group.
  private static final Map<PartType, Set<PartType>> GROUPED_TYPES = new HashMap<>();
  static {
    GROUPED_TYPES.put(
        PartType.SET, new HashSet<>(Arrays.asList(PartType.SET, PartType.PARTIAL_SET)));
  }

  private static final int SINGLE_POINT = -5;
  private static final int JOKER_POINT = 100;
  private static final int MULTIPLE_SINGLE_PENALTY = -50;

  private final Map<PartType, Integer> typeCounts;
  private final Set<Integer> runValues;
  private boolean has4Run;
  private boolean hasNatural;

  public ComplexScorer() {
    this.typeCounts = new HashMap<>();
    this.runValues = new HashSet<>();
    this.has4Run = false;

    for (PartType type : PartType.values()) {
      typeCounts.put(type, 0);
    }
  }

  private int getTypeScore(Part part) {
    PartType type = part.type;

    if (type == PartType.SINGLE) {
      // If the same single card is being used elsewhere, discount this heavily
      Card single = part.cards.get(0);
      int penalty = runValues.contains(single.value) ? MULTIPLE_SINGLE_PENALTY : 0;
      return penalty + (single.face == Face.JOKER ? JOKER_POINT : SINGLE_POINT);
    } else if (part.type == PartType.NATURAL_RUMMY) {
      hasNatural = true;
    }

    // Use a multiplier to encourage one run of 4, invalidate multiple runs of 4 and any runs of 5.
    // Discourage a set/rummy of 4 without a natural.
    double multiplier = 1;
    if (part.cards.size() == 4) {
      if (!has4Run) {
        has4Run = true;
        multiplier = ((type == PartType.SET || type == PartType.RUMMY) && !hasNatural) ? 0.5 : 1.10;
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
    // Set which cards belong to non-rummy parts
    for (Part part : parts) {
      for (Card card : part.cards) {
        if (part.type != PartType.SINGLE && !runValues.contains(card.value)) {
          runValues.add(card.value);
        }
      }
    }

    // Tally the score for each part
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
