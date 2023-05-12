package org.canvacord.gui;

import org.apache.logging.log4j.LogManager;
import org.canvacord.discord.DiscordBot;
import org.canvacord.event.CanvaCordEvent;
import org.canvacord.event.CanvaCordEventHandler;
import org.canvacord.event.CanvaCordEventListener;
import org.canvacord.exception.CanvaCordException;
import org.canvacord.gui.component.InstanceCell;
import org.canvacord.gui.component.InstanceDetailsPanel;
import org.canvacord.instance.Instance;
import org.canvacord.instance.InstanceManager;
import org.canvacord.main.CanvaCord;
import org.canvacord.scheduler.CanvaCordScheduler;
import org.canvacord.util.input.UserInput;
import org.canvacord.util.resources.ImageLoader;
import org.canvacord.util.time.LongTaskDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CanvaCordApp extends JFrame {

	// ================ SINGLETON ================
	private static CanvaCordApp instance;

	// ================ DIMENSIONS ================
	public static final int DEFAULT_WIDTH = 1500;
	public static final int DEFAULT_HEIGHT = DEFAULT_WIDTH * 3 / 4;

	public static final int MIN_WIDTH = DEFAULT_WIDTH * 3 / 4;
	public static final int MIN_HEIGHT = MIN_WIDTH * 3 / 4;

	public static final int DEFAULT_TOP_BAR_HEIGHT = DEFAULT_HEIGHT / 10;
	public static final int MIN_TOP_BAR_HEIGHT = MIN_HEIGHT / 10;

	public static final int MIN_INSTANCE_WIDTH = MIN_WIDTH * 3 / 4;

	// ================ COMPONENTS ================
	private JPanel topPanel;
	private JButton startAllButton;
	private JButton stopAllButton;
	private JButton newInstanceButton;
	private JButton optionsButton;

	private JPanel mainPanel;
	private JSplitPane splitPane;
	private JPanel noInstancesPanel;
	private JScrollPane instancesPane;
	private JPanel instanceList;
	private JPanel detailsPanel;

	private Map<String, InstanceCell> instanceCells = new HashMap<>();

	/**
	 * Construct the main application window for CanvaCord.
	 */
	private CanvaCordApp() {
		super("CanvaCord " + CanvaCord.VERSION_ID);
		setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
		setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				onAppClose();
				dispose();
				System.exit(0);
			}
		});
		getContentPane().setLayout(new BorderLayout(0, 0));
		// build GUI elements and logic
		buildGUI();
		initLogic();
	}

	// ================ CONSTRUCTION ================
	private void buildGUI() {

		// ================ TOOLBAR PANEL ================
		topPanel = new JPanel();
		topPanel.setBorder(new EmptyBorder(0, 20, 0, 20));
		topPanel.setMinimumSize(new Dimension(MIN_WIDTH, MIN_TOP_BAR_HEIGHT));
		topPanel.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_TOP_BAR_HEIGHT));
		getContentPane().add(topPanel, BorderLayout.NORTH);
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));

		// ================ START ALL INSTANCES BUTTON ================
		startAllButton = new JButton();
		startAllButton.setLayout(new BoxLayout(startAllButton, BoxLayout.X_AXIS));
		startAllButton.setMaximumSize(new Dimension(120, 40));
		JLabel startIcon = new JLabel();
		startIcon.setIcon(ImageLoader.loadIcon("start_icon.png"));
		JLabel startLabel = new JLabel("Start All");
		startAllButton.add(startIcon);
		startAllButton.add(Box.createHorizontalStrut(8));
		startAllButton.add(startLabel);
		topPanel.add(startAllButton);

		// spacing
		topPanel.add(Box.createHorizontalStrut(20));

		// ================ STOP ALL INSTANCES BUTTON ================
		stopAllButton = new JButton();
		stopAllButton.setLayout(new BoxLayout(stopAllButton, BoxLayout.X_AXIS));
		stopAllButton.setMaximumSize(new Dimension(120, 40));
		JLabel stopIcon = new JLabel();
		stopIcon.setIcon(ImageLoader.loadIcon("stop_icon.png"));
		JLabel stopLabel = new JLabel("Stop All");
		stopAllButton.add(stopIcon);
		stopAllButton.add(Box.createHorizontalStrut(8));
		stopAllButton.add(stopLabel);
		topPanel.add(stopAllButton);

		// spacing
		topPanel.add(Box.createHorizontalStrut(20));

		JSeparator groupSep1 = new JSeparator(SwingConstants.VERTICAL);
		groupSep1.setMaximumSize(new Dimension(2, 100));
		topPanel.add(groupSep1);

		// spacing
		topPanel.add(Box.createHorizontalStrut(20));

		// ================ NEW INSTANCE BUTTON ================
		newInstanceButton = new JButton();
		newInstanceButton.setLayout(new BoxLayout(newInstanceButton, BoxLayout.X_AXIS));
		newInstanceButton.setMaximumSize(new Dimension(120, 40));
		JLabel newIcon = new JLabel();
		newIcon.setIcon(ImageLoader.loadIcon("new_icon.png"));
		JLabel newLabel = new JLabel("New...");
		newInstanceButton.add(newIcon);
		newInstanceButton.add(Box.createHorizontalStrut(8));
		newInstanceButton.add(newLabel);
		topPanel.add(newInstanceButton);

		// spacing
		topPanel.add(Box.createHorizontalStrut(20));

		// ================ CANVACORD OPTIONS BUTTON ================
		optionsButton = new JButton();
		optionsButton.setLayout(new BoxLayout(optionsButton, BoxLayout.X_AXIS));
		optionsButton.setMaximumSize(new Dimension(120, 40));
		JLabel optionsIcon = new JLabel();
		optionsIcon.setIcon(ImageLoader.loadIcon("options_icon.png"));
		JLabel optionsLabel = new JLabel("Options");
		optionsButton.add(optionsIcon);
		optionsButton.add(Box.createHorizontalStrut(8));
		optionsButton.add(optionsLabel);
		topPanel.add(optionsButton);

		// ================ MAIN PANEL ================
		mainPanel = new JPanel();
		getContentPane().add(mainPanel, BorderLayout.CENTER);
		mainPanel.setLayout(new BorderLayout(0, 0));

		// ================ SPLIT PANE ================
		splitPane = new JSplitPane();
		splitPane.setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT - MIN_TOP_BAR_HEIGHT));
		splitPane.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT - DEFAULT_TOP_BAR_HEIGHT));
		mainPanel.add(splitPane, BorderLayout.CENTER);

		// ================ NO INSTANCES PANEL ================
		noInstancesPanel = new JPanel();
		noInstancesPanel.setLayout(new GridBagLayout());

		JLabel noInstancesLabel = new JLabel("No Instances");
		noInstancesLabel.setFont(CanvaCordFonts.HEADER_FONT);
		noInstancesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		noInstancesPanel.add(noInstancesLabel);

		// ================ INSTANCES SCROLL PANE ================
		instancesPane = new JScrollPane();

		instanceList = new JPanel();
		instanceList.setLayout(new BoxLayout(instanceList, BoxLayout.Y_AXIS));

		instancesPane.getViewport().setView(instanceList);

		splitPane.setLeftComponent(instancesPane);

		// ================ INSTANCE DETAILS PANE ================
		detailsPanel = new JPanel();
		detailsPanel.setLayout(new CardLayout());

		detailsPanel.add("", new InstanceDetailsPanel(null));

		splitPane.setRightComponent(detailsPanel);

		// TODO

		// ================ CONDITIONAL ELEMENTS ================
		// if there are no instances, the left side of the split pane should be the "No Instances" panel
		if (InstanceManager.getInstances().isEmpty())
			splitPane.setLeftComponent(noInstancesPanel);
		else {
			populateInstancesPane();
		}

	}

	private void populateInstancesPane() {

		// clear the instances pane
		instanceList.removeAll();

		// add all instances to the instances pane
		List<Instance> instances = InstanceManager.getInstances();

		for (int i = 0; i < instances.size(); i++) {
			Instance instance = instances.get(i);
			addInstanceCell(instance);
			addInstanceDescription(instance);
//			if (i < instances.size() - 1) instancesPane.add(Box.createVerticalStrut(2));
			// TODO ^ this can be placed in another map by strut hashCode and removed when the instance is deleted
		}

		// show these instances in the panel
		splitPane.setLeftComponent(instancesPane);
		SwingUtilities.invokeLater(() -> splitPane.setDividerLocation(0.75));
	}

	/**
	 * Add a new cell to the Instance list panel.
	 * @param instance the instance the new cell should represent
	 */
	private void addInstanceCell(Instance instance) {
		InstanceCell cell = new InstanceCell(instance);
		instanceList.add(cell);
		instanceCells.put(cell.getInstance().getInstanceID(), cell);
		if (!splitPane.getLeftComponent().equals(instancesPane))
			splitPane.setLeftComponent(instancesPane);
		SwingUtilities.invokeLater(() -> splitPane.setDividerLocation(0.75));
	}

	/**
	 * Remove a cell from the Instance list panel. This should be used whenever
	 * a user deletes an Instance.
	 * @param cell the cell to remove
	 */
	private void removeInstanceCell(InstanceCell cell) {
		instanceList.remove(cell);
		instanceCells.remove(cell.getInstance().getInstanceID());
		System.out.println("Removed instance " + instance.getName() + " from panel");
		SwingUtilities.invokeLater(() -> splitPane.setDividerLocation(0.75));
		if (instanceCells.isEmpty())
			splitPane.setLeftComponent(noInstancesPanel);
	}

	private void addInstanceDescription(Instance instance) {
		InstanceDetailsPanel instanceDetailsPanel = new InstanceDetailsPanel(instance);
		detailsPanel.add(instance.getInstanceID(), instanceDetailsPanel);
	}

	private void removeInstanceDescription(InstanceDetailsPanel instanceDetailsPanel) {
		// TODO
	}

	// ================ INTERACTIVITY ================
	private void initLogic() {

		// ================ MOVING THE SPLIT PANE DIVIDER ================
		// the divider should start at 75%
		SwingUtilities.invokeLater(() -> splitPane.setDividerLocation(0.75));

		// the divider should reset to 75% when the application window is resized
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent event) {
				splitPane.setDividerLocation(0.75);
			}
		});

		// the divider should not be allowed to be moved to a position less than the minimum instance width
		splitPane.addPropertyChangeListener("dividerLocation", event -> {
			int location = (Integer) event.getNewValue();
			int windowWidth = getWidth();
			double ratio = location / (double) windowWidth;
			if (ratio < 0.75) {
				splitPane.setDividerLocation(0.75);
			}
		});

		// ================ STARTING/STOPPING ALL INSTANCES ================
		startAllButton.addActionListener(event -> {
			try {
				InstanceManager.runAllInstances();
			}
			catch (Exception e) {
				UserInput.showExceptionWarning(e);
				e.printStackTrace();
			}
		});

		stopAllButton.addActionListener(event -> {
			try {
				InstanceManager.stopAllInstances();
			}
			catch (Exception e) {
				UserInput.showExceptionWarning(e);
				e.printStackTrace();
			}
		});

		// ================ CREATING NEW INSTANCES ================
		newInstanceButton.addActionListener(event -> {

			// run the user through the New Instance wizard; if a new instance is created, update the panel
			InstanceManager.generateNewInstance().ifPresent(
					instance -> CanvaCordEvent.newEvent(CanvaCordEvent.Type.NEW_INSTANCE, instance)
			);

		});

		// ================ EDITING CANVACORD OPTIONS ================
		optionsButton.addActionListener(event -> {
			CanvaCord.explode();
		});

		// ================ REACTING TO EVENTS ================
		CanvaCordEventHandler.addEventListener(event -> {

			switch (event.getType()) {
				case NEW_INSTANCE -> {
					Instance newInstance = (Instance) event.getPayload()[0];
					addInstanceCell(newInstance);
					// Ask if user wants to initialize the instance
					int response = UserInput.askToConfirmCustom("Would you like to initialize this\nnew instance now?", "Initialize Now?", new String[]{"Yes", "Not Now"}, 0, JOptionPane.PLAIN_MESSAGE);
					if (response == 0) {
						LongTaskDialog.runLongTask(
								() -> {
									boolean success = newInstance.initialize();
									if (success) UserInput.showMessage("Instance initialized.", "Success");
									else UserInput.showErrorMessage("Instance initialization failed. Check\nthe logs for more information.", "Failed");
								},
								"Initializing " + newInstance.getName(),
								"Initialize Instance"
						);
					}
					else {
						UserInput.showMessage("You can initialize your instance any time\nfrom its settings menu. You will need\nto initialize it before you can run it.", "Initialize Later");
					}
				}
				case INSTANCE_DELETED -> {
					Instance deletedInstance = (Instance) event.getPayload()[0];
					InstanceCell cellToDelete = instanceCells.get(deletedInstance.getInstanceID());
					removeInstanceCell(cellToDelete);
					((CardLayout) detailsPanel.getLayout()).show(detailsPanel, "");
				}
			}

		});

		// ================ SELECTING INSTANCES ================
		CanvaCordEventHandler.addEventListener(event -> {
			if (event.getType() == CanvaCordEvent.Type.GUI_COMPONENT_CLICKED) {

				// User clicked an Instance
				InstanceCell clickedCell = (InstanceCell) event.getPayload()[0];

				System.out.println("User clicked " + clickedCell.getInstance().getName());

				((CardLayout) detailsPanel.getLayout()).show(detailsPanel, clickedCell.getInstance().getInstanceID());

			}
		});

	}

	// ================ RUNNING THE APP ================

	/**
	 * Run the CanvaCord application. This fails if CanvaCord is already running.
	 */
	public static void run() {

		if (instance != null)
			throw new CanvaCordException("Cannot have two CanvaCordApp instances!");

		instance = new CanvaCordApp();
		instance.setVisible(true);

	}

	/**
	 * Called when the user attempts to exit CanvaCord by clicking the X on the window.
	 * This attempts to properly shut down all instances and the scheduler.
	 */
	private static void onAppClose() {

		System.out.println("Closing CanvaCord");

		try {
			InstanceManager.stopAllInstances();
			CanvaCordScheduler.shutDown();
			DiscordBot.getBotInstance().disconnect();
			LogManager.shutdown();
		}
		catch (Exception e) {
			UserInput.showExceptionWarning(e);
		}

	}


}
