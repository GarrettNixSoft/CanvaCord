package org.canvacord.util.file;

import java.io.InputStream;
import java.net.URL;

/*
	Copied from the Slick2D library.
 */
public class ClasspathLocation implements ResourceLocation {

	public ClasspathLocation() {
	}

	public URL getResource(String ref) {
		String cpRef = ref.replace('\\', '/');
		return ResourceLoader.class.getClassLoader().getResource(cpRef);
	}

	public InputStream getResourceAsStream(String ref) {
		String cpRef = ref.replace('\\', '/');
		return ResourceLoader.class.getClassLoader().getResourceAsStream(cpRef);
	}
}
