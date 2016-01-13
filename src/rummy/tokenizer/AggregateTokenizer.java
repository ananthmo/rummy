package rummy.tokenizer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import rummy.core.Card;
import rummy.core.Card.Face;
import rummy.core.Hand;
import rummy.parts.Part;

/**
 * Aggregates together all the different tokenizers.
 */
public class AggregateTokenizer implements PartsTokenizer {

  private final PartsTokenizer rummyTokenizer;
  private final PartsTokenizer setTokenizer;
  private final PartsTokenizer singlesTokenizer;

  public AggregateTokenizer() {
    rummyTokenizer = new RummyTokenizer();
    setTokenizer = new SetTokenizer();
    singlesTokenizer = new SinglesTokenizer();
  }

  @Override
  public Set<Part> tokenize(Hand hand, Face faceJoker) {
    Set<Part> parts = new HashSet<>();
    parts.addAll(rummyTokenizer.tokenize(hand, faceJoker));
    parts.addAll(setTokenizer.tokenize(hand, faceJoker));
    parts.addAll(singlesTokenizer.tokenize(hand, faceJoker));
    return parts;
  }
}
