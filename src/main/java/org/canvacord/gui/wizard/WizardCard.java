package org.canvacord.gui.wizard;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

/**
 * The WizardCard is the base for every card (page) that appears within a CanvaCordWizard.
 * It contains all user-interactive components except for the navigation and Cancel buttons.
 */
public abstract class WizardCard extends JPanel {

	private CanvaCordWizard parent;

	private WizardCard previousCard;
	private WizardNavigator navigator;

	private WizardAction onNavigateTo;
	private WizardAction onNavigateAway;

	private boolean configured = false;

	private final String name;
	private final boolean isEndCard;

	public static final int WIDTH = 600;
	public static final int HEIGHT = 450;

	public WizardCard(CanvaCordWizard parent, String name, boolean isEndCard) {
		this.parent = parent;
		this.name = name;
		this.isEndCard = isEndCard;
		setLayout(new FlowLayout());
		setMaximumSize(new Dimension(WIDTH, HEIGHT));
	}

	/**
	 * Subclasses should build their GUI components here. Any components that need to have
	 * listeners or logic applied to them should be implemented as instance fields.
	 */
	protected abstract void buildGUI();

	/**
	 * Subclasses should add listeners and logic to components here.
	 */
	protected abstract void initLogic();

	protected CanvaCordWizard getParentWizard() { return parent; }

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

	/**
	 * Get the previous card, if one exists. No previous card will be available
	 * for the first card in any given CanvaCordWizard.
	 * @return an Optional that may or may not contain a WizardCard instance
	 */
	public Optional<WizardCard> getPreviousCard() {
		return Optional.ofNullable(previousCard);
	}

	/**
	 * Assign the logic that will be used to determine the next card to navigate to
	 * when the user clicks the "Next" button.
	 * @param navigator a WizardNavigator returning a WizardCard
	 */
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

	@Override
	public String toString() {
		return name;
	}
}
