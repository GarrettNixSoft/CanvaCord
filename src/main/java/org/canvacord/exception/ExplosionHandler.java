package org.canvacord.exception;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ExplosionHandler {

	private static Clip boomClip;

	static {

		try {
			String pathToSound = "resources/boom.wav";
			InputStream inputStream = ExplosionHandler.class.getClassLoader().getResourceAsStream(pathToSound);
			if (inputStream == null) {
				inputStream = new BufferedInputStream(Files.newInputStream(Paths.get(pathToSound)));
			}

			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(inputStream));
			boomClip = AudioSystem.getClip();
			boomClip.open(audioInputStream);
		}
		catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
			e.printStackTrace();
			System.err.println("Failed to load boom sounds, no fun for us today.");
		}
	}

	public static void makeFunnyBoomSound() {
		boomClip.loop(0);
	}

}
