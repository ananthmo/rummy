package rummy.parts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import rummy.core.Card;

enum PartType {
  NATURAL_RUMMY, // Eg 3H-4H-5H
  RUMMY, // Eg 3H-Jk-5H
  SET, // Eg 4H-4C-4D, 4H-Jk-4D
  PARTIAL_RUMMY, // Eg 4H-5H, 4H-Jk
  PARTIAL_SET, // eg 4H-4D, 4H-Jk
  SINGLE, // eg 4H
}

/**
 * A part (or token) represents a unit that a hand breaks down to. For instance, a 3H in a hand
 * could fall into a rummy parts 3H-4H-5H, and/or a partial set 3H-3C.
 */
public class Part {

  public final PartType type;
  public final List<Card> cards;

  Part(PartType type, List<Card> cards) {
    this.type = type;
    this.cards = cards;
  }

  public String toString() {
    String result = "";
    result += type.name() + ": " + cards.toString();
    return result;
  }

  public static Part naturalRummy(List<Card> cards) {
    ArrayList<Card> cardsCopy = new ArrayList<>(cards);
    return new Part(PartType.NATURAL_RUMMY, cardsCopy);
  }

  public static Part partialRummy(List<Card> cards) {
    ArrayList<Card> cardsCopy = new ArrayList<>(cards);
    return new Part(PartType.PARTIAL_RUMMY, cardsCopy);
  }

  public static Part set(List<Card> cards) {
    ArrayList<Card> cardsCopy = new ArrayList<>(cards);
    return new Part(PartType.SET, cardsCopy);
  }

  public static Part partialSet(List<Card> cards) {
    ArrayList<Card> cardsCopy = new ArrayList<>(cards);
    return new Part(PartType.PARTIAL_SET, cardsCopy);
  }

  public static Part single(Card card) {
    return new Part(PartType.SINGLE, Arrays.asList(card));
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Part)) {
      return false;
    }
    Part other = (Part) o;
    return type == other.type && Objects.equals(cards, other.cards);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, cards);
  }
}
