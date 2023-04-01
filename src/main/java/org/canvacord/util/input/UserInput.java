package org.canvacord.util.input;

import javax.swing.*;
import java.awt.*;

public class UserInput {

	private static JFrame buildParent() {
		JFrame frame = new JFrame();
		frame.setAlwaysOnTop(true);
		return frame;
	}

	/**
	 * Prompt the user to enter a String of input.
	 * @param prompt the prompt message to show the user
	 * @return the user's input as a String
	 */
	public static String getUserString(String prompt) {
		JFrame frame = buildParent();
		String result = JOptionPane.showInputDialog(frame, prompt);
		frame.dispose();
		return result;
	}

	/**
	 * Prompt the user to enter an integer as input.
	 * Will repeat each time the user enters a value that
	 * causes {@code Integer.parseInt()} to fail. If the
	 * user chooses to cancel, 0 will be returned.
	 * @param prompt the prompt message to show the user
	 * @return the user's input as an int
	 */
	public static int getUserInt(String prompt) {
		while (true) {
			String response = getUserString(prompt);

			if (response == null) return 0;

			try {
				return Integer.parseInt(response);
			}
			catch (NumberFormatException e) {
				showErrorMessage("Please enter a valid integer.", "Invalid Input");
			}
		}
	}

	public static String getUserChoiceFromList(String prompt, String title, String[] options) {

		JFrame frame = buildParent();

		String response = (String) JOptionPane.showInputDialog(frame, prompt, title,
				JOptionPane.PLAIN_MESSAGE,
				null, options, options[0]);

		frame.dispose();

		return response;

	}

	public static void showErrorMessage(String message, String title) {
		JFrame frame = buildParent();
		JOptionPane.showMessageDialog(frame, message, title, JOptionPane.ERROR_MESSAGE);
		frame.dispose();
	}

	public static void showWarningMessage(String message, String title) {
		JFrame frame = buildParent();
		JOptionPane.showMessageDialog(frame, message, title, JOptionPane.WARNING_MESSAGE);
		frame.dispose();
	}

	public static void showMessage(String message, String title) {
		JFrame frame = buildParent();
		JOptionPane.showMessageDialog(frame, message, title, JOptionPane.PLAIN_MESSAGE);
		frame.dispose();
	}

	public static void showExceptionWarning(Exception e) {
		JFrame frame = buildParent();
		JOptionPane.showMessageDialog(frame, e.getMessage(), e.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
		frame.dispose();
		e.printStackTrace();
	}

	public static boolean askToConfirm(String message, String title) {
		JFrame frame = buildParent();
		int response = JOptionPane.showConfirmDialog(frame, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		frame.dispose();
		return response == JOptionPane.YES_OPTION;
	}

	public static int askToConfirmCustom(String message, String title, String[] options, int defaultOption, int messageType) {
		JFrame frame = buildParent();
		int response = JOptionPane.showOptionDialog(frame, message, title, JOptionPane.DEFAULT_OPTION, messageType, null, options, options[defaultOption]);
		frame.dispose();
		return response;
	}

	public static int askToConfirmCustom(Component parent, String message, String title, String[] options, int defaultOption, int messageType) {
		return JOptionPane.showOptionDialog(parent, message, title, JOptionPane.DEFAULT_OPTION, messageType, null, options, options[defaultOption]);
	}

}
