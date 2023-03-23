package org.canvacord.util.string;

import java.util.List;

public class StringConverter {
	/*
	 * Given a list of strings, return them all combined
	 * into a single String object.
	 */
	public static String combineAll(List<String> strings) {
		StringBuilder result = new StringBuilder();
		for (String str : strings) {
			result.append(str).append("\n");
		}
		return result.toString();
	}

	public static String listToString(List<?> list) {
		StringBuilder result = new StringBuilder();
		result.append("List[");
		for (Object o : list) {
			result.append(o);
			result.append(" ");
		}
		// remove last space
		result.deleteCharAt(result.length() - 1);
		result.append("]");
		return result.toString();
	}

	public static String tabbedListToString(List<?> list) {
		StringBuilder result = new StringBuilder();
		result.append("List[\n");
		for (Object o : list) {
			result.append('\t');
			result.append(o);
			result.append(",\n");
		}
		// remove last space
		result.deleteCharAt(result.length() - 1);
		result.append("\n]");
		return result.toString();
	}

	public static String byteArrayToString(byte[] arr) {
		if (arr.length == 0) return "{}";
		StringBuilder result = new StringBuilder();
		result.append('{');
		for (byte b : arr) {
			result.append(b);
			result.append(", ");
		}
		result.setLength(result.length() - 2);
		result.append('}');
		return result.toString();
	}

	public static String shortArrayToString(short[] arr) {
		if (arr.length == 0) return "{}";
		StringBuilder result = new StringBuilder();
		result.append('{');
		for (short s : arr) {
			result.append(s);
			result.append(", ");
		}
		result.setLength(result.length() - 2);
		result.append('}');
		return result.toString();
	}

	public static String intArrayToString(int[] arr) {
		if (arr.length == 0) return "{}";
		StringBuilder result = new StringBuilder();
		result.append('{');
		for (int i : arr) {
			result.append(i);
			result.append(", ");
		}
		result.setLength(result.length() - 2);
		result.append('}');
		return result.toString();
	}

	public static String longArrayToString(long[] arr) {
		if (arr.length == 0) return "{}";
		StringBuilder result = new StringBuilder();
		result.append('{');
		for (long l : arr) {
			result.append(l);
			result.append(", ");
		}
		result.setLength(result.length() - 2);
		result.append('}');
		return result.toString();
	}

	public static String floatArrayToString(float[] arr) {
		if (arr.length == 0) return "{}";
		StringBuilder result = new StringBuilder();
		result.append('{');
		for (float f : arr) {
			result.append(f);
			result.append(", ");
		}
		result.setLength(result.length() - 2);
		result.append('}');
		return result.toString();
	}

	public static String doubleArrayToString(double[] arr) {
		if (arr.length == 0) return "{}";
		StringBuilder result = new StringBuilder();
		result.append('{');
		for (double d : arr) {
			result.append(d);
			result.append(", ");
		}
		result.setLength(result.length() - 2);
		result.append('}');
		return result.toString();
	}

	/**
	 * Get the value from a string formatted as follows:
	 * {@code *=*}, where {@code *} is any sequence of characters.
	 * Returns the right value.
	 * @param valueStr The string to extract a value from.
	 * @return The value represented on the right side of the equals character.
	 */
	public static String getValue(String valueStr) {
		return valueStr.substring(valueStr.indexOf("=") + 1);
	}

	public static String enumToString(String enumName) {
		return StringUtils.uppercaseWords(enumName.replaceAll("_", " "));
	}
	
}
