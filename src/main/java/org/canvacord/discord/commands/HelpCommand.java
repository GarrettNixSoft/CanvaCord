package org.canvacord.discord.commands;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.component.*;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.interaction.ButtonClickEvent;
import org.javacord.api.event.interaction.SelectMenuChooseEvent;
import org.javacord.api.interaction.*;
import org.javacord.api.interaction.callback.ComponentInteractionOriginalMessageUpdater;
import org.javacord.api.interaction.callback.InteractionImmediateResponseBuilder;
import org.javacord.api.listener.interaction.ButtonClickListener;
import org.javacord.api.listener.interaction.SelectMenuChooseListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;

public class HelpCommand extends Command implements ButtonClickListener, SelectMenuChooseListener{
	private final List<SelectMenuOption> tutorialSelectMenuOptions = new ArrayList<>();
	private final List <SelectMenuOption> commandSelectMenuOptions = new ArrayList<>();
	private final HashMap<String,EmbedBuilder> tutorialDescriptions = new HashMap<>();
	private final HashMap<Long,Command> commandList = new HashMap<>(); //TODO: get this from Instance

	@Override
	public String getName(){
		return "help";
	}
	@Override
	public String getDescription(){
		return """
                This is the default help command! It is used to give a general overview of CanvaCord and how to use it. There are buttons below to select between different types of documentation.
                Call me using: */help* !
                
                **Optional Parameters:** ***/help commands <command option>***
                To skip straight to viewing a specific command's description, use its slash command name as a parameter, after */help commands*
                Discord will attempt to autocomplete, or show you a list of available options as you type. How nice!
                """;
	}
	@Override
	public String getShortDescription(){
		return "Send an overview for CanvaCord commands.";
	}
	@Override
	public void execute(SlashCommandInteraction interaction) {
		//IMPORTANT: when making slash command options in the helper slash command: make their VALUE their COMMAND ID

		String commandIdFromInteraction = interaction
				.getOptionByName("commands")
				.flatMap(SlashCommandInteractionOption::getStringValue)//GETS THE COMMANDS PARAMETER ID
				.orElse(String.valueOf(interaction.getCommandId())); //GETS THE HELP COMMAND ID

		long commandOptionId = 0;
		try {
			commandOptionId = Long.parseLong(commandIdFromInteraction);
		}
		catch (NumberFormatException e){
			System.out.println("Error registering command!");
		}

		if (commandList.isEmpty()) commandList.put(interaction.getCommandId(),this); //FIXME: JUST SO IT WORKS WITHOUT THE REAL HASHMAP

		DiscordApi api = interaction.getApi(); //need the API to attach listeners

		try{
			api.getGlobalSlashCommandById(commandOptionId).join();
		} catch (CompletionException e) { // keep until command building is complete (checks that the Command ID exists)
			interaction.createImmediateResponder().setContent("An error fetching that command has occurred. ("+e.getCause().getMessage()+")")
					.setFlags(MessageFlag.EPHEMERAL).respond();
			return;
		}

		InteractionImmediateResponseBuilder response = interaction.createImmediateResponder()
				.addEmbed(new EmbedBuilder() //body of message
						.setTitle("/"+commandList.get(commandOptionId).getName())
						.addField("Description",commandList.get(commandOptionId).getDescription())
						.setFooter("See the GitHub for complete documentation: https://github.com/GarrettNixSoft/CanvaCord"))
				.setFlags(MessageFlag.EPHEMERAL); //makes it so only the user who sent the slash command sees it

		if (commandOptionId == interaction.getCommandId()){  //IF IS DEFAULT HELP COMMAND
			response.addComponents(getButtonRow()).respond()
					.thenAccept(originalResponse -> { //attach the listeners for component interactions
						api.addButtonClickListener(this).removeAfter(10, TimeUnit.MINUTES);
						api.addSelectMenuChooseListener(this).removeAfter(10, TimeUnit.MINUTES);
					}); // see if there are better ways to attach listeners to interactionimmediateresponders
		}
		else { //regular command responses do not need the buttons or listeners
			response.respond();
		}
	}

	@Override
	public SlashCommandBuilder getBuilder() {
		return SlashCommand.with(getName(),getShortDescription());
	}

