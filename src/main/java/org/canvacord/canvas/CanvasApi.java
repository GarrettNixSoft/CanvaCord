package org.canvacord.canvas;

import edu.ksu.canvas.CanvasApiFactory;
import edu.ksu.canvas.oauth.NonRefreshableOauthToken;
import edu.ksu.canvas.oauth.OauthToken;

public class CanvasApi {

	private final OauthToken token;
	private final CanvasApiFactory apiFactory;

	public CanvasApi(String canvasURL, String tokenStr) {
		token = new NonRefreshableOauthToken(tokenStr);
		apiFactory = new CanvasApiFactory(canvasURL);
	}

}
