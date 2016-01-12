package rummy.parts;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import rummy.core.Card;
import rummy.core.Card.Face;
import rummy.core.Card.Suit;
import rummy.core.Hand;
import rummy.parts.PartsSolver.Solution;
import rummy.tokenizer.AggregateTokenizer;

public class PartsSolverTest {

  @Test
  public void testFullHand() {
    Hand hand = new Hand(
      Card.build(Face.TWO, Suit.HEARTS),
      Card.build(Face.THREE, Suit.HEARTS),
      Card.build(Face.FOUR, Suit.HEARTS),
      Card.build(Face.FIVE, Suit.HEARTS),
      Card.build(Face.SEVEN, Suit.HEARTS),
      Card.build(Face.SEVEN, Suit.SPADES),
      Card.build(Face.SEVEN, Suit.CLUBS),
      Card.build(Face.NINE, Suit.HEARTS),
      Card.build(Face.TEN, Suit.HEARTS),
      Card.build(Face.JACK, Suit.HEARTS),
      Card.build(Face.KING, Suit.HEARTS),
      Card.build(Face.KING, Suit.SPADES),
      Card.build(Face.KING, Suit.CLUBS),
      Card.build(Face.FOUR, Suit.SPADES)
    );

    List<Part> parts = new AggregateTokenizer().tokenize(hand);
    System.out.println("numParts:" + parts.size());
    assertTrue(parts.size() > 0);

    PartsSolver solver = new PartsSolver(13, parts, true);
    Solution solution = solver.findBestHand();
    assertNotNull(solution.parts);
    System.out.println("solution");
    System.out.println(solution.parts);
    System.out.println(solver.searchIterations);
  }

  @Test
  public void testQkaRun() {
    Hand hand = new Hand(
      Card.build(Face.QUEEN, Suit.HEARTS),
      Card.build(Face.THREE, Suit.HEARTS),
      Card.build(Face.KING, Suit.HEARTS),
      Card.build(Face.SEVEN, Suit.HEARTS),
      Card.build(Face.ACE, Suit.HEARTS),
      Card.build(Face.TWO, Suit.HEARTS),
      Card.build(Face.TWO, Suit.SPADES)
    );

    List<Part> parts = new AggregateTokenizer().tokenize(hand);
    System.out.println("qka parts:" + parts.toString());
  }

  @Test
  public void testSparseHand() {
    Hand hand = new Hand(
      Card.build(Face.TWO, Suit.HEARTS),
      Card.build(Face.THREE, Suit.HEARTS),
      Card.build(Face.FOUR, Suit.HEARTS),
      Card.build(Face.FOUR, Suit.SPADES),
      Card.build(Face.FIVE, Suit.HEARTS),
      Card.build(Face.FIVE, Suit.SPADES),
      Card.build(Face.SIX, Suit.HEARTS)
    );

    List<Part> parts = new AggregateTokenizer().tokenize(hand);
    System.out.println("numParts:" + parts.size());
    assertTrue(parts.size() > 0);

    Solution solution = new PartsSolver(7, parts, false).findBestHand();
    //assertNotNull(solution.parts);
    System.out.println("solution");
    System.out.println(solution.parts);
  }

  @Test
  public void testRun() {
    Hand hand = toHand("5♦ 6♦ 7♦ 10♦ J♦ Q♦ A♠ 2♠ 3♠ 5♣ 6♣ 7♣ jk QS");
    List<Part> parts = new AggregateTokenizer().tokenize(hand);
    assertTrue(parts.size() > 0);
  }

