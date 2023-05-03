package org.canvacord.gui.wizard.cards.instance;

import org.canvacord.gui.CanvaCordFonts;
import org.canvacord.gui.component.ImagePanel;
import org.canvacord.gui.wizard.CanvaCordWizard;
import org.canvacord.gui.wizard.WizardCard;
import org.canvacord.instance.Instance;

import javax.swing.*;
import java.awt.*;

/**
 * The InstanceConfigCard implements generic functionality common
 * to all but the starting card of the InstanceCreateWizard.
 */
public abstract class InstanceConfigCard extends WizardCard {

	private CanvaCordWizard parent;

	// The Card Panel contains the contentPanel and places it
	// in the center of the wizard, under the header
	protected JPanel cardPanel;
	// The Content Panel contains all GUI elements that reside
	// in the interactive area, above the navigation buttons at
	// the bottom
	protected JPanel contentPanel;

	public InstanceConfigCard(CanvaCordWizard parent, String name, boolean isEndCard, String title) {
		super(parent, name, isEndCard);
		this.parent = parent;
		buildHeader(title);
		buildGUI();
		initLogic();

		cardPanel.add(contentPanel, BorderLayout.CENTER);
		add(cardPanel);
	}

	/**
	 * Builds the header visible at the top of each card in the instance setup process.
	 * @param title the title to show on top of the header
	 */
	private void buildHeader(String title) {

		// ================ GUI SUB-PANEL ================
		cardPanel = new JPanel();
		cardPanel.setLayout(new BorderLayout());
		cardPanel.setMaximumSize(new Dimension(CanvaCordWizard.WIDTH, CanvaCordWizard.HEIGHT - 30));

		// ================ HEADER ================
		ImagePanel topBar = ImagePanel.loadFromResources("setup_topbar.png");
		topBar.setLayout(null);
//		topBar.setBorder(new EmptyBorder(10, 10, 10, 10));
		topBar.setPreferredSize(new Dimension(CanvaCordWizard.WIDTH, 80));
		cardPanel.add(topBar, BorderLayout.NORTH);

		JLabel cardHeader = new JLabel(title);
		cardHeader.setFont(CanvaCordFonts.HEADER_FONT);
		cardHeader.setBounds(30, 25, 300, 30);
		topBar.add(cardHeader);

		// ================ MAIN CONTENT ================
		contentPanel = new JPanel();
		contentPanel.setMaximumSize(new Dimension(CanvaCordWizard.WIDTH - 10, CanvaCordWizard.HEIGHT - 30));

	}

	/**
	 * All GUI elements visible on this card should be created, configured, and added
	 * to the content panel here.
	 */
	protected abstract void buildGUI();

	/**
	 * All listeners should be implemented and added here.
	 */
	protected abstract void initLogic();

	/**
	 * When an InstanceCreateWizard is created in edit mode, it will call this
	 * method on each of its child cards, passing a reference to the Instance
	 * that is being edited.
	 * @param instanceToEdit the instance to extract field data from
	 */
	public abstract void prefillGUI(Instance instanceToEdit);

	protected CanvaCordWizard getParentWizard() {
		return parent;
	}

}
