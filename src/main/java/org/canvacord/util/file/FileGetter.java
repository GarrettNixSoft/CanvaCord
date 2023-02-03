package org.canvacord.util.file;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.Optional;

public class FileGetter {

	static {
		try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
		catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException ignored) {}
	}

	/**
	 * Prompt the user to select a file to open.
	 * @param startingDir the directory to open the file dialog in by default
	 * @param filterDesc the description that will show in the file type box at the bottom of the dialog
	 * @param extensions the file extensions that should be visible in the file chooser. If no extensions are provided, all files will be visible.
	 * @return the file the user selected, or an empty Optional if they cancel or close the dialog
	 */
	public static Optional<File> getFile(String startingDir, String filterDesc, String... extensions) {
		return promptForFile(false, startingDir, filterDesc, extensions);
	}

	/**
	 * Prompt the user to select a file to write to.
	 * @param startingDir the directory to open the file dialog in by default
	 * @param filterDesc the description that will show in the file type box at the bottom of the dialog
	 * @param extensions the file extensions that should be visible in the file chooser. If no extensions are provided, all files will be visible.
	 * @return the file the user selected, or an empty Optional if they cancel or close the dialog
	 */
	public static Optional<File> getSaveDestination(String startingDir, String filterDesc, String... extensions) {
		return promptForFile(true, startingDir, filterDesc, extensions);
	}

	private static Optional<File> promptForFile(boolean save, String startingDir, String filterDesc, String... extensions) {

		JFileChooser fileChooser = new JFileChooser(startingDir);

		FileFilter fileFilter = new FileFilter() {
			@Override
			public boolean accept(File f) {
				if (extensions.length == 0)
					return true;
				else for (String ext : extensions) {
					if (f.getName().toLowerCase().endsWith(ext)) return true;
					else if (f.isDirectory()) return true;
				}
				return false;
			}

			@Override
			public String getDescription() {
				return filterDesc;
			}
		};

//		fileChooser.addChoosableFileFilter(fileFilter);
		fileChooser.setFileFilter(fileFilter);

		JFrame parent = new JFrame("Choose a File");
//		parent.setIconImage(Config.ICON_IMAGE);
		parent.setAlwaysOnTop(true);

		int selection = save ? fileChooser.showSaveDialog(parent) : fileChooser.showOpenDialog(parent);
		File selectedFile = fileChooser.getSelectedFile();

		parent.dispose();

		if (selection == JFileChooser.APPROVE_OPTION)
			return Optional.of(selectedFile);

		else if (selection == JFileChooser.CANCEL_OPTION)
			return Optional.empty();

		else return Optional.empty();

	}

	/**
	 * Prompt the user to choose a folder.
	 * @param startingDir the directory to open the file dialog in by default
	 * @return the folder the user chose, or an empty Optional if they cancel or close the dialog
	 */
	public static Optional<File> getDirectory(String startingDir) {

		JFileChooser fileChooser = new JFileChooser(startingDir);
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		JFrame parent = new JFrame("Choose a Folder");
//		parent.setIconImage(Config.ICON_IMAGE);
		parent.setAlwaysOnTop(true);

		int selection = fileChooser.showOpenDialog(parent);
		File selectedFile = fileChooser.getSelectedFile();

		parent.dispose();

		if (selection == JFileChooser.APPROVE_OPTION)
			return Optional.of(selectedFile);

		else if (selection == JFileChooser.CANCEL_OPTION)
			return Optional.empty();

		else return Optional.empty();

	}

}
