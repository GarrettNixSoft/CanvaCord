package org.canvacord.gui.component;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Paths;

public class ImagePanel extends JPanel {

	private BufferedImage image;

	public ImagePanel(String pathToImage) {
		try {
			image = ImageIO.read(Paths.get(pathToImage).toFile());
		}
		catch (IOException e) {
			image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
	}
}
