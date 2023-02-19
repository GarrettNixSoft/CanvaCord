package org.canvacord.gui.wizard;

import org.canvacord.exception.CanvaCordException;
import org.canvacord.gui.task.BackgroundTask;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * The CanvaCordWizard is the base for implementing multi-page GUI processes.
 */
public abstract class CanvaCordWizard extends JDialog {

	public static Font getFont(int size) {
		return new Font("Segoe UI", Font.PLAIN, size);
	}

	private JPanel contentPanel;

	private JPanel cardPanel;
	private JPanel buttonPanel;

	private CardLayout cardLayout;

	private Box buttonBox;
	private JButton backButton;
	private JButton nextButton;
	private JButton cancelButton;

	// CURRENT CARD
	private WizardCard currentCard;

	// Cancel flag
	private boolean cancelled = false;

	// Finish action
	private BackgroundTask<Boolean> finishTask;

	public static final int WIDTH = 600;
	public static final int HEIGHT = 500;

	/**
	 * Build a CanvaCordWizard with the given window title.
	 * @param title the string to display as the wizard's window title
	 */
	public CanvaCordWizard(String title) {

		setSize(WIDTH, HEIGHT);
		setMinimumSize(new Dimension(WIDTH, HEIGHT));
		setMaximumSize(new Dimension(WIDTH, HEIGHT));
		setPreferredSize(new Dimension(WIDTH, HEIGHT));

		setModalityType(ModalityType.APPLICATION_MODAL);
		setResizable(false);
		setTitle(title);

		initComponents();
		initButtons();
		initCards();

		getContentPane().add(contentPanel);

		// Center in the current display
		setLocationRelativeTo(null);

		pack();

		// The Back button should always be disabled on the first card
		setBackButtonEnabled(false);

	}

	/**
	 * Builds the basic GUI components shared by all CanvaCordWizards, such as
	 * the navigation and Cancel buttons ate the bottom and the blank panel
	 * which will be populated with GUI components by implementing subclasses
	 */
	private void initComponents() {

		contentPanel = new JPanel();
		contentPanel.setLayout(new BorderLayout());

		buttonPanel = new JPanel();
		buttonPanel.setLayout(new BorderLayout());
		buttonPanel.add(new JSeparator(), BorderLayout.NORTH);

		backButton = new JButton("< Back");
		nextButton = new JButton("Next >");
		cancelButton = new JButton("Cancel");

		buttonBox = new Box(BoxLayout.X_AXIS);
		buttonBox.add(backButton);
		buttonBox.add(Box.createHorizontalStrut(10));
		buttonBox.add(nextButton);
		buttonBox.add(Box.createHorizontalStrut(10));
		buttonBox.add(new JSeparator(SwingConstants.VERTICAL));
		buttonBox.add(Box.createHorizontalStrut(10));
		buttonBox.add(cancelButton);
		buttonPanel.add(buttonBox, BorderLayout.EAST);
		buttonPanel.setBorder(new EmptyBorder(new Insets(5, 10, 5, 10)));

		cardPanel = new JPanel();
//		cardPanel.setBorder(new EmptyBorder(new Insets(5, 10, 5, 10)));

		cardLayout = new CardLayout();
		cardPanel.setLayout(cardLayout);

		contentPanel.add(cardPanel, BorderLayout.CENTER);
		contentPanel.add(buttonPanel, BorderLayout.SOUTH);

	}

	/**
	 * Initializes the basic logic for the navigation and Cancel buttons.
	 */
	private void initButtons() {

		// the back and next buttons should navigate in their respective directions, if the current card supports it
		backButton.addActionListener(event -> currentCard.getPreviousCard().ifPresent(this::setCurrentCard));
		nextButton.addActionListener(event -> {
			currentCard.getNextCard().ifPresentOrElse(this::setCurrentCard, () -> {
				// if there is no next card, and this is an end card, then check whether to close the wizard
				if (currentCard.isEndCard()) {
					if (finishTask == null || finishTask.execute())
						this.setVisible(false);
				}
			});

		});

		// if the cancel button is enabled, it sets the cancelled flag and closes this wizard
		cancelButton.addActionListener(event -> {
			this.setVisible(false);
			cancelled = true;
		});

	}

	/**
	 * Add a card (or page) to this wizard.
	 * @param card the card to add
	 */
	protected void registerCard(WizardCard card) {
		if (!card.isConfigured()) throw new CanvaCordException("Attempted to register a Wizard Card without configuring its navigator");
		cardPanel.add(card, card.toString());
		cardLayout.addLayoutComponent(card, card.toString());
		if (currentCard == null) {
			setCurrentCard(card);
		}
	}

	/**
	 * Subclasses should build and register their cards in this method.
	 */
	protected abstract void initCards();

	public void setBackButtonEnabled(boolean enabled) {
		this.backButton.setEnabled(enabled);
	}

	public void setNextButtonEnabled(boolean enabled) {
		this.nextButton.setEnabled(enabled);
	}

	public void setNextButtonTooltip(String tooltip) {this.nextButton.setToolTipText(tooltip); }

	public void setCancelButtonEnabled(boolean enabled) {
		this.cancelButton.setEnabled(enabled);
	}

	public void setFinishTask(BackgroundTask<Boolean> task) { this.finishTask = task; }

	/**
	 * Navigate the wizard to the specified card (page).
	 * @param currentCard the card to navigate to
	 */
	public void setCurrentCard(WizardCard currentCard) {
		// If there is already a card showing, call its function for navigating away
		if (this.currentCard != null) {
			this.currentCard.navigateAway();
		}
		// tell the CardLayout to show the new card
		cardLayout.show(cardPanel, currentCard.toString());
		System.out.println("Show " + currentCard + " (width " + currentCard.getWidth() + ")");
		// Assign the new current card and call its function for navigating to it
		this.currentCard = currentCard;
		this.currentCard.navigateTo();

		// determine if this card is the last card in a sequence
		if (this.currentCard.isEndCard()) {
			nextButton.setText("Finish");
		}
		else {
			nextButton.setText("Next >");
		}

		// determine if this is the first card in a sequence
		setBackButtonEnabled(this.currentCard.getPreviousCard().isPresent());
	}

	/**
	 * Subclasses should implement logic here to determine whether the user has
	 * successfully completed whatever operation the wizard implements based on
	 * their inputs and the state of the GUI components.
	 * @return {@code true} if the user has completed the process without error.
	 */
	public abstract boolean completedSuccessfully();

	public boolean isCancelled() {
		return cancelled;
	}

	/**
	 * Show this wizard to the user.
	 * @return the result of calling {@code completedSuccessfully()} once the user exits the wizard,
	 * 			whether by closing the window, clicking Cancel, or clicking Finish.
	 */
	public boolean runWizard() {

		this.setVisible(true);
		this.dispose();

		return this.completedSuccessfully();

	}

}
