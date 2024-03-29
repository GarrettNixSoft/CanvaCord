package org.canvacord.util.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * NOTE: This class obtained from the Slick2D library -- <a href="https://slick.ninjacave.com/">available here</a>
 */
public record FileSystemLocation(File root) implements ResourceLocation {

	public URL getResource(String ref) {
		try {
			File file = new File(this.root, ref);
			if (!file.exists()) {
				file = new File(ref);
			}

			return !file.exists() ? null : file.toURI().toURL();
		} catch (IOException var3) {
			return null;
		}
	}

	public InputStream getResourceAsStream(String ref) {
		try {
			File file = new File(this.root, ref);
			if (!file.exists()) {
				file = new File(ref);
			}

			return new FileInputStream(file);
		} catch (IOException var3) {
			return null;
		}
	}
}
