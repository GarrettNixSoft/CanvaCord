package org.canvacord.reminder;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Random;

/**
 * ReminderEncryption facilitates cryptographic obfuscation (NOT secure)
 * of custom reminder messages.
 */
public class ReminderEncryption {

	// The cipher object to use for actually performing cryptographic functions
	private static final Cipher cipher;

	static {
		try {
			cipher = Cipher.getInstance("AES");
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

	/**
	 * Encrypt the message stored in a Reminder.
	 * @param reminder the reminder to encrypt
	 * @return a copy of the reminder in which the message is replaced with an encrypted version
	 */
	public static Reminder encryptReminder(Reminder reminder) {
		SecretKey secretKey = getKey(reminder.userID());
		String encryptedMessage = encryptMessage(reminder.message(), secretKey);
		return new Reminder(reminder.reminderID(), reminder.userID(), reminder.channelID(),
							reminder.createdAt(), reminder.triggerDate(), true, encryptedMessage);
	}

	/**
	 * Decrypt an encrypted message stored in a reminder.
	 * @param reminder the reminder to decrypt
	 * @return a copy of the reminder in which the message is replaced with the decrypted version
	 */
	public static Reminder decryptReminder(Reminder reminder) {
		SecretKey secretKey = getKey(reminder.userID());
		String decryptedMessage = decryptMessage(reminder.message(), secretKey);
		return new Reminder(reminder.reminderID(), reminder.userID(), reminder.channelID(),
							reminder.createdAt(), reminder.triggerDate(), true, decryptedMessage);
	}

	/**
	 * Generate a {@code SecretKey} for encrypting/decrypting a message.
	 * This key is generated deterministically using the user's
	 * Discord ID, and is thus not truly secure -- a malicious
	 * Owner running CanvaCord can still read the messages if they
	 * try.
	 * @param userID the ID of the user who created the reminder
	 * @return a {@code SecretKey} generated using the user's ID as a seed
	 */
	private static SecretKey getKey(long userID) {
		Random random = new Random(userID);
		byte[] bytes = new byte[32];
		random.nextBytes(bytes);
		return new SecretKeySpec(bytes, "AES");
	}

	/**
	 * Encrypt a String using a {@code SecretKey}.
	 * @param message the message String to encrypt
	 * @param secretKey the key to use in the encryption process
	 * @return the encrypted String
	 */
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

	/**
	 * Decript a String using a {@code SecretKey}.
	 * @param message the encrypted String to decrypt
	 * @param secretKey the key to use in the decryption process
	 * @return the decrypted String
	 */
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
