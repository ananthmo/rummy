package rummy.tokenizer;

import java.util.ArrayList;
import java.util.List;

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
  public List<Part> tokenize(Hand hand) {
    List<Part> parts = new ArrayList<>();
    parts.addAll(rummyTokenizer.tokenize(hand));
    parts.addAll(setTokenizer.tokenize(hand));
    parts.addAll(singlesTokenizer.tokenize(hand));
    return parts;
  }
}
