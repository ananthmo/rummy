package rummy.core;

import java.util.Objects;

/**
 * A card used in a hand of rummy. Its either a normal card with a face and suit, or a joker.
 */
public class Card {

  /** The possible suits a card can be. */
  public enum Suit {
    HEARTS,
    DIAMONDS,
    SPADES,
    CLUBS,
    JOKER;

    static Suit[] SUITS = {HEARTS, DIAMONDS, SPADES, CLUBS};
  }

  /** The possible faces a card can be. */
  public enum Face {
    ACE(10),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    SEVEN(7),
    EIGHT(8),
    NINE(9),
    TEN(10),
    JACK(10),
    QUEEN(10),
    KING(10),
    JOKER(0);

    final int points;

    Face(int points) {
      this.points = points;
    }

    static Face[] FACES =
        {ACE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING};
  }

  public final Face face;
  public final Suit suit;
  public final int value;
  public final int jokerIdx;

  public Card(Face face, Suit suit) {
    this.face = face;
    this.suit = suit;
    this.value = suit.ordinal() * 13 + face.ordinal();
    this.jokerIdx = 0;
  }

  public Card(int jokerIdx) {
    this.jokerIdx = jokerIdx;
    this.face = null;
    this.suit = null;
    this.value = -1;
  }

  public boolean isJoker() {
    return jokerIdx > 0;
  }

  public String toString() {
    if (isJoker()) {
      return "jk" + jokerIdx;
    }

    String result = "";
    switch (face) {
      case ACE: result += "A"; break;
      case EIGHT: result += "8"; break;
      case FIVE: result += "5"; break;
      case FOUR: result += "4"; break;
      case JACK: result += "J"; break;
      case KING: result += "K"; break;
      case NINE: result += "9"; break;
      case QUEEN: result += "Q"; break;
      case SEVEN: result += "7"; break;
      case SIX: result += "6"; break;
      case TEN: result += "10"; break;
      case THREE: result += "3"; break;
      case TWO: result += "2"; break;
      default: throw new IllegalStateException("bad card");
    };
    switch (suit) {
      case CLUBS: result += "\u2663"; break;
      case DIAMONDS: result += "\u2666"; break;
      case HEARTS: result += "\u2665"; break;
      case SPADES: result += "\u2660"; break;
      default: throw new IllegalStateException("bad card");
    }
    return result;
  }

  public static Card build(Face face, Suit suit) {
    return new Card(face, suit);
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Card)) {
      return false;
    }
    Card other = (Card) o;
    return face == other.face
        && suit == other.suit
        && value == other.value
        && jokerIdx == other.jokerIdx;
  }

  @Override
  public int hashCode() {
    return Objects.hash(face, suit, value, jokerIdx);
  }
}
