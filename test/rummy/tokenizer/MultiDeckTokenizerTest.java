package rummy.tokenizer;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import rummy.core.Card;
import rummy.core.Card.Face;
import rummy.core.Card.Suit;

/**
 * Unit tests for {@link MultiDeckTokenizer}. 
 */
public class MultiDeckTokenizerTest {

  private final Card ACE_1 = new Card(Face.ACE, Suit.CLUBS, 0);
  private final Card ACE_2 = new Card(Face.ACE, Suit.CLUBS, 1);
  private final Card TWO_1 = new Card(Face.TWO, Suit.CLUBS, 0);
  private final Card THREE_1 = new Card(Face.THREE, Suit.CLUBS, 0);
  private final Card THREE_2 = new Card(Face.THREE, Suit.CLUBS, 1);

  @Test
  public void testMultiply() {
    // Empty list
    assertMultiply(
        new Card[][] {{ACE_1}, {ACE_2}},
        new Card[][] {{}},
        new Card[] {ACE_1, ACE_2});

    // Single item
    assertMultiply(
        new Card[][] {{ACE_1, TWO_1}, {ACE_2, TWO_1}},
        new Card[][] {{ACE_1}, {ACE_2}},
        new Card[] {TWO_1});

    // Multiple items
    assertMultiply(
        new Card[][] {
          {ACE_1, TWO_1, THREE_1},
          {ACE_1, TWO_1, THREE_2},
          {ACE_2, TWO_1, THREE_1},
          {ACE_2, TWO_1, THREE_2}},
        new Card[][] {{ACE_1, TWO_1}, {ACE_2, TWO_1}},
        new Card[] {THREE_1, THREE_2});
  }

  private static void assertMultiply(
      Card[][] expectedRuns, Card[][] runs, Card[] cardSet) {
    List<List<Card>> expectedRunsList = toList(expectedRuns);
    List<List<Card>> runsList = toList(runs);
    Set<Card> cardSetSet = new LinkedHashSet<Card>(Arrays.asList(cardSet));

    assertEquals(expectedRunsList, MultiDeckTokenizer.multiply(runsList, cardSetSet));
  }

  private static <T> List<List<T>> toList(T[][] arrays) {
    List<List<T>> list = new ArrayList<>();
    for (T[] array : arrays) {
      list.add(Arrays.asList(array));
    }
    return list;
  }
}
