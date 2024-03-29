package org.canvacord;

import org.canvacord.discord.DiscordBot;
import org.canvacord.instance.InstanceManager;


public class PrefillGuiTest {
    public static void main(String[] args){

        // Create and run a wizard to get the user to set up the instance
        /*
        InstanceCreateWizard wizard = //new InstanceCreateWizard();
                new InstanceCreateWizard(InstanceLoader.loadInstance("32202-1016848330992656415").get());
        wizard.runWizard();

        InstanceManager.generateNewInstance(); */
        InstanceManager.loadInstances();
        InstanceManager.editNewInstance(InstanceManager.getInstanceByCourseID("32150").get()).ifPresentOrElse(
                instance -> System.out.println("Successfully generated instance " + instance.getName()),
                () -> System.out.println("Failed instance creation, for one reason or another")
        );

        DiscordBot.getBotInstance().disconnect();

    }
}
