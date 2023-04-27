package org.canvacord.gui.component;

import net.miginfocom.swing.MigLayout;
import org.canvacord.event.CanvaCordEvent;
import org.canvacord.event.CanvaCordEventHandler;
import org.canvacord.event.FetchStage;
import org.canvacord.gui.CanvaCordApp;
import org.canvacord.gui.CanvaCordFonts;
import org.canvacord.instance.Instance;
import org.canvacord.instance.InstanceManager;
import org.canvacord.setup.InstanceCreateWizard;
import org.canvacord.util.Globals;
import org.canvacord.util.input.UserInput;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.Paths;

public class InstanceCell extends JPanel {

	// ================ COMPONENT POSITIONING AND ALIGNMENT ================
	public static final int ICON_SIZE = 120;
	public static final int HEIGHT = 150;
	public static final int LABEL_HEIGHT = 40;
	public static final int STATUS_HEIGHT = 30;
	public static final int DETAILS_HEIGHT = HEIGHT - LABEL_HEIGHT - STATUS_HEIGHT;
	public static final int OPTIONS_WIDTH = 60;
	public static final int STATUS_WIDTH = CanvaCordApp.MIN_INSTANCE_WIDTH - ICON_SIZE - OPTIONS_WIDTH;

	// ================ ASSOCIATED INSTANCE ================
	private final Instance instance;

	// ================ UPDATING STATUS ================
	private JLabel statusLabel;
	private DangerousProgressBar statusBar;

	// instances can be started, stopped, and edited
	private JButton startButton;
	private JButton stopButton;
	private JButton optionsButton;

	public InstanceCell(Instance instance) {
		this.instance = instance;
//		setMinimumSize(new Dimension(CanvaCordApp.MIN_INSTANCE_WIDTH - 5, HEIGHT));
//		setPreferredSize(new Dimension(CanvaCordApp.MIN_INSTANCE_WIDTH - 3, HEIGHT));
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
		setLayout(new MigLayout("", "[][][grow]", "[grow]"));
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
//		iconContainerPanel.setMinimumSize(new Dimension(ICON_SIZE, ICON_SIZE));
//		iconContainerPanel.setPreferredSize(new Dimension(ICON_SIZE, ICON_SIZE));
//		iconContainerPanel.setMaximumSize(new Dimension(ICON_SIZE, ICON_SIZE));
		iconContainerPanel.setLayout(new MigLayout("", "[grow]", "[grow]"));

		// Show the instance icon
		ImagePanel iconPanel = new ImagePanel(iconPath);
//		iconPanel.setMinimumSize(new Dimension(ICON_SIZE, ICON_SIZE));
//		iconPanel.setPreferredSize(new Dimension(ICON_SIZE, ICON_SIZE));
//		iconPanel.setMaximumSize(new Dimension(ICON_SIZE, ICON_SIZE));
		iconContainerPanel.add(iconPanel, "cell 0 0, width " + ICON_SIZE + ", height " + ICON_SIZE);
		add(iconContainerPanel, "cell 0 0, growx, growy");

		// populate the main panel
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new MigLayout("align left", "[]", "[][][][]"));
		add(centerPanel, "cell 1 0");

		// Show the instance's name
		JLabel instanceLabel = new JLabel(instance.getName());
		instanceLabel.setFont(CanvaCordFonts.HEADER_FONT);
		centerPanel.add(instanceLabel, "cell 1 0");

		// Put all detail components in a lower panel

		// Show the associated Canvas course title
		JLabel courseLabel = new JLabel("Course: " + instance.getCourseTitle());
		courseLabel.setFont(CanvaCordFonts.LABEL_FONT_MEDIUM);
		courseLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		centerPanel.add(courseLabel, "cell 1 1");

		// Show the associated Discord server name
		JLabel serverLabel = new JLabel("Server: " + instance.getServerName());
		serverLabel.setFont(CanvaCordFonts.LABEL_FONT_MEDIUM);
		serverLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		centerPanel.add(serverLabel, "cell 1 2");

		// Show the current status on a panel
		JPanel statusPanel = new JPanel();
		statusPanel.setLayout(new MigLayout("", "[][grow]", "[]"));

