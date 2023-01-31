package org.canvacord.gui.wizard;

import java.util.Optional;

public interface WizardNavigator {

	Optional<WizardCard> getNextCard();

}
