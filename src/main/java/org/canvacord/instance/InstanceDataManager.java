package org.canvacord.instance;

import org.canvacord.exception.CanvaCordException;
import org.canvacord.util.file.FileUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.nio.file.Paths;

public class InstanceDataManager {

    public static void createInstanceData(String instanceID) throws CanvaCordException {

        File targetFile = Paths.get("instances/" + instanceID + "/data.json").toFile();

        if (targetFile.exists())
            throw new CanvaCordException("Data for instance " + instanceID + " already exists!");

        else {

            JSONObject emptyInstanceData = new JSONObject();
            emptyInstanceData.put("assignments", new JSONArray());
            emptyInstanceData.put("announcements", new JSONArray());

            if (!FileUtil.writeJSON(emptyInstanceData, targetFile))
                throw new CanvaCordException("Failed to create data file for instance " + instanceID);

        }

    }

}
