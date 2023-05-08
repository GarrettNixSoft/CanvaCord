package org.canvacord.cli.commands;

import org.canvacord.cli.CanvaCordCLI;
import org.canvacord.discord.commands.Command;
import org.canvacord.util.data.Stack;

public class HelpCommand extends CLICommand {

	public HelpCommand() {
		super("help");
	}

	@Override
	public boolean execute(Stack<String> args) {

		if (args.isEmpty()) {
			printCommandList();
		}
		else {
			String commandName = args.poll();
			if (!CanvaCordCLI.commandMap.containsKey(commandName))
				System.out.println("Unknown command.");
			else {
				try {
					CanvaCordCLI.commandMap.get(commandName).getConstructor().newInstance().printUsage();
				}
				catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
		}

		return false;
	}

	private void printCommandList() {

		System.out.println("Available commands:");
		for (String command : CanvaCordCLI.COMMANDS) {
			System.out.println("\t" + command);
		}
		System.out.println("\nFor details about a particular command, use \"help [command]\"\n");

	}

	@Override
	public String getUsage() {
		return null;
	}
}
