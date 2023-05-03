package org.canvacord.util.resources;

import org.canvacord.main.CanvaCord;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

public class ImageLoader {

	public static ImageIcon loadIcon(String iconPath) {
		return new ImageIcon(loadImage(iconPath));
	}

	public static Image loadImage(String imagePath) {
		final URL url = Thread.currentThread().getContextClassLoader().getResource(imagePath);
		try {
			return ImageIO.read(url);
		}
		catch (IOException | NullPointerException e) {
			e.printStackTrace();
			CanvaCord.explode(e.getMessage());
			return null;
		}
	}

}
