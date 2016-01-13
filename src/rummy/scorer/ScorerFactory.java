package rummy.scorer;

import com.google.common.base.Preconditions;

/**
 * Factory class for returning {@link Scorer} instances.
 */
public class ScorerFactory {

  // Singleton instance variables
  public static final ScorerFactory SIMPLE = new ScorerFactory(Type.SIMPLE);
  public static final ScorerFactory COMPLEX = new ScorerFactory(Type.COMPLEX);

  private enum Type {
    SIMPLE,
    COMPLEX
  }

  private final Type type;

  private ScorerFactory(Type type) {
    this.type = Preconditions.checkNotNull(type);
  }

  public Scorer get() {
    switch (type) {
      case COMPLEX: return new ComplexScorer();
      case SIMPLE: return SimpleScorer.INSTANCE;
      default: throw new IllegalStateException("bad type");
    }
  }
}
