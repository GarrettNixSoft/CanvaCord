package org.canvacord.gui.options.page;

import net.miginfocom.swing.MigLayout;
import org.canvacord.entity.CanvaCordRole;
import org.canvacord.exception.CanvaCordException;
import org.canvacord.gui.CanvaCordFonts;
import org.canvacord.gui.component.ColorIcon;
import org.canvacord.gui.dialog.RoleCreateDialog;
import org.canvacord.gui.options.OptionPage;
import org.canvacord.util.input.UserInput;
import org.canvacord.util.resources.ImageLoader;
import org.json.JSONArray;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RolesPage extends OptionPage {

	private List<CanvaCordRole> roles;
	private Map<String, CanvaCordRole> rolesByName;
	private List<CanvaCordRole> registeredRoles;
	private JList<CanvaCordRole> rolesList;

	private JButton newRoleButton;
	private JButton editRoleButton;
	private JButton deleteRoleButton;

	public RolesPage() {
		super("Roles");
	}

	@Override
	protected void buildGUI() {

		roles = new ArrayList<>();
		rolesByName = new HashMap<>();
		registeredRoles = new ArrayList<>();

		setLayout(new MigLayout("insets 10 10 10 10",
				"[grow][]", "[]".repeat(20)));

		JLabel rolesLabel = new JLabel("Configured Roles:");
		rolesLabel.setFont(CanvaCordFonts.bold(CanvaCordFonts.LABEL_FONT_MEDIUM));
		add(rolesLabel, "cell 0 0");

		rolesList = new JList<>();
		rolesList.setFixedCellHeight(-1);
		rolesList.setLayoutOrientation(JList.VERTICAL);
		rolesList.setCellRenderer(new RoleCellRenderer(this));

		JScrollPane rolesPane = new JScrollPane();
		rolesPane.getViewport().setView(rolesList);

		add(rolesPane, "cell 0 1 5 10, growx, growy");

		newRoleButton = new JButton(ImageLoader.loadIcon("new_icon.png"));
		add(newRoleButton, "cell 9 3");

		editRoleButton = new JButton(ImageLoader.loadIcon("edit_icon_wip.png"));
		add(editRoleButton, "cell 9 6");

		deleteRoleButton = new JButton(ImageLoader.loadIcon("delete_icon_non_beveled.png"));
		add(deleteRoleButton, "cell 9 9");

	}

	@Override
	protected void initLogic() {

		newRoleButton.addActionListener(event -> {
			RoleCreateDialog.buildRole().ifPresent(
					newRole -> {
						if (rolesByName.containsKey(newRole.getName())) {
							UserInput.showErrorMessage("Role names must be unique!", "Duplicate Name");
							return;
						}
						roles.add(newRole);
						rolesByName.put(newRole.getName(), newRole);
						updateRolesList();
					}
			);
		});

		editRoleButton.addActionListener(event -> {
			CanvaCordRole roleToEdit = rolesList.getSelectedValue();
			if (roleToEdit == null) return;
			rolesByName.remove(roleToEdit.getName());
			int index = rolesList.getSelectedIndex();
			RoleCreateDialog.editRole(roleToEdit).ifPresentOrElse(
					editedRole -> {
						roles.set(index, editedRole);
						rolesByName.put(editedRole.getName(), editedRole);
						updateRolesList();
					},
					() -> rolesByName.put(roleToEdit.getName(), roleToEdit));
		});

		deleteRoleButton.addActionListener(event -> {
			int index = rolesList.getSelectedIndex();
			if (index == -1) return;
			if (UserInput.askToConfirm("Delete this role?", "Confirm Delete")) {
				CanvaCordRole removed = roles.remove(index);
				rolesByName.remove(removed.getName());
				updateRolesList();
			}
		});
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void prefillGUI() {

		roles = (ArrayList<CanvaCordRole>) dataStore.get("roles");
		registeredRoles = (ArrayList<CanvaCordRole>) dataStore.get("registered_roles");

		updateRolesList();

	}

	@Override
	protected void verifyInputs() throws Exception {

		if (roles.isEmpty())
			throw new CanvaCordException("You must create at least one Role.");

		dataStore.store("configured_roles", getRolesArray());

	}

	private static class RoleCellRenderer extends JLabel implements ListCellRenderer<CanvaCordRole> {

		private final RolesPage parentPage;

		public RoleCellRenderer(RolesPage parentPage) {
			this.parentPage = parentPage;
		}

		@Override
		public Component getListCellRendererComponent(JList<? extends CanvaCordRole> list, CanvaCordRole role, int index, boolean isSelected, boolean cellHasFocus) {

			ColorIcon icon = new ColorIcon(role.getColor(), 50, 50);
			icon.setDoBorder(true);

			setIcon(icon);

			setText("<html><b>" + role.getName() + "</b><br/>" + "Registered: " + parentPage.registeredRoles.contains(role) + "</html>");

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

	private JSONArray getRolesArray() {
		JSONArray result = new JSONArray();
		for (CanvaCordRole role : roles) {
			result.put(role.getJSON());
		}
		return result;
	}

}
