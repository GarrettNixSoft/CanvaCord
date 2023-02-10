package org.canvacord.gui.wizard;

import org.canvacord.exception.CanvaCordException;
import org.canvacord.gui.BackgroundTask;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public abstract class CanvaCordWizard extends JDialog {

	public static final Font WIZARD_HEADER_FONT = new Font("Segoe UI", Font.BOLD, 20);
	public static final Font WIZARD_LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 12);
	public static final Font WIZARD_LABEL_FONT_MEDIUM = new Font("Segoe UI", Font.PLAIN, 14);
	public static final Font WIZARD_LABEL_FONT_LARGE = new Font("Segoe UI", Font.PLAIN, 16);

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

	protected void registerCard(WizardCard card) {
		if (!card.isConfigured()) throw new CanvaCordException("Attempted to register a Wizard Card without configuring its navigator");
		cardPanel.add(card, card.toString());
		cardLayout.addLayoutComponent(card, card.toString());
		if (currentCard == null) {
			setCurrentCard(card);
		}
	}

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
