package org.canvacord.gui.options.page;

import net.miginfocom.swing.MigLayout;
import org.canvacord.entity.CanvaCordNotification;
import org.canvacord.entity.CanvaCordRole;
import org.canvacord.exception.CanvaCordException;
import org.canvacord.gui.CanvaCordFonts;
import org.canvacord.gui.component.ColorIcon;
import org.canvacord.gui.dialog.NotificationCreateDialog;
import org.canvacord.gui.options.OptionPage;
import org.canvacord.util.input.UserInput;
import org.canvacord.util.string.StringUtils;
import org.json.JSONArray;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class NotificationsPage extends OptionPage {

	private List<CanvaCordNotification> notifications;
	private JList<CanvaCordNotification> notificationsList;

	private JButton newNotificationButton;
	private JButton editNotificationButton;
	private JButton deleteNotificationButton;

	public NotificationsPage() {
		super("Notifications");
	}

	@Override
	protected void buildGUI() {

		setLayout(new MigLayout("insets 10 10 10 10", "[grow][]", "[]".repeat(20)));

		JLabel notificationsLabel = new JLabel("Configured Notifications:");
		notificationsLabel.setFont(CanvaCordFonts.bold(CanvaCordFonts.LABEL_FONT_MEDIUM));
		add(notificationsLabel, "cell 0 0");

		notificationsList = new JList<>();
		notificationsList.setFixedCellHeight(-1);
		notificationsList.setLayoutOrientation(JList.VERTICAL);
		notificationsList.setCellRenderer(new NotificationCellRenderer());

		JScrollPane notificationPane = new JScrollPane();
		notificationPane.getViewport().setView(notificationsList);
		add(notificationPane, "cell 0 1 5 10, growx, growy");

		newNotificationButton = new JButton(new ImageIcon("resources/new_icon.png"));
		add(newNotificationButton, "cell 9 3");

		editNotificationButton = new JButton(new ImageIcon("resources/edit_icon_wip.png"));
		add(editNotificationButton, "cell 9 6");

		deleteNotificationButton = new JButton(new ImageIcon("resources/delete_icon_non_beveled.png"));
		add(deleteNotificationButton, "cell 9 9");

	}

	@Override
	@SuppressWarnings("unchecked")
	protected void initLogic() {

		// ================ ADDING NEW NOTIFICATIONS ================
		newNotificationButton.addActionListener(event -> {
			List<CanvaCordRole> availableRoles = (List<CanvaCordRole>) dataStore.get("roles");
			NotificationCreateDialog.buildNotification((Long) dataStore.get("server_id"), availableRoles).ifPresent(notification -> {
				notifications.add(notification);
				updateNotificationsList();
			});
		});

		// ================ EDITING EXISTING NOTIFICATIONS ================
		editNotificationButton.addActionListener(event -> {
			CanvaCordNotification selection = notificationsList.getSelectedValue();
			if (selection == null) return;
			int index = notificationsList.getSelectedIndex();
			List<CanvaCordRole> availableRoles = (List<CanvaCordRole>) dataStore.get("roles");
			NotificationCreateDialog.editNotification((Long) dataStore.get("server_id"), availableRoles, selection).ifPresent(
					editedNotification -> {
						notifications.set(index, editedNotification);
						updateNotificationsList();
					}
			);
		});

		// ================ DELETING NOTIFICATIONS ================
		deleteNotificationButton.addActionListener(event -> {
			int index = notificationsList.getSelectedIndex();
			if (index == -1) return;
			if (UserInput.askToConfirm("Delete this Notification?", "Confirm Delete")) {
				notifications.remove(index);
				updateNotificationsList();
			}
		});

	}

	@Override
	@SuppressWarnings("unchecked")
	protected void prefillGUI() {
		notifications = (List<CanvaCordNotification>) dataStore.get("notifications");
		updateNotificationsList();
	}

	@Override
	protected void verifyInputs() throws Exception {

		if (notifications.isEmpty())
			throw new CanvaCordException("You must create at least one Notification.");

		dataStore.store("configured_notifications", getNotificationsArray());

	}

	private static class NotificationCellRenderer extends JLabel implements ListCellRenderer<CanvaCordNotification> {

		private static final int SIZE = 50;

		@Override
		public Component getListCellRendererComponent(JList<? extends CanvaCordNotification> list, CanvaCordNotification notification, int index, boolean isSelected, boolean cellHasFocus) {

			List<CanvaCordRole> notifyRoles = notification.getRolesToPing();
			String rolesListStr = StringUtils.commaSeparatedList(notifyRoles);
			String eventStr = notification.getEventType().toString();

			// Get an average color from the selected roles
			int r = 0, g = 0, b = 0;
			for (CanvaCordRole role : notifyRoles) {
				Color color = role.getColor();
				r += color.getRed();
				g += color.getGreen();
				b += color.getBlue();
			}
			r /= notifyRoles.size();
			g /= notifyRoles.size();
			b /= notifyRoles.size();

			ColorIcon colorIcon = new ColorIcon(new Color(r, g, b), SIZE, SIZE);
			colorIcon.setDoBorder(true);
			setIcon(colorIcon);

			setText("<html><b>" + notification.getName() + "</b><br/>" + eventStr + ": " + notification.getFriendlyScheduleDescription() + "<br/>-->" + rolesListStr);

			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}

			setEnabled(list.isEnabled());
			setFont(CanvaCordFonts.LABEL_FONT_SMALL);
			setOpaque(true);

			return this;

		}
	}

	private void updateNotificationsList() {

		ListModel<CanvaCordNotification> notificationListModel = new AbstractListModel<>() {
			@Override
			public int getSize() {
				return notifications.size();
			}

			@Override
			public CanvaCordNotification getElementAt(int index) {
				return notifications.get(index);
			}
		};

		notificationsList.setModel(notificationListModel);
		notificationsList.updateUI();

	}

	private JSONArray getNotificationsArray() {
		JSONArray result = new JSONArray();
		for (CanvaCordNotification notification : notifications) {
			result.put(notification.getJSON());
		}
		return result;
	}

}
