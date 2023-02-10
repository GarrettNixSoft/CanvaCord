package org.canvacord.gui.wizard.cards.instance;

import org.canvacord.gui.ImagePanel;
import org.canvacord.gui.wizard.CanvaCordWizard;
import org.canvacord.gui.wizard.WizardCard;

import javax.swing.*;
import java.awt.*;

public abstract class InstanceConfigCard extends WizardCard {

	private CanvaCordWizard parent;
	protected JPanel cardPanel;
	protected JPanel contentPanel;

	public InstanceConfigCard(CanvaCordWizard parent, String name, boolean isEndCard, String title) {
		super(parent, name, isEndCard);
		this.parent = parent;
		buildHeader(title);
		buildGUI();
		initLogic();

		cardPanel.add(contentPanel, BorderLayout.CENTER);
	}

	private void buildHeader(String title) {

		// ================ GUI SUB-PANEL ================
		cardPanel = new JPanel();
		cardPanel.setLayout(new BorderLayout());
		cardPanel.setMaximumSize(new Dimension(CanvaCordWizard.WIDTH, CanvaCordWizard.HEIGHT - 30));

		// ================ HEADER ================
		ImagePanel topBar = new ImagePanel("resources/setup_topbar.png");
		topBar.setLayout(null);
//		topBar.setBorder(new EmptyBorder(10, 10, 10, 10));
		topBar.setPreferredSize(new Dimension(CanvaCordWizard.WIDTH, 80));
		topBar.setMinimumSize(new Dimension(CanvaCordWizard.WIDTH, 80));
		topBar.setMaximumSize(new Dimension(CanvaCordWizard.WIDTH, 80));
		cardPanel.add(topBar, BorderLayout.NORTH);

		JLabel cardHeader = new JLabel(title);
		cardHeader.setFont(CanvaCordWizard.WIZARD_HEADER_FONT);
		cardHeader.setBounds(30, 25, 300, 30);
		topBar.add(cardHeader);

		// ================ MAIN CONTENT ================
		contentPanel = new JPanel();
		contentPanel.setMaximumSize(new Dimension(CanvaCordWizard.WIDTH - 10, CanvaCordWizard.HEIGHT - 30));

	}

	protected abstract void buildGUI();
	protected abstract void initLogic();

	protected CanvaCordWizard getParentWizard() {
		return parent;
	}

}
