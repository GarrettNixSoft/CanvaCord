package org.canvacord.persist;

import org.canvacord.util.file.FileUtil;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * The ConfigLoader loads all root-level configurations for CanvaCord.
 * These are the settings independent of the various instances, such as
 * the API keys and whether to run instances when CanvaCord starts.
 */
public class ConfigManager {

    private static JSONObject configJSON;

	public static Optional<JSONObject> loadConfig() {
        File target = Paths.get("config/secrets.json").toFile();
        Optional<JSONObject> loadedFile = FileUtil.getJSON(target);
        loadedFile.ifPresent(jsonObject -> configJSON = jsonObject);
        return loadedFile;
    }

    public static JSONObject getConfig() {
        if (configJSON == null) loadConfig();
        return configJSON;
    }

    public static boolean writeTokenData(JSONObject configData) {
        // TODO: look into encrypting this in some way, maybe make it optional
        File target = Paths.get("config/secrets.json").toFile();
        if (FileUtil.writeJSON(configData, target)) {
            configJSON = configData;
            return true;
        }
        else return false;
    }

    public static String getCanvasToken() {
        return configJSON.getString("canvas_token");
    }

    public static String getDiscordToken() {
        return configJSON.getString("discord_token");
    }

    public static String getUserID() {
        return configJSON.getString("id");
    }

    public static String getCanvasURL() {
        return configJSON.getString("url");
    }
}
