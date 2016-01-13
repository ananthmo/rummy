package rummy.tokenizer;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

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
    return new ImmutableSet.Builder<Part>()
        .addAll(rummyTokenizer.tokenize(hand, faceJoker))
        .addAll(setTokenizer.tokenize(hand, faceJoker))
        .addAll(singlesTokenizer.tokenize(hand, faceJoker))
        .build();
  }
}
