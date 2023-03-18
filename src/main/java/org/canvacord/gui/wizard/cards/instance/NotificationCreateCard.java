package org.canvacord.gui.wizard.cards.instance;

import org.canvacord.entity.CanvaCordNotification;
import org.canvacord.entity.CanvaCordRole;
import org.canvacord.event.CanvaCordEvent;
import org.canvacord.gui.CanvaCordFonts;
import org.canvacord.gui.component.ColorIcon;
import org.canvacord.gui.dialog.NotificationCreateDialog;
import org.canvacord.gui.wizard.CanvaCordWizard;
import org.canvacord.gui.wizard.WizardCard;
import org.canvacord.instance.Instance;
import org.canvacord.setup.InstanceCreateWizard;
import org.canvacord.util.input.UserInput;
import org.canvacord.util.string.StringConverter;
import org.canvacord.util.string.StringUtils;
import org.json.JSONArray;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationCreateCard extends InstanceConfigCard {

	private List<CanvaCordNotification> notifications;
	private JList<CanvaCordNotification> notificationsList;

	private JButton newNotificationButton;
	private JButton editNotificationButton;
	private JButton deleteNotificationButton;

	public NotificationCreateCard(CanvaCordWizard parent, String name, boolean isEndCard) {
		super(parent, name, isEndCard, "Set Up Notifications");
	}

	@Override
	protected void buildGUI() {

		// Use an absolute layout for this one as well
		contentPanel.setLayout(null);
		Dimension size = new Dimension(WizardCard.WIDTH, WizardCard.HEIGHT);
		contentPanel.setMinimumSize(size);
		contentPanel.setMaximumSize(size);
		contentPanel.setPreferredSize(size);

		JLabel description = new JLabel();
		description.setText(
				"""
				<html>Now, let's set up your Notifications. These are the actual messages CanvaCord will send
				when it finds new information from Canvas that users may want to be notified about. For each
				notification, you must assign it to an event type (such as "New Assignment"), a schedule, a
				target channel, and at least one Role you created in the previous step. Additionally, you can
				set a custom message format; if you leave this blank, CanvaCord will use a default message.</html>
				"""
		);
		description.setFont(CanvaCordFonts.LABEL_FONT_BIGGER_THAN_SMALL_BUT_SMALLER_THAN_MEDIUM);
		description.setBounds(20, 8, WizardCard.WIDTH - 40, 100);
		contentPanel.add(description);

		JScrollPane notificationsPane = new JScrollPane();
		notificationsPane.setBounds(20, 120, 450, 220);
		contentPanel.add(notificationsPane);

		notificationsList = new JList<>();
		notificationsPane.getViewport().setView(notificationsList);

		int buttonX = 508;
		int buttonY = 134;
		int buttonSize = 48;
		int buttonSpacing = 20;

		newNotificationButton = new JButton(new ImageIcon("resources/new_icon.png"));
		newNotificationButton.setBounds(buttonX, buttonY, buttonSize, buttonSize);
		contentPanel.add(newNotificationButton);

		editNotificationButton = new JButton(new ImageIcon("resources/edit_icon_wip.png"));
		editNotificationButton.setBounds(buttonX, buttonY + buttonSize + buttonSpacing, buttonSize, buttonSize);
		contentPanel.add(editNotificationButton);

		deleteNotificationButton = new JButton(new ImageIcon("resources/delete_icon_non_beveled.png"));
		deleteNotificationButton.setBounds(buttonX, buttonY + (buttonSize + buttonSpacing) * 2, buttonSize, buttonSize);
		contentPanel.add(deleteNotificationButton);

		// SHOWING NOTIFICATIONS IN THE LIST
		notificationsList.setFixedCellHeight(-1);
		notificationsList.setLayoutOrientation(JList.VERTICAL);
		notificationsList.setCellRenderer(new NotificationCellRenderer(notificationsList));

	}

	@Override
	protected void initLogic() {

		// init the list
		notifications = new ArrayList<>();

		// ================ ADDING NEW NOTIFICATIONS ================
		newNotificationButton.addActionListener(event -> {
			List<CanvaCordRole> availableRoles = ((RoleCreateCard) getParentWizard().getCard("role_config")).getRoles();
			NotificationCreateDialog.buildNotification((InstanceCreateWizard) getParentWizard(), availableRoles).ifPresent(notification -> {
				notifications.add(notification);
				updateNotificationsList();
				if (notifications.size() == 1)
					enableNext();
			});
		});

		// ================ EDITING EXISTING NOTIFICATIONS ================
		editNotificationButton.addActionListener(event -> {
			CanvaCordNotification selection = notificationsList.getSelectedValue();
			if (selection == null) return;
			int index = notificationsList.getSelectedIndex();
			List<CanvaCordRole> availableRoles = ((RoleCreateCard) getParentWizard().getCard("role_config")).getRoles();
			NotificationCreateDialog.editNotification((InstanceCreateWizard) getParentWizard(), availableRoles, selection).ifPresent(
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
				if (notifications.isEmpty())
					disableNext();
			}
		});

	}

	@Override
	protected void prefillGUI(Instance instanceToEdit) {
		// TODO Andrew
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

	private void enableNext() {
		getParentWizard().setNextButtonEnabled(true);
		getParentWizard().setNextButtonTooltip(null);
	}

	private void disableNext() {
		getParentWizard().setNextButtonEnabled(false);
		getParentWizard().setNextButtonTooltip("<html>You must create at least one<br>Notification before continuing.</html>");
	}

	private static class NotificationCellRenderer extends JLabel implements ListCellRenderer<CanvaCordNotification> {

		private static final int SIZE = 50;

		public NotificationCellRenderer(JList parent) {
			setPreferredSize(new Dimension(parent.getWidth(), SIZE));
		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(SIZE, SIZE);
		}

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

	public JSONArray getNotificationsArray() {
		JSONArray result = new JSONArray();
		for (CanvaCordNotification notification : notifications) {
			result.put(notification.getJSON());
		}
		return result;
	}
}
