package rummy.parts;

/** 
 * The possible ways of breaking down a rummy hand.
 */
public enum PartType {

  /** A rummy without jokers, 3 to 5 cards in sequence with same suit. Eg 3H-4H-5H. */
  NATURAL_RUMMY,

  /** A rummy with jokers. Eg. 3H-jk-4H-5H */
  RUMMY,

  /**
   * Three or more cards of the same face. Each suit must be unique. May contain jokers.
   * Eg 4H-4C-4D, 4H-jk-4D.
   */
  SET,

  /** Two cards that form part of a natural rummy. Eg 4H-5H. */
  PARTIAL_RUMMY,

  /** Two cards that form part of a set. Eg 4H-4C. */
  PARTIAL_SET,

  /** A card by itself. Eg 4H. */
  SINGLE,
}