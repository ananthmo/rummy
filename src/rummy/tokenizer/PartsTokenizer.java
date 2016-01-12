package rummy.tokenizer;

import java.util.List;

import rummy.core.Hand;
import rummy.parts.Part;

public interface PartsTokenizer {

  List<Part> tokenize(Hand hand);
}
