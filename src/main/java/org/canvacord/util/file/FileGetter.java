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

	public static Optional<File> getFile(String startingDir, String filterDesc, String... extensions) {
		return promptForFile(false, startingDir, filterDesc, extensions);
	}

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
