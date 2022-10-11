package org.canvacord.canvas;

import edu.ksu.canvas.CanvasApiFactory;
import edu.ksu.canvas.oauth.NonRefreshableOauthToken;
import edu.ksu.canvas.oauth.OauthToken;
import org.canvacord.util.file.FileUtil;
import org.canvacord.util.string.StringConverter;
import org.canvacord.util.string.StringUtils;

import java.nio.file.Paths;

public class CanvasApi {

	private static CanvasApi instance;

	private final OauthToken TOKEN;
	private final CanvasApiFactory API;

	private CanvasApi(String canvasURL, String tokenStr) {
		TOKEN = new NonRefreshableOauthToken(tokenStr);
		API = new CanvasApiFactory(canvasURL);
	}

	public static CanvasApi getInstance() {
		if (instance == null) {
			// TODO: LOAD URL AND TOKEN FROM CONFIG
			String url = "csulb.instructure.com";
			String token = StringConverter.combineAll(FileUtil.getFileData(Paths.get("config/token-canvas.txt").toFile()));
			instance = new CanvasApi(url, token);
		}
		return instance;
	}

}
