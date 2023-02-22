package org.canvacord.gui.component;

import org.canvacord.event.CanvaCordEvent;
import org.canvacord.event.CanvaCordEventHandler;
import org.canvacord.gui.CanvaCordApp;
import org.canvacord.gui.CanvaCordFonts;
import org.canvacord.instance.Instance;
import org.canvacord.instance.InstanceManager;
import org.canvacord.util.Globals;
import org.canvacord.util.input.UserInput;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.nio.file.Paths;

public class InstanceCell extends JPanel {
	
	public static final int ICON_SIZE = 96;
	public static final int HEIGHT = 130;
	public static final int LABEL_HEIGHT = 40;
	public static final int STATUS_HEIGHT = 30;
	public static final int DETAILS_HEIGHT = HEIGHT - LABEL_HEIGHT - STATUS_HEIGHT;
	public static final int OPTIONS_WIDTH = 60;
	public static final int STATUS_WIDTH = CanvaCordApp.MIN_INSTANCE_WIDTH - ICON_SIZE - OPTIONS_WIDTH;

	private final Instance instance;

	private JLabel statusLabel;
	private DangerousProgressBar statusBar;

	public InstanceCell(Instance instance) {
		this.instance = instance;
		setMinimumSize(new Dimension(CanvaCordApp.MIN_INSTANCE_WIDTH - 5, HEIGHT));
		setPreferredSize(new Dimension(CanvaCordApp.MIN_INSTANCE_WIDTH - 3, HEIGHT));
		setMaximumSize(new Dimension(10000, HEIGHT));
		setLayout(new BorderLayout());
		init(instance);
		initLogic(instance);
	}

	public Instance getInstance() {
		return instance;
	}

