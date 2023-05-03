package org.canvacord.util.resources;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class ImageLoader {

	public static ImageIcon loadIcon(String iconPath) {
		final URL url = Thread.currentThread().getContextClassLoader().getResource(iconPath);
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage(url));
	}

}
