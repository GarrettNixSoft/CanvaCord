package org.canvacord.gui.wizard;

import java.util.Optional;

/**
 * A functional interface used to logically determine which
 * card should be navigated to next when the user clicks the
 * "Next" button on a given card.
 * <br>
 * For simple wizards with no branching paths, this can simply
 * return a reference to the desired next card in the sequence.
 * For more complex wizards, where the next card may depend in
 * some way on what the user has done or entered on the current
 * or some previous card, this can be used to implement that
 * branching functionality.
 */
public interface WizardNavigator {

	Optional<WizardCard> getNextCard();

}
