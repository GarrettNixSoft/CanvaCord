package org.canvacord;

import org.canvacord.setup.FirstRunSetup;

public class DiscordServerBot {


    public static void main( String[] args) {
        //DiscordApi api = new DiscordApiBuilder().setToken("MTA4MjgyMzAzMjgzMTIxMzYyOA.G817wX.7RgTC2CQZDke2-WDw3BKHuPkbjQWLkNOiiSGbs").login().join();
        //System.out.println(api.createBotInvite());
        FirstRunSetup.runFirstTimeSetup();
    }
}
