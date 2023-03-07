package org.canvacord.gui.wizard.cards.instance;

import edu.ksu.canvas.requestOptions.ListCourseAssignmentsOptions;
import org.canvacord.discord.CanvaCordRole;
import org.canvacord.gui.CanvaCordFonts;
import org.canvacord.gui.component.ColorIcon;
import org.canvacord.gui.dialog.RoleCreateDialog;
import org.canvacord.gui.wizard.CanvaCordWizard;
import org.canvacord.gui.wizard.WizardCard;
import org.canvacord.util.input.UserInput;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RoleCreateCard extends InstanceConfigCard {

	private List<CanvaCordRole> roles;
	private JList<CanvaCordRole> rolesList;

	private JButton newRoleButton;
	private JButton editRoleButton;
	private JButton deleteRoleButton;

	public RoleCreateCard(CanvaCordWizard parent, String name, boolean isEndCard) {
		super(parent, name, isEndCard, "Set Up Roles");
	}

	@Override
	protected void buildGUI() {

		// Use an absolute layout for this one
		contentPanel.setLayout(null);
		Dimension size = new Dimension(WizardCard.WIDTH, WizardCard.HEIGHT);
		contentPanel.setMinimumSize(size);
		contentPanel.setMaximumSize(size);
		contentPanel.setPreferredSize(size);

		JLabel description = new JLabel();
		description.setText(
				"""
				<html>Next, let's set up some Roles for your CanvaCord instance. These are the Roles that
				CanvaCord will create in Discord and use for sending notifications to users when new
				Canvas objects are found during the fetch stage. In the next step, you'll configure what
				notifications should be sent, when, and to which of the roles you create here.</html>
				"""
		);
		description.setFont(CanvaCordFonts.LABEL_FONT_BIGGER_THAN_SMALL_BUT_SMALLER_THAN_MEDIUM);
		description.setBounds(20, 0, WizardCard.WIDTH - 40, 100);
		contentPanel.add(description);

		JScrollPane rolesPane = new JScrollPane();
		rolesPane.setBounds(20, 120, 450, 220);
		contentPanel.add(rolesPane);

		rolesList = new JList<>();
		rolesPane.getViewport().setView(rolesList);

		int buttonX = 508;
		int buttonY = 134;
		int buttonSize = 48;
		int buttonSpacing = 20;

		newRoleButton = new JButton(new ImageIcon("resources/new_icon.png"));
		newRoleButton.setBounds(buttonX, buttonY, buttonSize, buttonSize);
		contentPanel.add(newRoleButton);

		editRoleButton = new JButton(new ImageIcon("resources/edit_icon_wip.png"));
		editRoleButton.setBounds(buttonX, buttonY + buttonSize + buttonSpacing, buttonSize, buttonSize);
		contentPanel.add(editRoleButton);

		deleteRoleButton = new JButton(new ImageIcon("resources/delete_icon_non_beveled.png"));
		deleteRoleButton.setBounds(buttonX, buttonY + (buttonSize + buttonSpacing) * 2, buttonSize, buttonSize);
		contentPanel.add(deleteRoleButton);

		// SHOWING ROLES IN THE LIST
		rolesList.setFixedCellHeight(-1);
		rolesList.setLayoutOrientation(JList.VERTICAL);
		rolesList.setCellRenderer(new RoleCellRenderer(rolesList));

		// TODO



	}

	@Override
	protected void initLogic() {

		// init the list
		roles = new ArrayList<>();

		// ================ ADDING NEW ROLES ================
		newRoleButton.addActionListener(event -> {
			RoleCreateDialog.buildRole().ifPresent(role -> {
				roles.add(role);
				updateRolesList();
			});
		});

		editRoleButton.addActionListener(event -> {
			CanvaCordRole roleToEdit = rolesList.getSelectedValue();
			int index = rolesList.getSelectedIndex();
			RoleCreateDialog.editRole(roleToEdit).ifPresent(editedRole -> {
				roles.set(index, editedRole);
				updateRolesList();
			});
		});

		deleteRoleButton.addActionListener(event -> {
			if (UserInput.askToConfirm("Delete this role?", "Confirm Delete")) {
				int index = rolesList.getSelectedIndex();
				roles.remove(index);
				updateRolesList();
			}
		});

	}

	private void updateRolesList() {

		ListModel<CanvaCordRole> roleListModel = new AbstractListModel<>() {
			@Override
			public int getSize() {
				return roles.size();
			}

			@Override
			public CanvaCordRole getElementAt(int index) {
				return roles.get(index);
			}
		};

		rolesList.setModel(roleListModel);
		rolesList.updateUI();

	}

	private static class RoleCellRenderer extends JLabel implements ListCellRenderer<CanvaCordRole> {

		private static final int SIZE = 50;

		public RoleCellRenderer(JList parent) {
			setPreferredSize(new Dimension(parent.getWidth(), SIZE));
		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(SIZE, SIZE);
		}

		@Override
		public Component getListCellRendererComponent(JList<? extends CanvaCordRole> list, CanvaCordRole role, int index, boolean isSelected, boolean cellHasFocus) {

			String name = role.getName();
			Color color = role.getColor();

			ColorIcon icon = new ColorIcon(color, SIZE, SIZE);
			icon.setDoBorder(true);

			setText(name);
			setIcon(icon);

			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}

			setEnabled(list.isEnabled());
			setFont(CanvaCordFonts.LABEL_FONT_MEDIUM);
			setOpaque(true);

			return this;

		}
	}

	public JSONArray getRolesArray() {
		JSONArray result = new JSONArray();
		for (CanvaCordRole role : roles) {
			result.put(role.getJSON());
		}
		return result;
	}
}
