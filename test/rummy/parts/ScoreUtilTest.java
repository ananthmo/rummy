package rummy.parts;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import rummy.core.Card;
import rummy.core.Card.Face;
import rummy.core.Card.Suit;

/**
 * Unit tests for {@link ScoreUtil}.
 */
public class ScoreUtilTest {

  @Test
  public void testWinScores() {
    List<Part> parts = new ArrayList<>();
    parts.add(Part.naturalRummy(toList(
        new Card(Face.ACE, Suit.CLUBS, 0),
        new Card(Face.KING, Suit.CLUBS, 0),
        new Card(Face.QUEEN, Suit.CLUBS, 0))));
    parts.add(Part.naturalRummy(toList(
        new Card(Face.TWO, Suit.CLUBS, 0),
        new Card(Face.THREE, Suit.CLUBS, 0),
        new Card(Face.FOUR, Suit.CLUBS, 0))));
    parts.add(Part.set(toList(
        new Card(Face.FIVE, Suit.CLUBS, 0),
        new Card(Face.FIVE, Suit.SPADES, 0),
        new Card(Face.FIVE, Suit.HEARTS, 0))));
    parts.add(Part.set(toList(
        new Card(Face.SIX, Suit.DIAMONDS, 0),
        new Card(Face.SIX, Suit.CLUBS, 0),
        new Card(Face.SIX, Suit.SPADES, 0),
        new Card(Face.SIX, Suit.HEARTS, 0))));
    assertEquals(0, ScoreUtil.calculatePoints(parts));
  }

  @Test
  public void testFullHand() {
    List<Part> parts = new ArrayList<>();
    parts.add(Part.rummyWithJoker(toList(
        new Card(Face.ACE, Suit.CLUBS, 0),
        new Card(0),
        new Card(Face.QUEEN, Suit.CLUBS, 0))));
    parts.add(Part.rummyWithJoker(toList(
        new Card(Face.TWO, Suit.CLUBS, 0),
        new Card(1),
        new Card(Face.FOUR, Suit.CLUBS, 0))));
    parts.add(Part.set(toList(
        new Card(Face.FIVE, Suit.CLUBS, 0),
        new Card(Face.FIVE, Suit.SPADES, 0),
        new Card(Face.FIVE, Suit.HEARTS, 0))));
    parts.add(Part.set(toList(
        new Card(Face.SIX, Suit.DIAMONDS, 0),
        new Card(Face.SIX, Suit.CLUBS, 0),
        new Card(Face.SIX, Suit.SPADES, 0),
        new Card(Face.SIX, Suit.HEARTS, 0))));
    assertEquals(ScoreUtil.FULL_HAND_POINTS, ScoreUtil.calculatePoints(parts));
  }

  @Test
  public void testPartialHand() {
    List<Part> parts = new ArrayList<>();
    parts.add(Part.naturalRummy(toList(
        new Card(Face.ACE, Suit.CLUBS, 0),
        new Card(Face.KING, Suit.CLUBS, 0),
        new Card(Face.QUEEN, Suit.CLUBS, 0))));
    parts.add(Part.naturalRummy(toList(
        new Card(Face.TWO, Suit.CLUBS, 0),
        new Card(Face.THREE, Suit.CLUBS, 0),
        new Card(Face.FOUR, Suit.CLUBS, 0))));
    parts.add(Part.set(toList(
        new Card(Face.FIVE, Suit.CLUBS, 0),
        new Card(Face.FIVE, Suit.SPADES, 0),
        new Card(Face.FIVE, Suit.HEARTS, 0))));
    parts.add(Part.partialRummy(toList(
        new Card(Face.SIX, Suit.DIAMONDS, 0),
        new Card(Face.SEVEN, Suit.DIAMONDS, 0))));
    parts.add(Part.partialSet(toList(
        new Card(Face.JACK, Suit.DIAMONDS, 0),
        new Card(Face.JACK, Suit.SPADES, 0))));
    assertEquals(33, ScoreUtil.calculatePoints(parts));
  }

  public static List<Card> toList(Card... cards) {
    return new ArrayList<>(Arrays.asList(cards));
  }
}
