package org.canvacord.util.file;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * NOTE: This class obtained from the Slick2D library -- <a href="https://slick.ninjacave.com/">available here</a>
 */
public class ResourceLoader {

	private static final ArrayList<ResourceLocation> locations = new ArrayList<>();

	public ResourceLoader() {
	}

	public static void addResourceLocation(ResourceLocation location) {
		locations.add(location);
	}

	public static void removeResourceLocation(ResourceLocation location) {
		locations.remove(location);
	}

	public static void removeAllResourceLocations() {
		locations.clear();
	}

	public static InputStream getResourceAsStream(String ref) {
		InputStream in = null;

		for (ResourceLocation location : locations) {
			in = location.getResourceAsStream(ref);
			if (in != null) {
				break;
			}
		}

		if (in == null) {
			throw new RuntimeException("Resource not found: " + ref);
		} else {
			return new BufferedInputStream(in);
		}
	}

	public static boolean resourceExists(String ref) {
		URL url;

		for (ResourceLocation location : locations) {
			url = location.getResource(ref);
			if (url != null) {
				return true;
			}
		}

		return false;
	}

	public static URL getResource(String ref) {
		URL url = null;

		for (ResourceLocation location : locations) {
			url = location.getResource(ref);
			if (url != null) {
				break;
			}
		}

		if (url == null) {
			throw new RuntimeException("Resource not found: " + ref);
		} else {
			return url;
		}
	}

	static {
		locations.add(new ClasspathLocation());
		locations.add(new FileSystemLocation(new File(".")));
	}
}