	//option: make a new class for just the help command listeners
	@Override
	public void onButtonClick(ButtonClickEvent event){
		ButtonInteraction interaction = event.getButtonInteraction();
		String buttonID = interaction.getCustomId();
		ComponentInteractionOriginalMessageUpdater response = interaction.createOriginalMessageUpdater()
				.removeAllEmbeds()
				.addComponents(getButtonRow());
		EmbedBuilder messageBody = new EmbedBuilder().setTitle(buttonID + " overview").setDescription("Use the select menu below to choose a specific option");

		if (buttonID.equals("Commands")){
			messageBody.addField("More about commands", """
                    To use any Discord slash command type: */<command_name>*.
                    The Discord slash command prompt will show what commands are available to use, you can view a list of CanvaCord commands from the select menu below, or from the CanvaCord GitHub.

                    You reached here by *using* a slash command. Great job!""");
		}
		else if (buttonID.equals("Tutorial")){
			messageBody.addField("Welcome to CanvaCord!", """
                    For the specifics on each command,click the button below and select from there. Slash commands will be what you (the User) will primarily use to access CanvaCord's functionality.

                     For more details on what CanvaCord can do *other than* commands, use the select menu below.""");
		}
		response.addComponents(getMenuRow(buttonID)).addEmbed(messageBody).update();
		interaction.acknowledge();
	}
	@Override
	public void onSelectMenuChoose(SelectMenuChooseEvent event){
		SelectMenuInteraction interaction = event.getSelectMenuInteraction();
		String selectMenuChoiceString = interaction.getChosenOptions().get(0).getValue();
		ComponentInteractionOriginalMessageUpdater response = interaction.createOriginalMessageUpdater()
				.removeAllEmbeds().addComponents(getButtonRow());
		EmbedBuilder newMessage = new EmbedBuilder();

		try { // Attempt to parse the choice string as a long (if it is: then it's the command ID)
			Command selectedMenuCommand = commandList.get(Long.parseLong(selectMenuChoiceString));
			newMessage = newMessage.setTitle("/"+selectedMenuCommand.getName()).setDescription(selectedMenuCommand.getDescription());
			response.addComponents(getMenuRow("Commands"));
		}
		catch(NumberFormatException e) { // if the choice string is NOT a long, it's a tutorial
			newMessage = getTutorials().get(selectMenuChoiceString);
			response.addComponents(getMenuRow("Tutorial"));
		}
		if (newMessage.equals(new EmbedBuilder())) //SOMETHING messed up
			newMessage.setDescription("No commands exist! Please try CanvaCord set-up again, or contact us through the GitHub with an error report.");

		response.addEmbed(newMessage.setFooter("For more complete documentation, see the GitHub!: https://github.com/GarrettNixSoft/CanvaCord")).update();
		interaction.acknowledge();
	}
	private List<SelectMenuOption> getCommandSelectMenuOptions(){
		if (commandSelectMenuOptions.equals(new ArrayList<>())){
			commandList.forEach((commandID, command)
					-> commandSelectMenuOptions.add(SelectMenuOption.create(command.getName(),String.valueOf(commandID),"See details about: "+command.getName())));
		}
		return commandSelectMenuOptions;
	}
	private List<SelectMenuOption> getTutorialSelectMenuOptions(){
		if (tutorialSelectMenuOptions.equals(new ArrayList<>())){
			for (String name:getTutorials().keySet()) //give each tutorial an option from key set of names
				tutorialSelectMenuOptions.add(SelectMenuOption.create(name,name,"See details about: "+name));
		}
		return tutorialSelectMenuOptions;
	}
	private ActionRow getMenuRow(String buttonID){
		ActionRowBuilder builder = new ActionRowBuilder();
		// Check to create the correct dropdown menu
		List<SelectMenuOption> MenuOptions = (buttonID.equals("Commands")) ? getCommandSelectMenuOptions() : getTutorialSelectMenuOptions();
		builder.addComponents(SelectMenu.createStringMenu("Browse "+buttonID,"See "+buttonID+" Options",
				1,1,MenuOptions));
		return builder.build();
	}
	private ActionRow getButtonRow(){
		return ActionRow.of(Button.secondary("Tutorial", "Tutorials"),
				Button.secondary("Commands", "Commands"));
	}
	// If there's time: tutorial class, interacts with registered commands
	// checks which commands are in the server and rearranges tutorials accordingly
	private HashMap<String,EmbedBuilder> getTutorials(){
		if (tutorialDescriptions.isEmpty()) {
			//todo: as we finish GUI/near the end, add image (maybe gif?) examples for tutorials
			tutorialDescriptions.put("Editing an Instance", new EmbedBuilder().addField("Using the Application Control Panel", """
                    **(This applies to the CanvaCord bot instance owner only)**
                    *Preconditions: At least one initialized instance already exists.*
                                    
                    The owner of a bot instance has the option to change the CanvaCord bot's settings after it has been initialized using the Application Control Panel. Click on the settings button to access the properties of that specific CanvaCord instance.
                    There, you may access your stored tokens. It is recommended to change them immediately if it is suspected they have been accessed by a third party.
                    """)); //todo: update as we (read: me, ian) figure out what else you can do in acp settings
			tutorialDescriptions.put("Fetching Canvas data", new EmbedBuilder().addField("Fetching Canvas data", """
                            The CanvaCord bot fetches Canvas data, based on the course token, regularly. This is a background behaviour that allows the internal data cache to say up to date and permits the bot to perform all other Canvas course related functionality.
                                            
                            If you feel it's not frequent enough or there were updates you would like to fetch, there are two options:
                            """)
					.addInlineField("1: Server Owners", "Through the Application Control Panel (ACP) there is a button for each course/bot instance labeled *\"Update Now\"*. " +
							"Click it to fetch Canvas data.")
					.addInlineField("2: Non Owner Users", "Politely ask your server owner."));
			tutorialDescriptions.put("Getting notified", new EmbedBuilder().addField("Notifications", """
                    As Canvas data is updated (new assignments, assignment date changes, course announcements) the CanvaCord bot will mention Users by self-selected Discord roles. The bot will also ping users after being given a timestamp for a course's meeting times.
                                    
                    To enter or exit out of being notified, react to the CanvaCord message with the appropriate emote, or request the Discord server owner to access the server settings, and manually enter you into/remove you from the specified role.
                    """));
			tutorialDescriptions.put("Finding assignments", new EmbedBuilder().addField("Finding Assignments", "The CanvaCord bot has many ways to access your Canvas course's assignments:")
					.addField("Active Assignments", "While an assignment is within its window of being assigned, but not yet past due, it can be viewed using */assignment list* alongside other active assignments.\n The command */assignment <search parameters>* can be used to see specifics.")
					.addField("Old Assignments", "After an assignment's due date is passed, it and its details will be added to a dedicated text channel as a log for previous assignments.\nAs always, assignments can be accessed through the use of */assignment <search parameters>*.\n\nMore details on the */assignment* command are available using the */help commands assignment* command."));
			tutorialDescriptions.put("Seeing course information", new EmbedBuilder().addField("Seeing Course Information", "*Prerequisites: A CanvaCord instance is set up for the current server and Canvas data has been fetched at least once.*")
					.addField("Modules", "All users can request a list of Canvas course modules through the slash command */modules*")
					.addField("Textbook", """
                            *The following is for CanvaCord Owners only*
                            **Adding a textbook** is done through the Application Control Panel (ACP). For a specific course, there is a button labeled *"Add Textbook"*
                            Textbooks are either automatically fetched, given a syllabus to get information from, or links to the files can be provided to the bot.
                            If there is no syllabus, the bot may also be given a title and author to attempt a search.
                                                    
                            *The following is for all Users*
                            **Fetching a textbook** can be done using the */textbook* slash command. For more details, use the */help commands textbook* command.
                                                    
                            *CanvaCord claims no responsibility for found resources based on the automatic search results, as it pulls only from what is available online.
                            CanvaCord does not host any of this material itself.
                            Check the legality in your area for what is or isn't allowed, and the responsibility of the User's safety from downloading fetched textbooks is exclusively the User's.*.
                            """) //todo: is the title and author thing true
					.addField("Syllabus", """
                            *The following is for CanvaCord Owners only*
                            **Adding a Syllabus** is done through the Application Control Panel (ACP). For a specific course, there is a button labeled *"Add Syllabus"*
                            Syllabi are either automatically fetched by the CanvaCord bot, or manually added by the Owner.
                                                    
                            *The following is for all Users*
                            **Fetching a Syllabus** can be done using the */syllabus* slash command. More details are available using the */help commands syllabus* command.""")
					.addInlineField("Assignments", "Course assignments can be accessed using */assignment*. For more details: see the tutorial *Find Assignments* or use the */help commands assignment* command"));
			tutorialDescriptions.put("Custom reminders", new EmbedBuilder().addField("Custom Reminders", """
                    A custom reminder to ping the User can be created using the slash command: */remindme <parameters>*. This will be ephemeral, so no other server users will be able to see. Still, do not put any personal information in Discord, as a general rule.
                    
                    More details are available using the */help commands remindme* command.
                    """));
			tutorialDescriptions.put("Cleaning up", new EmbedBuilder().addField("Deleting Your Data", """
                    The CanvaCord bot can be scheduled to delete or archive all stored Discord data and Canvas cache data after a certain due date, automatically set to the Canvas course's scheduled end date.
                    
                    This can be done prematurely, or circumvented entirely, by the CanvaCord Owner. *Please* use it responsibly, as deletion is **permanent.**
                    """));
		}
		return tutorialDescriptions;
	}
}