		// Show the current status as a string
		statusLabel = new JLabel("Status: Stopped");
		statusLabel.setFont(CanvaCordFonts.LABEL_FONT_MEDIUM);
		statusPanel.setPreferredSize(new Dimension(150, 24));
		statusPanel.add(statusLabel, "cell 0 0");

		// Use a progress bar to show fetch or notification progress
		statusBar = new DangerousProgressBar(0, 100);
//		statusBar.setMinimumSize(new Dimension(300, 16));
//		statusBar.setPreferredSize(new Dimension(300, 20));
//		statusBar.setMaximumSize(new Dimension(450, 20));
		statusBar.setMinimum(0);
		statusBar.setMaximum(100);
		statusPanel.add(statusBar, "cell 1 0, growx");

		centerPanel.add(statusPanel, "cell 1 3, growx");

		// Add a panel for the options button
		JPanel optionsButtonPanel = new JPanel();
		optionsButtonPanel.setLayout(new MigLayout("", "[][][]", "[]"));

		final int buttonSize = 50;

		// Add start button
		startButton = new JButton(new ImageIcon("resources/start_icon.png"));
		optionsButtonPanel.add(startButton, "cell 0 0, width " + buttonSize + ", height " + buttonSize);

		// Add stop button
		stopButton = new JButton(new ImageIcon("resources/stop_icon.png"));
		optionsButtonPanel.add(stopButton, "cell 1 0, width " + buttonSize + ", height " + buttonSize);

		// Add a button for instance options
		optionsButton = new JButton();
		optionsButton.setIcon(new ImageIcon("resources/options_icon.png"));
		optionsButton.setEnabled(true);
		optionsButtonPanel.add(optionsButton, "cell 2 0, width " + buttonSize + ", height " + buttonSize);

		add(optionsButtonPanel, "cell 2 0");

		setEnabled(true);
		setOpaque(true);

	}

	private void initLogic(Instance instance) {

		// ================ FIRE SELECTION EVENT WHEN CLICKED ================
		InstanceCell instanceCell = this;
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				CanvaCordEvent.newEvent(CanvaCordEvent.Type.GUI_COMPONENT_CLICKED, instanceCell);
			}
		});

		// ================ LISTEN FOR NEW EVENTS ================
		CanvaCordEventHandler.addEventListener(event -> {

			// If this event involves this cell's instance
			if (event.getPayload()[0] instanceof Instance eventInstance && eventInstance.equals(instance)) {

				// check the type of event and update the status bar
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
					case FETCH_COMPLETED -> {
						statusLabel.setText("Status: Fetch Complete");
						statusBar.setFailed(false);
						statusBar.setValue(100);
						SwingUtilities.invokeLater(() -> {
							try {
								Thread.sleep(1000);
								statusLabel.setText("Status: Idle");
								statusBar.setValue(0);
							}
							catch (Exception e) {
								System.out.println("Sleep failed");
							}
						});
					}
					case NOTIFY_COMPLETED -> {
						statusLabel.setText("Status: Notify Complete");
						statusBar.setFailed(false);
						statusBar.setValue(100);
						SwingUtilities.invokeLater(() -> {
							try {
								Thread.sleep(1000);
								statusLabel.setText("Status: Idle");
								statusBar.setValue(0);
							}
							catch (Exception e) {
								System.out.println("Sleep failed");
							}
						});
					}

					case FETCH_ERROR -> {
						statusLabel.setText("Status: Fetch Error");
						statusBar.setFailed(true);
					}
					case NOTIFY_ERROR -> {
						statusLabel.setText("Status: Notify Error");
						statusBar.setFailed(true);
					}

					case FETCH_UPDATE -> {
						FetchStage stage = (FetchStage) event.getPayload()[1];
						if (stage == FetchStage.ASSIGNMENTS)
							statusBar.setValue(33);
						else if (stage == FetchStage.ANNOUNCEMENTS)
							statusBar.setValue(67);
					}

				}

			}

		});

		// OPTIONS BUTTON LOGIC
		optionsButton.addActionListener(event -> {

			// TODO make this do useful things; for now, use it for testing instance deletion
//			if (UserInput.askToConfirm("Delete this instance?", "Delete Test"))
//				InstanceManager.deleteInstance(instance);

			new InstanceCreateWizard(instance)
					.runWizard();

		});

	}

}
