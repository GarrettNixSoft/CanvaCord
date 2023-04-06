package org.canvacord.reminder;

import org.canvacord.main.CanvaCord;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;

public class ReminderEncryption {

	private static KeyGenerator keyGenerator;
	private static final Cipher cipher;

	static {
		try {
			keyGenerator = KeyGenerator.getInstance("AES");
			cipher = Cipher.getInstance("AES");
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

	public static Reminder encryptReminder(Reminder reminder) {
		SecretKey secretKey = getKey(reminder.userID());
		System.out.println("encryption key: " + secretKey.toString());
		String encryptedMessage = encryptMessage(reminder.message(), secretKey);
		return new Reminder(reminder.reminderID(), reminder.userID(), reminder.channelID(),
							reminder.createdAt(), reminder.triggerDate(), true, encryptedMessage);
	}

	public static Reminder decryptReminder(Reminder reminder) {
		SecretKey secretKey = getKey(reminder.userID());
		System.out.println("decryption key: " + secretKey.toString());
		String decryptedMessage = decryptMessage(reminder.message(), secretKey);
		return new Reminder(reminder.reminderID(), reminder.userID(), reminder.channelID(),
							reminder.createdAt(), reminder.triggerDate(), true, decryptedMessage);
	}

	private static SecretKey getKey(long userID) {
		try {
			keyGenerator = KeyGenerator.getInstance("AES");
		}
		catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			CanvaCord.explode();
		}
		Random random = new Random(userID);
		byte[] bytes = new byte[32];
		random.nextBytes(bytes);
		return new SecretKeySpec(bytes, "AES");
	}

	private static String encryptMessage(String message, SecretKey secretKey) {
		byte[] plainBytes = message.getBytes();
		try {
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			byte[] cipherBytes = cipher.doFinal(plainBytes);
			Base64.Encoder encoder = Base64.getEncoder();
			return encoder.encodeToString(cipherBytes);
		}
		catch (Exception e) {
			e.printStackTrace();
			return "<Encryption Failed>";
		}
	}

	private static String decryptMessage(String message, SecretKey secretKey) {
		Base64.Decoder decoder = Base64.getDecoder();
		byte[] cipherBytes = decoder.decode(message.getBytes());
		try {
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			byte[] plainBytes = cipher.doFinal(cipherBytes);
			return new String(plainBytes);
		}
		catch (Exception e) {
			e.printStackTrace();
			return "<Decryption Failed>";
		}
	}

}
