package org.canvacord.gui.options;

import net.miginfocom.swing.MigLayout;
import org.canvacord.exception.CanvaCordException;
import org.canvacord.gui.CanvaCordFonts;
import org.canvacord.gui.GuiDataStore;
import org.canvacord.gui.dialog.MultiErrorDialog;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class OptionsPanel extends JDialog {

	public static final int MIN_WIDTH = 600, MAX_WIDTH = 1000;
	public static final int MIN_HEIGHT = 600, MAX_HEIGHT = 1000;

	private boolean cancelled;

	private JTree optionPageTree;
	private JPanel buttonPanel;
	private JPanel pagePanel;
	private CardLayout pageLayout;

	private DefaultMutableTreeNode topNode;

	private JButton okButton;
	private JButton cancelButton;
	private JButton applyButton;

	private List<OptionPage> pages;
	private Map<String, OptionPage> pageMap;
	private Map<OptionPage, DefaultMutableTreeNode> nodeMap;
	private OptionPage currentPage;

	protected GuiDataStore dataStore;

	public OptionsPanel(String title, int width, int height) {
		// title the window
		setTitle(title);
		// determine the resizing limits
		setResizable(true);
		setPreferredSize(new Dimension(width, height));
		setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
		setMaximumSize(new Dimension(MAX_WIDTH, MAX_HEIGHT));
		// make this window modal
		setModalityType(ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				cancelled = true;
				setVisible(false);
			}
		});
		// init the data store
		this.dataStore = new GuiDataStore();
		// prepare for cards
		this.pages = new ArrayList<>();
		this.pageMap = new HashMap<>();
		this.nodeMap = new HashMap<>();
		// build the common layout for the whole menu
		buildLayout();
		updatePageList();
		// size everything
		pack();
		// center in window
		setLocationRelativeTo(null);
	}

	public void addOptionPage(OptionPage page) {
		if (pageMap.containsKey(page.getName())) throw new CanvaCordException("Duplicate of page " + page.getName());
		pages.add(page);
		pageMap.put(page.getName(), page);
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(page);
		nodeMap.put(page, node);
		topNode.add(node);
		pagePanel.add(page.getName(), page);
	}

	public void addOptionPage(OptionPage parent, OptionPage page) {
		if (!pageMap.containsKey(parent.getName()))
			throw new CanvaCordException("Parent node passed does not exist!");
		if (pageMap.containsKey(page.getName()))
			throw new CanvaCordException("Duplicate of page " + page.getName());
		pages.add(page);
		pageMap.put(page.getName(), page);
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(page);
		nodeMap.put(page, node);
		nodeMap.get(parent).add(node);
		pagePanel.add(page.getName(), page);
	}

	public boolean verifyInputs() {

		if (cancelled)
			return false;

		List<NamedError> inputErrors = new ArrayList<>();

		for (OptionPage card : pages) {
			try {
				card.verifyInputs();
			}
			catch (Exception e) {
				inputErrors.add(new NamedError(card.getName(), e));
			}
		}

		// TODO build an error window listing all errors
		if (inputErrors.isEmpty()) {
			complete(true);
			save();
			return true;
		}

		else {
			MultiErrorDialog.showMultiErrorDialog(
					"Configuration Errors",
					"<html>The following errors were found when trying to save your modified configuration:</html>",
					inputErrors);
			complete(false);
			return false;
		}
	}

	public void run() {
		// show the menu
		setVisible(true);
		// run the complete task
//		if (!cancelled) {
//			boolean verified = verifyInputs();
//			complete(verified);
//			if (verified) save();
//		}
		// the user exits, dispose of the window
		dispose();
	}

	protected void selectFirstPage() {
		updatePageList();
		// select the first page
		optionPageTree.setSelectionRow(0);
		optionPageTree.repaint();
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
		if (currentPage != null) {
			currentPage.onNavigateAway();
		}
		currentPage = page;
		currentPage.onNavigateTo();
		// populate the content panel
		pageLayout.show(pagePanel, currentPage.getName());
		repaint();
	}

	protected abstract void complete(boolean success);
	protected abstract void save();

	private void buildLayout() {
		// use a border layout for arranging panels
		setLayout(new MigLayout("", "[220px][grow]", "[grow][]"));
		// build the page list and put it on the left
		JScrollPane listScrollPane = new JScrollPane();
//		listScrollPane.setMinimumSize(new Dimension(MIN_LIST_WIDTH, MIN_HEIGHT - 40));
//		listScrollPane.setPreferredSize(new Dimension(DEFAULT_LIST_WIDTH, getPreferredSize().height - 40));
//		listScrollPane.setMaximumSize(new Dimension(MAX_LIST_WIDTH, MAX_HEIGHT - 40));
		// prepare the top of the tree
		topNode = new DefaultMutableTreeNode(null);
		// use a tree for the list
		optionPageTree = new JTree();
		optionPageTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		optionPageTree.addTreeSelectionListener(selection -> {
			// grab the selection
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) optionPageTree.getLastSelectedPathComponent();
			// return if there is no selection
			if (node == null) return;
			// get the selected page
			OptionPage selectedPage = (OptionPage) node.getUserObject();
			navigateToPage(selectedPage);
		});
		optionPageTree.setRootVisible(false);
		optionPageTree.setShowsRootHandles(true);
		optionPageTree.setRowHeight(24);
		optionPageTree.setFont(CanvaCordFonts.LABEL_FONT_MEDIUM);
		add(listScrollPane, "cell 0 0, width 240px:240px:320px, growx, growy");
		listScrollPane.getViewport().setView(optionPageTree);
		// build a panel to hold everything to the right of the list
		pagePanel = new JPanel(pageLayout = new CardLayout());
		pagePanel.setBorder(new EtchedBorder());
//		pagePanel.setMinimumSize(new Dimension(MIN_WIDTH - 120, MIN_HEIGHT - 40));
//		pagePanel.setPreferredSize(new Dimension(getPreferredSize().width - 120, getPreferredSize().height - 40));
//		pagePanel.setMaximumSize(new Dimension(MAX_WIDTH - 150, MAX_HEIGHT - 40));
		add(pagePanel, "cell 1 0, width 400px:600px:800px, growx, growy");
		// build the button panel and put it on the bottom
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.setMinimumSize(new Dimension(MIN_WIDTH, 40));
		buttonPanel.setPreferredSize(new Dimension(getPreferredSize().width, 40));
		buttonPanel.setMaximumSize(new Dimension(MAX_WIDTH, 40));
		initButtons();
		add(buttonPanel, "cell 0 1 2 1");
		// build the content panel, put it in the "center," and put the starting card in it
		// ...
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
		optionPageTree.setModel(new DefaultTreeModel(topNode));
	}

}
