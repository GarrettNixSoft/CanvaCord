package org.canvacord.persist;

import org.canvacord.main.CanvaCord;
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

    private static boolean verifyConfig(JSONObject loadedConfig) {
        if (!loadedConfig.has("canvas_token")) return false;
        if (!loadedConfig.has("discord_token")) return false;
        if (!loadedConfig.has("id")) return false;
        return loadedConfig.has("url");
    }

	public static Optional<JSONObject> loadConfig() {
        File target = Paths.get("config/secrets.json").toFile();
        Optional<JSONObject> loadedFile = FileUtil.getJSON(target);

        loadedFile.ifPresent(jsonObject -> {
            if (verifyConfig(jsonObject))
                configJSON = jsonObject;
            else
                CanvaCord.explode("secrets.json contains bad data or is missing data;\ntry deleting it and running again.");
        });
        return loadedFile;
    }

    private static JSONObject getConfig() {
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
        return getConfig().getString("canvas_token");
    }

    public static String getDiscordToken() {
        return getConfig().getString("discord_token");
    }

    public static String getUserID() {
        return getConfig().getString("id");
    }

    public static String getCanvasURL() {
        return getConfig().getString("url");
    }
}
