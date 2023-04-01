package org.canvacord;

import org.canvacord.discord.DiscordBot;
import org.canvacord.instance.*;
import org.canvacord.persist.CacheManager;
import org.canvacord.setup.InstanceCreateWizard;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;


public class PrefillGuiTest {
    public static void main(String[] args){

        // Create and run a wizard to get the user to set up the instance
        /*
        InstanceCreateWizard wizard = //new InstanceCreateWizard();
                new InstanceCreateWizard(InstanceLoader.loadInstance("32150-1083546251661938829").get());
        wizard.runWizard();

        InstanceManager.generateNewInstance(); */
        InstanceManager.editNewInstance(InstanceLoader.loadInstance("32150-1083546251661938829").get()).ifPresentOrElse(
                instance -> System.out.println("Successfully generated instance " + instance.getName()),
                () -> System.out.println("Failed instance creation, for one reason or another")
        );

        DiscordBot.getBotInstance().disconnect();

    }
}
