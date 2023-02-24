package org.canvacord.discord.commands;

import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.*;

import java.util.*;

import static org.canvacord.discord.commands.CommandHandler.registerCommandServer;

public class CommandBuilder {

	/**
	 * Takes the requested slash commands and registers them in the provided server
	 *
	 * @param server The server that commands are being built for.
	 * @param serverActiveCommands A set of owner chosen commands that determines what's available.
	 * */
	public static void buildCommandsForServer(Server server, Set<Command> serverActiveCommands) {
		List<SlashCommandOptionChoice> helpCommandOptionChoices = new ArrayList<>();
		HelpCommand help = new HelpCommand();
		// just guessing that the parameter for active commands would be a set
		// the help command should NOT be on, so we don't double up

		/*commands with options:
		assignment <parameters> DONE
		remindme <create: date and name parameters> <list> <delete: name parameter> DONE
		help <commands: parameter> DONE
		modules <list> <name: parameter> DONE
		*/

		for (Command command:serverActiveCommands){
			SlashCommandBuilder build = SlashCommand.with(command.getName(),command.getShortDescription());
			long commandID = registerCommandServer(build,command,server);
			helpCommandOptionChoices.add(SlashCommandOptionChoice.create(command.getName(),commandID));
		}


		// help cmd has to be LAST to get all registered COMMAND ID's
		SlashCommandBuilder helpCommand = SlashCommand.with(help.getName(),help.getShortDescription(),
				Collections.singletonList(SlashCommandOption.createWithChoices(
						SlashCommandOptionType.LONG,"commands","(optional) select a command",false,
						helpCommandOptionChoices)));
		registerCommandServer(helpCommand,help,server);

		// THE COMMAND CLASSES BELOW DO NOT EXIST YET
		// TODO
		/*
		SlashCommandBuilder assignmentCommand = build.addOption(SlashCommandOption
				.createStringOption("search","input parameters for assignment search",true));

		SlashCommandBuilder remindMeCommand = build.setOptions(Arrays.asList( //figure out format for reminder command
				SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND,"create","create a reminder from a name and date",
						Arrays.asList(SlashCommandOption.createStringOption("name","name of the reminder",true),
								SlashCommandOption.createStringOption("date","date-time for the reminder to be sent",true))),
				SlashCommandOption.create(SlashCommandOptionType.SUB_COMMAND,"list","list all reminders for this user"),
				SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND,"delete","enter the reminders name to delete it",
						Collections.singletonList(SlashCommandOption.createStringOption("name","name of the command to delete",true)))));

		SlashCommandBuilder modulesCommand = build.setOptions(Arrays.asList(
				SlashCommandOption.create(SlashCommandOptionType.SUB_COMMAND,"list","list all course modules"),
				SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND,"search","search modules",
						Collections.singletonList(SlashCommandOption.createStringOption("name","name of the module to search",true)))));
	*/
	}
}