  @Test
  public void testWinHands() {
    // Standard hand
    checkWin("9H 9S 3H 3D 4D 5D 9D 9C AH AS AD 4H 5H", false);
    // With jokers
    checkWin("2H 3H 4H 5H 7S 7C 7D 10S JS QS KH KD jk AS", true);
    // Double joker
    checkWin("7♦ 8♦ 9♦ A♣ 2♣ 3♣ 4♣ 2♠ 3♠ jk 10♦ 10♣ jk 6H", true);
    checkWin("7♦ 8♦ 9♦ A♣ 2♣ 3♣ 4♣ 2♠ 3♠ jk 10♦ 10♣ jk", false);
    // Need to insert joker in middle of run
    checkWin("A♥ 2♥ jk 4♥ 7♥ 8♥ 9♥ K♥ Q♥ J♥ 4♦ 4S 4H 6C", true);
    checkWin("2♥ jk 4♥ 5H 7♥ 8♥ 9♥ K♥ Q♥ J♥ 4♦ 4S 4H 6C", true);
    // Large sequence
    checkWin("A♥ 2♥ 3♥ 4♥ 5♥ 6H 7♥ 8♥ Q♥ 10♥ J♥ 4♦ jk jk", true);
    checkWin("A♥ 2♥ 3♥ 4♥ 5♥ 6H 7♥ 8♥ Q♥ 10♥ J♥ QH KH AH", true);

    // Multiples of same card
    checkWin("7H 2♥ jk 2♥ 3♥ jk 3♥ 4♥ 8H 4♥ 5♥ 5♥ 5♣ 9S", true);
  }

  @Test
  public void testSparseHands() {
    checkSolution("J♣ 6♠ K♠ jk 2♠ jk 10♣ Q♠ 3♣ J♠ Q♦ 5♣ 8♠ 10S", true);
    checkSolution("A♣ A♣ 3♠ 3♠ 5♠ 5♠ 7♣ 7♣ 9♣ 9♣ J♣ J♣ K♠ K♠", true);
    checkSolution("A♣ 2♣ 3♣ 8♦ 9♦ jk5 Q♥ Q♦ Q♣ 10♥ 8♠ 10♥ A♦ KC", true);
  }

  private static void checkWin(String in, boolean extraCard) {
    System.out.println("checkWin");
    Hand hand = toHand(in);
    List<Part> parts = new AggregateTokenizer().tokenize(hand);
    System.out.println(parts);
    Solution solution = new PartsSolver(parts, extraCard).findBestHand();
    System.out.println(solution.parts);
    assertTrue(solution.isWinning);
  }

  private static void checkSolution(String in, boolean extraCard) {
    System.out.println("checkSolution");
    Hand hand = toHand(in);
    List<Part> parts = new AggregateTokenizer().tokenize(hand);
    System.out.println(parts);
    Solution solution = new PartsSolver(parts, extraCard).findBestHand();
    System.out.println(solution.parts);
    assertTrue(solution.score > -100);
    assertTrue(solution.freeCards.size() == 1);
  }

  private static Hand toHand(String in) {
    Hand hand = new Hand();
    int jkIdx = 1;
    Map<Integer, Integer> cardCount = new HashMap<>();
    for (String val : in.split(" ")) {
      char suitChar = val.charAt(val.length() - 1);
      String faceStr = val.substring(0, val.length() - 1);

      if (val.startsWith("jk")) {
        hand.cards.add(new Card(jkIdx++));
        continue;
      }

      Suit suit;
      switch (suitChar) {
        case 'H': case '♥': suit = Suit.HEARTS; break;
        case 'S': case '♠': suit = Suit.SPADES; break;
        case 'D': case '♦': suit = Suit.DIAMONDS; break;
        case 'C': case '♣': suit = Suit.CLUBS; break;
        default: throw new IllegalArgumentException("bad hand string");
      }

      Face face;
      switch (faceStr) {
        case "A" : face = Face.ACE; break;
        case "2" : face = Face.TWO; break;
        case "3" : face = Face.THREE; break;
        case "4" : face = Face.FOUR; break;
        case "5" : face = Face.FIVE; break;
        case "6" : face = Face.SIX; break;
        case "7" : face = Face.SEVEN; break;
        case "8" : face = Face.EIGHT; break;
        case "9" : face = Face.NINE; break;
        case "10" : face = Face.TEN; break;
        case "J" : face = Face.JACK; break;
        case "Q" : face = Face.QUEEN; break;
        case "K" : face = Face.KING; break;
        default: throw new IllegalArgumentException("bad hand string");
      }

      int value = (new Card(face, suit, 0)).value;
      if (cardCount.get(value) == null) {
        cardCount.put(value, 0);
      }
      int deckIdx = cardCount.get(value);
      cardCount.put(value, deckIdx + 1);

      hand.cards.add(new Card(face, suit, deckIdx));
    }
    return hand;
  }
}
