package org.canvacord.util.string;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class StringUtils {

	public static boolean containsAny(String str, String... targets) {
		for (String target : targets) {
			if (str.contains(target)) {
				return true;
			}
		}
		return false;
	}

	public static String uppercaseWords(String str) {
		// prepare a StringBuilder to construct the result
		StringBuilder result = new StringBuilder();
		// split the string into words
		String[] words = str.split(" ");
		// iterate over each word
		for (int i = 0; i < words.length; i++) {
			String word = words[i];
			// if this word starts with a letter,
			if (Character.isAlphabetic(word.charAt(0))) {
				// capitalize it
				word = word.substring(0, 1).toUpperCase() + word.substring(1);
			}
			// append this word and a space to the result
			result.append(word).append(' ');
		}
		// return the result string
		return result.toString().trim();
	}

	public static String[] getStringNumberArray(int start, int end) {

		List<String> list = new ArrayList<>();

		for (int i = start; i <= end; i++) {
			list.add("" + i);
		}

		return list.toArray(new String[0]);

	}

	public static boolean isURL(String str) {
		return Pattern.compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]").matcher(str).find();
	}

}
