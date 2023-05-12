package org.canvacord.discord.commands;

import org.canvacord.canvas.CanvasApi;
import org.canvacord.exception.CanvaCordException;
import org.canvacord.instance.Instance;
import org.canvacord.instance.InstanceManager;
import org.canvacord.persist.CacheManager;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.LowLevelComponent;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.interaction.ButtonClickEvent;
import org.javacord.api.interaction.*;
import org.javacord.api.interaction.callback.ComponentInteractionOriginalMessageUpdater;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.javacord.api.listener.interaction.ButtonClickListener;
import org.json.JSONArray;
import org.javacord.api.entity.message.component.Button;
import org.json.JSONObject;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ModuleCommand extends Command implements ButtonClickListener {

	private int assignmentsPerPage = 3; //arbitrary number
	private int currentPage;
	private Instance instance;
	private JSONArray modulesArray = new JSONArray();


	@Override
	public String getDescription() {
		return """
				See the modules for the specific course instance using */modulelist <refresh>*!
				
				**Parameter:
				**Refresh (optional):** If you feel the modules list is out of date compared to what's listed on the Canvas course, this parameter can be used to attempt to refresh the list of modules stored in CanvaCord!
    			
				***What is a module?***
				Canvas has groups of assignments and files listed under *"modules"*. CanvaCord provides links to the Canvas page which directs you to the overview for a specific module.
				""";
	}

	@Override
	public String getShortDescription() {
		return "See the course modules list.";
	}

	@Override
	public String getName() {
		return "modulelist";
	}

	@Override
	public void execute(SlashCommandInteraction interaction) {

		// get instance, server, course id
		Server server = interaction.getServer().orElseThrow(CanvaCordException::new);
		instance = InstanceManager.getInstanceByServerID(server.getId()).orElseThrow(CanvaCordException::new);
		//CompletableFuture<InteractionOriginalResponseUpdater> response;

		// grab the optional boolean for refreshing
		boolean refresh;
		Optional<SlashCommandInteractionOption> refreshOption = interaction.getOptionByIndex(0);
		if (refreshOption.isPresent()) {
			Optional<Boolean> refreshValue = refreshOption.get().getBooleanValue();
			refresh = refreshValue.orElse(false);
		} else {
			refresh = false;
		}

		// Check the cache for modules
		modulesArray = CacheManager.getCachedModuleEntities(instance.getCourseID());

		if (refresh || modulesArray.isEmpty()) {
			interaction.respondLater(true).thenAccept(interactionOriginalResponseUpdater -> {
				modulesArray = CanvasApi.getInstance().getAllModuleFiles(instance.getCourseID());
				buildResponse(interaction, interactionOriginalResponseUpdater, modulesArray);
			});
		}
		else {
			interaction.createImmediateResponder().respond()
					.thenAccept(interactionOriginalResponseUpdater ->
							buildResponse(interaction, interactionOriginalResponseUpdater, modulesArray));
		}
	}

	private void buildResponse(SlashCommandInteraction interaction, InteractionOriginalResponseUpdater interactionOriginalResponseUpdater, JSONArray modulesArray) {

		// grab the Discord API
		DiscordApi api = interaction.getApi();

		if (modulesArray.isEmpty()) {
			interactionOriginalResponseUpdater.setFlags(MessageFlag.EPHEMERAL).setContent("An error occurred when fetching modules.").update();
			return;
		}

		// get the minimum between the default value or the num of fetched assignments
		assignmentsPerPage = Math.min(assignmentsPerPage, modulesArray.length());
		for (EmbedBuilder embed : assignmentEmbeds().subList(0, assignmentsPerPage)) {
			interactionOriginalResponseUpdater.addEmbed(embed).update();
		}

		if (modulesArray.length()>assignmentsPerPage) {
			interactionOriginalResponseUpdater
					.addComponents(ActionRow.of(Button.danger("forward", "▶")))
					.update().thenAccept(originalResponse -> {
						api.addButtonClickListener(this).removeAfter(10, TimeUnit.MINUTES);
					});
		}

	}

	private List<EmbedBuilder> assignmentEmbeds(){
		List <EmbedBuilder> moduleEmbeds = new ArrayList<>();
		for (int i=0; i < modulesArray.length(); i++) {
			System.out.println(modulesArray.getJSONObject(i).get("title").toString());
			EmbedBuilder assignmentEmbed = new EmbedBuilder()
					.setTitle(modulesArray.getJSONObject(i).get("title").toString())
					.setUrl(modulesArray.getJSONObject(i).get("url").toString())
					.setColor(Color.RED);
            /*
            if (assignment.getDescription()!= null){
                assignmentEmbed.setDescription(assignment.getDescription());
            }
            else{
                assignmentEmbed.setDescription(assignment.getName());
            }
            if (assignment.getDueAt() != null){
                assignmentEmbed.addField("Due",assignment.getDueAt().toString());
            }
            */
			moduleEmbeds.add(assignmentEmbed);
		}
		return moduleEmbeds;
	}


	@Override
	public SlashCommandBuilder getBuilder(Instance instance) {
		return SlashCommand.with(getName(),getShortDescription(),
				List.of(SlashCommandOption.create(SlashCommandOptionType.BOOLEAN, "refresh", "Force CanvaCord to refresh the modules list from Canvas.", false)));
	}

	@Override
	public void onButtonClick(ButtonClickEvent buttonClickEvent) {

		ButtonInteraction interaction = buttonClickEvent.getButtonInteraction();
		ComponentInteractionOriginalMessageUpdater response = interaction.createOriginalMessageUpdater();
		String buttonID = interaction.getCustomId();
		List<LowLevelComponent> buttons = new ArrayList<>();

		int pageStart = currentPage * assignmentsPerPage;
		int pageEnd;

		if (buttonID.equals("forward") && modulesArray.length()-(pageStart+assignmentsPerPage)>0){
			currentPage+=1;
			buttons.add(Button.danger("back","◀"));

			if(modulesArray.length()-((currentPage * assignmentsPerPage)+assignmentsPerPage)>0){ //ugly yo
				buttons.add(Button.danger("forward", "▶"));
			}
		}
		else if (buttonID.equals("back") && currentPage!=0) {
			currentPage -= 1;

			if(currentPage != 0){
				buttons.add(Button.danger("back","◀"));
			}

			// put after the if statement to preserve button order
			buttons.add(Button.danger("forward", "▶"));
		}

		pageStart = currentPage * assignmentsPerPage;
		pageEnd = Math.min(pageStart + assignmentsPerPage, modulesArray.length());


		response.removeAllEmbeds();
		for (EmbedBuilder embed : assignmentEmbeds().subList(pageStart, pageEnd)) {
			response.addEmbed(embed);
		}

		response.addComponents(ActionRow.of(buttons)).update();
	}
}
