package rummy.parts;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import rummy.core.Card;
import rummy.core.Card.Face;
import rummy.core.Hand;

public class PartsBuilder {

	private static final Comparator<Card> COMPARE_BY_VALUE = new Comparator<Card>() {
		@Override
		public int compare(Card c1, Card c2) {
			return c1.value - c2.value;
		}
	};

	private static final Comparator<Card> COMPARE_BY_FACE = new Comparator<Card>() {
		@Override
		public int compare(Card c1, Card c2) {
			return c1.face.ordinal() - c2.face.ordinal();
		}
	};
	
	// Tokenizes a hand of cards into a list of Parts (eg Rummys, Sets, partial sets, etc).
	public List<Part> buildParts(Hand hand) {
		List<Card> cards = new ArrayList<>(removeJokers(hand.cards));
		
		
		List<Part> parts = new ArrayList<>();
		parts.addAll(findSingleParts(cards));
		parts.addAll(findRummyParts(cards));
		parts.addAll(findSetParts(cards));
		return parts;
	}
	
	List<Card> removeJokers(List<Card> cards) {
		List<Card> cardsNoJ = new ArrayList<>();
		for (Card card : cards) {
			if (!card.joker) {
				cardsNoJ.add(card);
			}
		}
		return cardsNoJ;
	}
	
	/**
	 * Finds the Rummy-related partTypes for this hand of cards. It sorts the cards by value, which
	 * sorts by suit then face. Iterates through the list, maintaining a sliding window of run
	 * types - the window is reset when a run is broken. Once a suit is exhausted, perform a final
	 * check for the Q-K-A run (which is not caught in the normal sequence).
	 */
	List<Part> findRummyParts(List<Card> cards) {
		List<Part> parts = new ArrayList<>();
		cards.sort(COMPARE_BY_VALUE);
		
		Card prev = null;
		List<Card> run = new ArrayList<>();
		List<Card> qkaRun = new ArrayList<>();
		for (int i = 0; i < cards.size(); i++) {
			Card card = cards.get(i);
			if (prev != null
					&& (card.suit != prev.suit || card.face.ordinal() != prev.face.ordinal() + 1)) {
				run.clear();
				if (card.suit != prev.suit) {
					qkaRun.clear();
				}
			}
			
			// Check for wrapping Q-K-A runs
			if (card.face == Face.ACE || card.face == Face.KING || card.face == Face.QUEEN) {
				qkaRun.add(card);
				if (qkaRun.size() == 3) {
					parts.add(Part.naturalRummy(qkaRun));	
				}
			}
			
			run.add(card);
			if (run.size() == 3 || run.size() == 4) {
				parts.add(Part.naturalRummy(run));
			} else if (run.size() == 2) { // Note: only adds the first 2 cards in the sequence
				parts.add(Part.partialRummy(run));	
			}
			
			prev = card;
		}
		
		return parts;
	}
	
	/**
	 * Returns each card as a SINGLE part.
	 */
	List<Part> findSingleParts(List<Card> cards) {
		List<Part> parts = new ArrayList<>(cards.size());
		for (Card card : cards) {
			parts.add(Part.single(card));
		}
		return parts;
	}
	
	/**
	 * Finds the set-related part types of this hand of cards. Sorts the cards by face, then
	 * iterates through, keeping a running list of cards with same face value.
	 */
	List<Part> findSetParts(List<Card> cards) {
		cards.sort(COMPARE_BY_FACE);
		List<Part> parts = new ArrayList<>();
		List<Card> run = new ArrayList<>();
		Card prev = null;
		for (int i = 0; i < cards.size(); i++) {
			Card card = cards.get(i);
			
			if (prev != null && card.face != prev.face) {
				run.clear();
			}
			
			run.add(card);
			if (run.size() == 3 || run.size() == 4) {
				parts.add(Part.set(run));
			} else if (run.size() == 2) { // Note: only adds first 2 cards of group
				parts.add(Part.partialSet(run));
			}
			
			prev = card;
		}
		return parts;
	}
}
