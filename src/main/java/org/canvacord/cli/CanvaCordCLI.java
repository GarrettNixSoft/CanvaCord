package org.canvacord.cli;

import org.apache.logging.log4j.LogManager;
import org.canvacord.cli.commands.*;
import org.canvacord.cli.commands.instance.InstanceCommand;
import org.canvacord.cli.commands.start.StartCommand;
import org.canvacord.cli.commands.stop.StopCommand;
import org.canvacord.discord.DiscordBot;
import org.canvacord.instance.InstanceManager;
import org.canvacord.main.CanvaCord;
import org.canvacord.scheduler.CanvaCordScheduler;
import org.canvacord.util.data.Stack;

import java.util.*;

public class CanvaCordCLI implements Runnable {

	public static final Map<String, Class<? extends CLICommand>> commandMap = new HashMap<>();
	public static final List<String> COMMANDS = new ArrayList<>();

	static {
		// HELP AND ITS ALIASES
		commandMap.put("help", HelpCommand.class);
		commandMap.put("h", HelpCommand.class);
		// EVERYTHING ELSE
		commandMap.put("quit", QuitCommand.class);
		commandMap.put("exit", QuitCommand.class);
		commandMap.put("list", ListCommand.class);
		commandMap.put("start", StartCommand.class);
		commandMap.put("stop", StopCommand.class);
		commandMap.put("instance", InstanceCommand.class);
		// LIST OF COMMANDS FOR HELP
		COMMANDS.add("help");
		COMMANDS.add("quit");
		COMMANDS.add("list");
		COMMANDS.add("start");
		COMMANDS.add("stop");
		COMMANDS.add("instance");
	}

	// Singleton
	private static CanvaCordCLI instance;

	// Use a scanner for input
	private final Scanner in;

	// Constructor initializes input
	private CanvaCordCLI() {
		in = new Scanner(System.in);
	}

	/**
	 * Run CanvaCord's CLI mode.
	 */
	public void run() {

		printStartup();

		// Loop until closed
		while (true) {
			String input = await();
			boolean close = processCommand(input);
			if (close) break;
		}

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

		System.out.println("\n");

		// Close the logger properly
		LogManager.shutdown();

	}

	/**
	 * Print the startup message to stdout
	 */
	private void printStartup() {
		System.out.println();
		System.out.println("======== CANVACORD " + CanvaCord.VERSION_ID + " ========");
		System.out.println();
	}

	/**
	 * Wait for the user to enter some input
	 * @return the user's input trimmed
	 */
	private String await() {
		System.out.print("> ");
		return in.nextLine().trim();
	}

	/**
	 * Process a user's command
	 * @param command the command string
	 * @return whether this command should terminate CanvaCord
	 */
	private boolean processCommand(String command) {

		// catch empty strings
		if (command.isBlank()) return false;

		// parse the command arguments
		String[] argTokens = command.split("\\s+");

		// Push arguments onto the stack in reverse order
		Stack<String> args = new Stack<>();
		for (int i = argTokens.length - 1; i >= 0; i--) {
			args.push(argTokens[i]);
		}

		// follow the command map and execute whatever it points to
		try {
			return commandMap.getOrDefault(args.poll(), UnknownCommand.class).getConstructor().newInstance().execute(args);
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private static CanvaCordCLI getInstance() {
		if (instance == null)
			instance = new CanvaCordCLI();
		return instance;
	}

	public static void runCLI() {

		// Run the CLI thread
		new Thread(getInstance()).start();

	}

}
