package org.canvacord.gui.options;

import org.canvacord.gui.CanvaCordFonts;
import org.canvacord.gui.GuiDataStore;
import org.canvacord.util.gui.ComponentUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class OptionsPanel extends JDialog {

	public static final int MIN_WIDTH = 400, MAX_WIDTH = 1000;
	public static final int MIN_HEIGHT = 400, MAX_HEIGHT = 1000;

	public static final int MIN_LIST_WIDTH = 120, DEFAULT_LIST_WIDTH = 150, MAX_LIST_WIDTH = 180;

	private boolean cancelled;

	private JList<String> optionPageList;
	private JPanel buttonPanel;
	private JPanel contentPanel;

	private JButton okButton;
	private JButton cancelButton;
	private JButton applyButton;

	private List<OptionPage> pages;
	private Map<String, OptionPage> pageMap;
	private OptionPage currentPage;

	protected GuiDataStore dataStore;

	public OptionsPanel(String title, int width, int height) {
		// title the window
		setTitle(title);
		// determine the resizing limits
		setPreferredSize(new Dimension(width, height));
		setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
		setMaximumSize(new Dimension(MAX_WIDTH, MAX_HEIGHT));
		// make this window modal
		setModalityType(ModalityType.APPLICATION_MODAL);
		// init the data store
		this.dataStore = new GuiDataStore();
		// prepare for cards
		this.pages = new ArrayList<>();
		this.pageMap = new HashMap<>();
		// build the common layout for the whole menu
		buildLayout();
		updatePageList();
		// size everything
		pack();
		// center in window
		setLocationRelativeTo(null);
	}

	public void addOptionPage(OptionPage card) {
		pages.add(card);
		pageMap.put(card.getName(), card);
	}

	public boolean verifyInputs() {

		if (cancelled)
			return false;

		List<Exception> inputErrors = new ArrayList<>();

		for (OptionPage card : pages) {
			try {
				card.verifyInputs();
			}
			catch (Exception e) {
				inputErrors.add(e);
			}
		}

		// TODO build an error window listing all errors

		return inputErrors.isEmpty();
	}

	public void run() {
		// show the menu
		setVisible(true);
		// the user exits, dispose of the window
		dispose();
		// run the complete task
		complete(verifyInputs());
	}

	protected void selectFirstPage() {
		// select the first page
		optionPageList.setSelectedIndex(0);
		optionPageList.repaint();
		// open the first page
		currentPage = pages.get(0);
		navigateToPage(currentPage);
	}

	protected void provideDataStore() {
		for (OptionPage page : pages) {
			page.provideDataStore(dataStore);
		}
	}

	protected void prefillGUI() {
		for (OptionPage page : pages) {
			page.prefillGUI();
		}
	}

	protected void navigateToPage(OptionPage page) {
		if (page == currentPage) return;
		if (currentPage != null) currentPage.onNavigateAway();
		currentPage = page;
		currentPage.onNavigateTo();
		// populate the content panel
		if (contentPanel.getComponents().length == 1) contentPanel.remove(0);
		contentPanel.add(currentPage, BorderLayout.CENTER);
	}

	protected abstract void complete(boolean success);

	private void buildLayout() {
		// use a border layout for arranging panels
		setLayout(new BorderLayout());
		// build the page list and put it on the left
		optionPageList = new JList<>();
		optionPageList.setMinimumSize(new Dimension(MIN_LIST_WIDTH, MIN_HEIGHT - 40));
		optionPageList.setPreferredSize(new Dimension(DEFAULT_LIST_WIDTH, getPreferredSize().height - 40));
		optionPageList.setMaximumSize(new Dimension(MAX_LIST_WIDTH, MAX_HEIGHT - 40));
		optionPageList.setFixedCellHeight(-1);
		optionPageList.setLayoutOrientation(JList.VERTICAL);
		optionPageList.setCellRenderer(new DefaultCellRenderer());
		optionPageList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		optionPageList.addListSelectionListener(selection -> {
			OptionPage selectedPage = pages.get(selection.getFirstIndex());
			navigateToPage(selectedPage);
		});
		add(optionPageList, BorderLayout.WEST);
		// build a panel to hold everything to the right of the list
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BorderLayout());
		rightPanel.setMinimumSize(new Dimension(MIN_WIDTH - 120, MIN_HEIGHT - 40));
		rightPanel.setPreferredSize(new Dimension(getPreferredSize().width - 120, getPreferredSize().height - 40));
		rightPanel.setMaximumSize(new Dimension(MAX_WIDTH - 150, MAX_HEIGHT - 40));
		add(rightPanel, BorderLayout.EAST);
		// build the button panel and put it on the bottom
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.setMinimumSize(new Dimension(MIN_WIDTH, 40));
		buttonPanel.setPreferredSize(new Dimension(getPreferredSize().width, 40));
		buttonPanel.setMaximumSize(new Dimension(MAX_WIDTH, 40));
		initButtons();
		rightPanel.add(buttonPanel, BorderLayout.SOUTH);
		// build the content panel, put it in the "center," and put the starting card in it
		contentPanel = new JPanel();
		contentPanel.setLayout(new BorderLayout());
		contentPanel.setBorder(new LineBorder(Color.BLACK));
		contentPanel.setMinimumSize(new Dimension(MIN_WIDTH - 120, MIN_HEIGHT - 40));
		contentPanel.setPreferredSize(new Dimension(getPreferredSize().width - 120, getPreferredSize().height - 40));
		contentPanel.setMaximumSize(new Dimension(MAX_WIDTH - 150, MAX_HEIGHT - 40));
		rightPanel.add(contentPanel, BorderLayout.CENTER);
	}

	private void initButtons() {
		// button size and spacing
		Dimension buttonSize = new Dimension(80, 28);
		int buttonSpacing = 10;
		// build the buttons
		okButton = new JButton("OK");
		okButton.setPreferredSize(buttonSize);
		okButton.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		cancelButton = new JButton("Cancel");
		cancelButton.setPreferredSize(buttonSize);
		cancelButton.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		applyButton = new JButton("Apply");
		applyButton.setPreferredSize(buttonSize);
		applyButton.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		// lay them out
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(okButton);
		buttonPanel.add(Box.createHorizontalStrut(buttonSpacing));
		buttonPanel.add(cancelButton);
		buttonPanel.add(Box.createHorizontalStrut(buttonSpacing));
		buttonPanel.add(applyButton);
		buttonPanel.add(Box.createHorizontalStrut(buttonSpacing));
		// program their behavior
		okButton.addActionListener(event -> {
			if (verifyInputs())
				setVisible(false);
		});
		applyButton.addActionListener(event -> {
			verifyInputs();
		});
		cancelButton.addActionListener(event -> {
			cancelled = true;
			setVisible(false);
		});
	}

	private void updatePageList() {
		optionPageList.setModel(new AbstractListModel<>() {
			@Override
			public int getSize() {
				return pages.size();
			}

			@Override
			public String getElementAt(int index) {
				return pages.get(index).getName();
			}
		});
	}

	protected void provideCellRenderer(ListCellRenderer<String> renderer) {
		optionPageList.setCellRenderer(renderer);
	}

	private static class DefaultCellRenderer extends JLabel implements ListCellRenderer<String> {

		@Override
		public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus) {

			setFont(CanvaCordFonts.LABEL_FONT_MEDIUM);
			setText(value);
			setPreferredSize(new Dimension(120, 36));
			setOpaque(true);

			setBorder(new EmptyBorder(4,4,4,4));

			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}

			return this;
		}
	}

}
