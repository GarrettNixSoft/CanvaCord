package org.canvacord.gui.wizard;

import org.canvacord.exception.CanvaCordException;
import org.canvacord.gui.BooleanTask;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public abstract class CanvaCordWizard extends JDialog {

	public static final Font WIZARD_LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 12);
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
	private BooleanTask finishTask;

	public CanvaCordWizard(String title) {

		int width = 600;
		int height = 500;

		setSize(width, height);
		setMinimumSize(new Dimension(width, height));
		setMaximumSize(new Dimension(width, height));

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

	}

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
		buttonBox.add(Box.createHorizontalStrut(30));
		buttonBox.add(cancelButton);
		buttonPanel.add(buttonBox, BorderLayout.EAST);
		buttonPanel.setBorder(new EmptyBorder(new Insets(5, 10, 5, 10)));

		cardPanel = new JPanel();
		cardPanel.setBorder(new EmptyBorder(new Insets(5, 10, 5, 10)));

		cardLayout = new CardLayout();
		cardPanel.setLayout(cardLayout);

		contentPanel.add(cardPanel, BorderLayout.CENTER);
		contentPanel.add(buttonPanel, BorderLayout.SOUTH);

	}

	private void initButtons() {

		// the back and next buttons should navigate in their respective directions, if the current card supports it
		backButton.addActionListener(event -> currentCard.getPreviousCard().ifPresent(this::setCurrentCard));
		nextButton.addActionListener(event -> {
			currentCard.getNextCard().ifPresentOrElse(this::setCurrentCard, () -> {
				// if there is no next card, and this is an end card, then check whether to close the wizard
				if (currentCard.isEndCard()) {
					if (finishTask != null && finishTask.execute())
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

	protected void registerCard(WizardCard card) {
		if (!card.isConfigured()) throw new CanvaCordException("Attempted to register a Wizard Card without configuring its navigator");
		cardPanel.add(card);
		cardLayout.addLayoutComponent(card, card.toString());
		if (currentCard == null) {
			setCurrentCard(card);
		}
	}

	protected abstract void initCards();

	protected void setBackButtonEnabled(boolean enabled) {
		this.backButton.setEnabled(enabled);
	}

	protected void setNextButtonEnabled(boolean enabled) {
		this.nextButton.setEnabled(enabled);
	}

	protected void setCancelButtonEnabled(boolean enabled) {
		this.cancelButton.setEnabled(enabled);
	}

	protected void setFinishTask(BooleanTask task) { this.finishTask = task; }

	public void setCurrentCard(WizardCard currentCard) {
		// If there is already a card showing, call its function for navigating away
		if (this.currentCard != null) {
			this.currentCard.navigateAway();
		}
		// tell the CardLayout to show the new card
		cardLayout.show(cardPanel, currentCard.toString());
		// Assign the new current card and call its function for navigating to it
		this.currentCard = currentCard;
		this.currentCard.navigateTo();

		if (this.currentCard.isEndCard()) {
			nextButton.setText("Finish");
		}
		else {
			nextButton.setText("Next >");
		}
	}

	public abstract boolean completedSuccessfully();

	public boolean isCancelled() {
		return cancelled;
	}

	public boolean runWizard() {

		this.setVisible(true);
		this.dispose();

		return this.completedSuccessfully();

	}

}
