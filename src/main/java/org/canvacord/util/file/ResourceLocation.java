package org.canvacord.util.file;

import java.io.InputStream;
import java.net.URL;

/**
 * NOTE: This class obtained from the Slick2D library -- <a href="https://slick.ninjacave.com/">available here</a>
 */
public interface ResourceLocation {

	InputStream getResourceAsStream(String var1);

	URL getResource(String var1);

}
