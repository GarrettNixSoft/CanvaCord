package org.canvacord.util.time;

import org.canvacord.gui.CanvaCordFonts;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LongTaskDialog extends JDialog {

	private final LongTask task;

	public LongTaskDialog(LongTask task, String message, String title) {
		super();
		this.task = task;
		setMinimumSize(new Dimension(100, 40));
		setModalityType(ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setResizable(false);
		// build
		setTitle(title);
		buildDialog(message);
		// position
		setLocationRelativeTo(null);
	}

	private void buildDialog(String message) {
		setLayout(new BorderLayout());
		JLabel label = new JLabel(message);
		label.setFont(CanvaCordFonts.LABEL_FONT_SMALL);
		label.setBorder(new EmptyBorder(10, 10, 10, 10));
		add(label, BorderLayout.CENTER);
		pack();
	}

	public void runTask() {
		System.out.println("Running long task...");
		long time = Profiler.executeProfiled(task::execute);
		System.out.println("Ran long task in " + time + "ms");
		int minTime = 100;
		if (time < minTime) {
			try {
				Thread.sleep(minTime - time);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		setVisible(false);
	}

	public static void runLongTask(LongTask task, String message, String title) {
		LongTaskDialog dialog = new LongTaskDialog(task, message, title);
		new Thread(
				dialog::runTask
		).start();
		dialog.setVisible(true);
		dialog.dispose();
	}

}