	public void init(Instance instance) {

		// Use a BorderLayout and add some vertical border padding
		setLayout(new BorderLayout());
		setBorder(new BevelBorder(BevelBorder.RAISED));

		// Get the icon to draw
		String iconPath = instance.getIconPath();

		// get the default if applicable
		if (iconPath.equals("default"))
			iconPath = Globals.DEFAULT_INSTANCE_ICON_PATH;
		// ootherwise, confirm the file exists; if not, apply the default
		else {
			if (!Paths.get(iconPath).toFile().exists())
				iconPath = Globals.DEFAULT_INSTANCE_ICON_PATH;
		}

		// add the icon to the left
		JPanel iconContainerPanel = new JPanel();
		iconContainerPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		iconContainerPanel.setMinimumSize(new Dimension(ICON_SIZE, ICON_SIZE));
		iconContainerPanel.setPreferredSize(new Dimension(ICON_SIZE, ICON_SIZE));
		iconContainerPanel.setMaximumSize(new Dimension(ICON_SIZE, ICON_SIZE));
		iconContainerPanel.setLayout(new GridBagLayout());

		ImagePanel iconPanel = new ImagePanel(iconPath);
		iconPanel.setMinimumSize(new Dimension(ICON_SIZE, ICON_SIZE));
		iconPanel.setPreferredSize(new Dimension(ICON_SIZE, ICON_SIZE));
		iconPanel.setMaximumSize(new Dimension(ICON_SIZE, ICON_SIZE));
		iconContainerPanel.add(iconPanel);
		add(iconContainerPanel, BorderLayout.WEST);

		// populate the main panel
		JPanel mainPanel = new JPanel();
		mainPanel.setBorder(new EmptyBorder(0, 10, 0, 0));
		mainPanel.setLayout(new BorderLayout());

		// Separate the label as an upper panel
		JPanel labelPanel = new JPanel();
		labelPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.X_AXIS));
		labelPanel.setMinimumSize(new Dimension(STATUS_WIDTH, LABEL_HEIGHT));
		labelPanel.setPreferredSize(new Dimension(STATUS_WIDTH, LABEL_HEIGHT));

		JLabel instanceLabel = new JLabel(instance.getName());
		instanceLabel.setFont(CanvaCordFonts.HEADER_FONT);
		labelPanel.add(instanceLabel);

		mainPanel.add(labelPanel, BorderLayout.NORTH);

		// Put all detail components in a lower panel
		JPanel instanceDetailsPanel = new JPanel();
		instanceDetailsPanel.setBorder(new EmptyBorder(0, 5, 0, 5));
		instanceDetailsPanel.setMinimumSize(new Dimension(STATUS_WIDTH, DETAILS_HEIGHT));
		instanceDetailsPanel.setPreferredSize(new Dimension(STATUS_WIDTH, DETAILS_HEIGHT));
		instanceDetailsPanel.setLayout(new BoxLayout(instanceDetailsPanel, BoxLayout.Y_AXIS));

		JLabel courseLabel = new JLabel("Course: " + instance.getCourseTitle());
		courseLabel.setFont(CanvaCordFonts.LABEL_FONT_MEDIUM);
		courseLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		instanceDetailsPanel.add(courseLabel);

		instanceDetailsPanel.add(Box.createVerticalStrut(2));

		JLabel serverLabel = new JLabel("Server: " + instance.getServerName());
		serverLabel.setFont(CanvaCordFonts.LABEL_FONT_MEDIUM);
		serverLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		instanceDetailsPanel.add(serverLabel);

		JPanel statusPanel = new JPanel();
		statusPanel.setBorder(new EmptyBorder(0, 5, 5, 5));
		statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
		statusPanel.setMinimumSize(new Dimension(STATUS_WIDTH, STATUS_HEIGHT));
		statusPanel.setPreferredSize(new Dimension(STATUS_WIDTH, STATUS_HEIGHT));

		statusLabel = new JLabel("Status: Stopped");
		statusLabel.setFont(CanvaCordFonts.LABEL_FONT_MEDIUM);
		statusPanel.add(statusLabel);

		statusPanel.add(Box.createHorizontalStrut(20));

		statusBar = new DangerousProgressBar(0, 100);
		statusBar.setMinimumSize(new Dimension(200, 12));
		statusBar.setPreferredSize(new Dimension(200, 16));
		statusBar.setMaximumSize(new Dimension(300, 16));
		statusBar.setMinimum(0);
		statusBar.setMaximum(100);
		statusPanel.add(statusBar);

		JPanel optionsButtonPanel = new JPanel();
		optionsButtonPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		optionsButtonPanel.setLayout(new GridBagLayout());

		JButton optionsButton = new JButton();
		optionsButton.setIcon(new ImageIcon("resources/options_icon.png"));
		optionsButton.setEnabled(true);
		optionsButtonPanel.add(optionsButton);

		mainPanel.add(instanceDetailsPanel, BorderLayout.CENTER);
		mainPanel.add(statusPanel, BorderLayout.SOUTH);
		mainPanel.add(optionsButtonPanel, BorderLayout.EAST);

		add(mainPanel, BorderLayout.CENTER);

		setEnabled(true);
		setOpaque(true);

		// OPTIONS BUTTON LOGIC
		optionsButton.addActionListener(event -> {

			// TODO make this do useful things; for now, use it for testing instance deletion
			if (UserInput.askToConfirm("Delete this instance?", "Delete Test"))
				InstanceManager.deleteInstance(instance);

		});

	}

	private void initLogic(Instance instance) {

		CanvaCordEventHandler.addEventListener(event -> {

			// If this event involves this cell's instance
			if (event.getPayload()[0] instanceof Instance eventInstance && eventInstance.equals(instance)) {

				switch (event.getType()) {

					case INSTANCE_STARTED -> {
						statusLabel.setText("Status: Idle");
					}
					case INSTANCE_STOPPED -> {
						statusLabel.setText("Status: Stopped");
					}
					case FETCH_STARTED -> {
						statusLabel.setText("Status: Fetching...");
						statusBar.setFailed(false);
						statusBar.setValue(0);
					}
					case NOTIFY_STARTED -> {
						statusLabel.setText("Status: Notifying...");
						statusBar.setFailed(false);
						statusBar.setValue(0);
					}
					case FETCH_COMPLETED, NOTIFY_COMPLETED -> {
						statusLabel.setText("Status: Idle");
						statusBar.setFailed(false);
						statusBar.setValue(0);
					}

					case FETCH_ERROR -> {
						statusLabel.setText("Status: Fetch Error");
						statusBar.setFailed(true);
					}
					case NOTIFY_ERROR -> {
						statusLabel.setText("Status: Notify Error");
						statusBar.setFailed(true);
					}

				}

			}

		});

	}

}
