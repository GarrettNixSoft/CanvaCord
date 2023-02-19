package org.canvacord.instance;

import org.canvacord.exception.CanvaCordException;
import org.canvacord.util.file.FileUtil;
import org.json.JSONObject;

import java.io.File;
import java.nio.file.Paths;

public class InstanceWriter {

    public static void writeInstance(Instance instance) throws CanvaCordException {

        JSONObject instanceJSON = instance.getConfiguration().getRawJSON();
        File targetFile = Paths.get("instances/" + instance.getInstanceID() + "/config.json").toFile();
        if (!FileUtil.writeJSON(instanceJSON, targetFile))
            throw new CanvaCordException("Failed to write instance " + instance.getInstanceID() + " to disk!");

    }

}
