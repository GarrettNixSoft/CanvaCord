package org.canvacord.gui.wizard.cards.instance;

import net.miginfocom.swing.MigLayout;
import org.canvacord.gui.ImagePanel;
import org.canvacord.gui.wizard.CanvaCordWizard;

import javax.swing.*;
import java.awt.*;

public class InstanceBasicConfigCard extends InstanceConfigCard {

	public InstanceBasicConfigCard(CanvaCordWizard parent, String name, boolean isEndCard) {
		super(parent, name, isEndCard, "Basic Settings");
	}

	@Override
	protected void buildGUI() {

		// ================ GUI SUB-PANEL ================
		JPanel cardPanel = new JPanel();
		cardPanel.setLayout(new BorderLayout());

		// ================ HEADER ================
		ImagePanel topBar = new ImagePanel("resources/setup_topbar.png");
		topBar.setLayout(null);
//		topBar.setBorder(new EmptyBorder(10, 10, 10, 10));
		topBar.setPreferredSize(new Dimension(getMaximumSize().width, 80));
		cardPanel.add(topBar, BorderLayout.NORTH);

		JLabel cardHeader = new JLabel("Basic Settings");
		cardHeader.setFont(CanvaCordWizard.WIZARD_HEADER_FONT);
		cardHeader.setBounds(30, 25, 300, 30);
		topBar.add(cardHeader);

		// ================ MAIN CONTENT ================
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new MigLayout("", "[grow]", "[grow][grow]"));

		// TODO

		// ================ ADD COMPONENTS ================
		cardPanel.add(contentPanel, BorderLayout.CENTER);
		add(cardPanel);

	}

	@Override
	protected void initLogic() {
		// TODO
	}
}
