package org.canvacord.gui.wizard;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

public class WizardCard extends JPanel {

	private WizardCard previousCard;
	private WizardNavigator navigator;

	private WizardAction onNavigateTo;
	private WizardAction onNavigateAway;

	private boolean configured = false;

	private boolean isEndCard;

	public WizardCard(boolean isEndCard) {
		this.isEndCard = isEndCard;
		setLayout(new FlowLayout());
	}

	public void setOnNavigateTo(WizardAction onNavigateTo) {
		this.onNavigateTo = onNavigateTo;
	}

	public void setOnNavigateAway(WizardAction onNavigateAway) {
		this.onNavigateAway = onNavigateAway;
	}

	public void navigateTo() {
		if (onNavigateTo != null) onNavigateTo.execute();
	}

	public void navigateAway() {
		if (onNavigateAway != null) onNavigateAway.execute();
	}

	public void setPreviousCard(WizardCard previousCard) {
		this.previousCard = previousCard;
	}

	public Optional<WizardCard> getPreviousCard() {
		return Optional.ofNullable(previousCard);
	}

	public void setNavigator(WizardNavigator navigator) {
		this.navigator = navigator;
		if (this.navigator != null) configured = true;
	}

	public Optional<WizardCard> getNextCard() {
		return navigator.getNextCard();
	}

	public boolean isConfigured() {
		return configured;
	}

	public boolean isEndCard() {
		return isEndCard;
	}
}
