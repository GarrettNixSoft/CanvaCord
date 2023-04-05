package org.canvacord.util.time;

import org.json.JSONArray;
import org.quartz.DateBuilder;

import java.util.Date;

public class CanvaCordTime {

	public static Date getDateOf(int hour, int minute, String ampm) {

		switch (ampm) {
			case "am" -> {
				if (hour == 12) hour = 0;
			}
			case "pm" -> {
				if (hour != 12) hour += 12;
			}
		}

		return DateBuilder.dateOf(hour, minute, 0);

	}

	public static int get24Hour(int hour, String ampm) {
		switch (ampm) {
			case "am" -> {
				if (hour == 12) hour = 0;
			}
			case "pm" -> {
				if (hour != 12) hour += 12;
			}
		}

		return hour;
	}

	public static int stringToDayConstant(String dayString) {
		return switch (dayString.toLowerCase()) {
			case "monday", "mon", "mo", "m" -> DateBuilder.MONDAY;
			case "tuesday", "tue", "tu" -> DateBuilder.TUESDAY;
			case "wednesday", "wed", "we" -> DateBuilder.WEDNESDAY;
			case "thursday", "thu", "th" -> DateBuilder.THURSDAY;
			case "friday", "fri", "fr" -> DateBuilder.FRIDAY;
			case "saturday", "sat", "sa" -> DateBuilder.SATURDAY;
			case "sunday", "sun", "su" -> DateBuilder.SUNDAY;
			default -> -1;
		};
	}

	public static Integer[] stringsToDayConstants(JSONArray dayStrings) {
		Integer[] daysOfWeek = new Integer[dayStrings.length()];
		for (int i = 0; i < daysOfWeek.length; i++) {
			daysOfWeek[i] = stringToDayConstant(dayStrings.getString(i));
		}
		return daysOfWeek;
	}

}
