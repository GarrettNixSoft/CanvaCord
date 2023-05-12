package org.canvacord.setup;
import org.canvacord.gui.wizard.CanvaCordWizard;
import org.canvacord.gui.wizard.cards.TokenSetupCard;
import org.canvacord.util.input.UserInput;
import org.canvacord.util.string.StringUtils;

/**
 * The TokenSetupWizard implements the initial setup process for CanvaCord. It is the
 * first operation run when CanvaCord is installed on a system.
 */
public class TokenSetupWizard extends CanvaCordWizard {

	private TokenSetupCard tokenSetupCard;

	public TokenSetupWizard() {
		super("Configure CanvaCord");
	}

	@Override
	public void initCards() {

		// Card for adding the institution's Canvas URL and Canvas/Discord API tokens
		registerCard(tokenSetupCard = new TokenSetupCard(this, "token_setup", true));
	}

	@Override
	public boolean completedSuccessfully() {

		// Verify the URL string
		if (!StringUtils.isURL(tokenSetupCard.getUrlField().getText()))
			return false;

		// If the URL is valid, check the verification status
		return tokenSetupCard.isVerifiedCanvasToken() && tokenSetupCard.isVerifiedDiscordToken();

	}

}
