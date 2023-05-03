package org.canvacord.gui.component;

import org.canvacord.main.CanvaCord;
import org.canvacord.util.resources.ImageLoader;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Paths;

public class ImagePanel extends JPanel {

	private BufferedImage image;

	public ImagePanel(BufferedImage image) {
		this.image = image;
	}

	public static ImagePanel loadFromResources(String pathToImage) {
		BufferedImage image;
		try {
			Image loadedImage = ImageLoader.loadImage(pathToImage);
			image = new BufferedImage(loadedImage.getWidth(null), loadedImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
			image.getGraphics().drawImage(loadedImage, 0, 0, null);
		}
		catch (Exception e) {
			image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
			e.printStackTrace();
			CanvaCord.explode();
		}
		return new ImagePanel(image);
	}

	public static ImagePanel loadFromDisk(String pathToImage) {
		BufferedImage image;
		try {
			image = ImageIO.read(Paths.get(pathToImage).toFile());
		}
		catch (IOException e) {
			image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		}
		return new ImagePanel(image);
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (image != null)
			g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
	}
}
