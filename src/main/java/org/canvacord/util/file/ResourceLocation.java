package org.canvacord.util.file;

import java.io.InputStream;
import java.net.URL;

/*
	Adapted from the Slick2D library.
 */
public interface ResourceLocation {

	InputStream getResourceAsStream(String var1);

	URL getResource(String var1);

}
