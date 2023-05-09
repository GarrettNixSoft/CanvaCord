package org.canvacord;

import org.canvacord.canvas.TextbookFetcher;
import org.canvacord.instance.Instance;
import org.canvacord.instance.InstanceManager;
import org.canvacord.persist.ConfigManager;
import org.canvacord.util.file.TextbookScraper;

import java.io.IOException;

public class fetchTextbookOnlineTest {

    public static void main(String[] args) throws IOException {
        ConfigManager.loadConfig();
        InstanceManager.loadInstances();
        Instance instance = InstanceManager.getInstances().get(0);
        System.out.println(TextbookFetcher.fetchTextbookOnline("bio", "32150"));
    }
}
