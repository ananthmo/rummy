package rummy.parts;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rummy.core.Card;

/**
 * Combines a list of part tokens (eg rummys, sets, partial rummys, single cards, etc) into a
 * hand that optimizes a cost function.  Goes through each possible combination of parts that forms
 * a valid hand, and chooses the one with best score. The algorithm favors memory over
 * running (eg computation) time, for mobile devices.
 */
public class PartsCombiner {

  private static final int DEFAULT_HAND_SIZE = 13;

  // Map from BitIdx to a Part, eg 4 -> PartialRummy(2H,3H)
  private final Map<Integer, Part> bitIdxToPart;
  // Map from a Part to its BitIdx, eg PartialRummy(2H,3H) -> 4
  private final Map<Part, Integer> partToBitIdx;
  // Map from a Card to a BitSet, containing the BitIdxes of the Parts the card belongs to,
  // Eg 2H -> [1000]
  private final Map<Card, BitSet> cardToBitSet;
  // Map from a Part to a BitSet, containing the BitIdxes of the Parts that each card belongs to
  // Eg PartialRummy(2H,3H) -> [1010]
  private final Map<Part, BitSet> partToBitSet;

  final PartsScorer scorer;
  List<Part> parts;
  final int handSize;
  final boolean extraCard;

  public static final Comparator<Part> COMPARE_BY_ORDINAL = new Comparator<Part>() {
    @Override
    public int compare(Part p1, Part p2) {
      // First enums to last enums
      return p1.type.ordinal() - p2.type.ordinal();
    }
  };

  // Allows for a different handSize just for testing purposes
  PartsCombiner(int handSize, List<Part> parts, boolean extraCard) {
    bitIdxToPart = new HashMap<>();
    partToBitIdx = new HashMap<>();
    cardToBitSet = new HashMap<>();
    partToBitSet = new HashMap<>();
    this.parts = parts;
    this.handSize = handSize;
    this.scorer = new PartsScorer();
    this.extraCard = extraCard;

    this.parts.sort(COMPARE_BY_ORDINAL);
    this.parts = pruneParts(parts);
    System.out.println(parts);

    // Create a BitIndex for each part
    int nextBitIdx = 1;
    for (Part part : parts) {
      bitIdxToPart.put(nextBitIdx, part);
      partToBitIdx.put(part, nextBitIdx);
      nextBitIdx += 1;  
    }

    // Create a BitSet for each card, indicating which parts it is used in.
    for (Part part : parts) {
      for (Card card : part.cards) {
        if (cardToBitSet.get(card) == null) {
          cardToBitSet.put(card, new BitSet());
        }
        BitSet cardBitSet = cardToBitSet.get(card);
        int bitIdx = partToBitIdx.get(part);
        cardBitSet.set(bitIdx);
      }
    }

    // Create a BitSet for each part, indicated which parts its cards are used in.
    for (Part part : parts) {
      BitSet bitSet = new BitSet();
      for (Card card : part.cards) {
        BitSet cardBitSet = cardToBitSet.get(card);
        bitSet.or(cardBitSet);
      }
      partToBitSet.put(part, bitSet);
      //System.out.println(partToBitIdx.get(part) + " ->" + part + " : " + partToBitSet.get(part));
    }
  }

  public PartsCombiner(List<Part> parts, boolean extraCard) {
    this(DEFAULT_HAND_SIZE, parts, extraCard);
  }

  /**
   * Shortens a list of parts, in order to lower the computation time for finding optimal part
   * configurations. This will make it impossible to find the optimal hand in certain (hopefully
   * rare) cases. It will remove parts that belong to a 3-card rummy (eg, those cards are stuck
   * to the rummy, don't consider any other usages).
   */
  static List<Part> pruneParts(List<Part> parts) {
    List<Part> pruned = new ArrayList<>();
    Set<Card> blackListed = new HashSet<>();
    for (Part part : parts) {
      boolean skip = false;
      if (part.type == PartType.NATURAL_RUMMY) {
        if (part.cards.size() == 3 && blackListed.size() <= 3) {
          blackListed.addAll(part.cards);
        }
      } else if (part.type != PartType.RUMMY) {
        for (Card card : part.cards) {
          if (blackListed.contains(card)) {
            skip = true;
          }
        }
      }

      if (!skip) {
        pruned.add(part);
      }
    }
    return pruned;
  }

  public Solution combineParts() {
    // Add all parts to set
    BitSet availableParts = new BitSet();
    Set<Card> allCards = new HashSet<>();
    for (Part part : parts) {
      availableParts.set(partToBitIdx.get(part));
      allCards.addAll(part.cards);
    }

    // Find which parts to use
    Set<Part> parts = new LinkedHashSet<>();
    Solution solution = new Solution();
    search(availableParts, 0, parts, allCards, new LinkedHashSet<>(), solution);
    return solution;
  }

  public static class Solution {
      public List<Part> parts = null;
      public int score = -999999;
      public List<Card> freeCards = null;
      public boolean isWinning = false;
  }

  int computeScore(Set<Part> parts) {
    return scorer.scoreParts(parts) + (scorer.isWinning(parts) ? 10000000 : 0);
  }

  int count = 0;
  BitSet usedPartSet;
  void search(
      BitSet availableParts,
      int startIdx,
      Set<Part> parts,
      Set<Card> availableCards,
      Set<Card> usedCards,
      Solution solution) {
    if (solution.isWinning) {
      // Found a solution, end the search.
      return;
    }

    count++;
    if (usedCards.size() == handSize && availableCards.size() == (extraCard ? 1 : 0)) {
      // Found a solution, record it if its the best one so far
      int score = computeScore(parts);
      if (score > solution.score) {
        solution.parts = new ArrayList<Part>(parts);
        solution.score = score;
        solution.freeCards = new ArrayList<Card>(availableCards);
        if (score > 100000) {
          solution.isWinning = true;
        }
      }
      return;
    }

    if (availableCards.size() + usedCards.size() < handSize || usedCards.size() >= handSize) {
      // No possible solution in this path
      return;
    }

    // Each up on GC by reusing these variables throughout the loop search.
    BitSet original = new BitSet();
    original.or(availableParts);
    Part part;
    for (int bitIdx = availableParts.nextSetBit(startIdx);
        bitIdx >= 0;
        bitIdx = availableParts.nextSetBit(bitIdx+1)) {
      // Use this part to form a hand
      part = bitIdxToPart.get(bitIdx);

      // No use in continuing if first part is a single, there must be a better hand previously.
      if (part.type == PartType.SINGLE && parts.size() == 0) {
        return;
      }

      // Mark which parts are no longer available for use
      usedPartSet = partToBitSet.get(part);
      parts.add(part);
      availableCards.removeAll(part.cards);
      usedCards.addAll(part.cards);

      // Clear the used bits
      availableParts.andNot(usedPartSet);

      // Recursively search through remaining cards to form a hand
      search(availableParts, bitIdx + 1, parts, availableCards, usedCards, solution);

      // Restore hand to original state.
      availableParts.or(original);
      parts.remove(part);
      usedCards.removeAll(part.cards);
      availableCards.addAll(part.cards);
    }
  }
}
