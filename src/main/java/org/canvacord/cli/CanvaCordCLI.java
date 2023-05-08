package org.canvacord.cli;

import org.canvacord.discord.DiscordBot;
import org.canvacord.instance.InstanceManager;
import org.canvacord.main.CanvaCord;
import org.canvacord.scheduler.CanvaCordScheduler;
import org.canvacord.util.input.UserInput;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CanvaCordCLI implements Runnable {

	private static final Map<String, CLICommandHandler> commandMap = new HashMap<>();

	static {
		commandMap.put("quit", args -> true);
		commandMap.put("exit", args -> true);
		commandMap.put("list", CLICommands.listCommand);
		commandMap.put("start", CLICommands.startCommand);
	}

	private Scanner in;

	private CanvaCordCLI() {
		in = new Scanner(System.in);
	}

	public void run() {

		printStartup();

		// Loop until closed
		while (true) {
			String input = await();
			boolean close = processCommand(input);
			if (close) break;
		}

		System.out.println("\nExiting.");

		// Close the input
		in.close();

		// Shut down CanvaCord
		try {
			InstanceManager.stopAllInstances();
			CanvaCordScheduler.shutDown();
			DiscordBot.getBotInstance().disconnect();
		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void printStartup() {
		System.out.println();
		System.out.println("======== CANVACORD " + CanvaCord.VERSION_ID + " ========");
		System.out.println();
	}

	private String await() {
		System.out.print("> ");
		return in.nextLine();
	}

	private boolean processCommand(String command) {

		// catch empty strings
		if (command.isBlank()) return false;

		// parse the command arguments
		String[] args = command.split("\\s+");

		// follow the command map and execute whatever it points to
		return commandMap.getOrDefault(args[0], arg -> {
			System.out.println("Unknown command. Type help to list commands.");
			return false;
		}).execute(args);
	}

	public static void runCLI() {

		// Run the CLI thread
		new Thread(new CanvaCordCLI()).start();

	}

}
