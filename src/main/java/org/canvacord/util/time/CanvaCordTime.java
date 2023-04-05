package org.canvacord.util.time;

import org.canvacord.util.string.StringUtils;
import org.json.JSONArray;
import org.quartz.DateBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
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

	public static String get24Hour(int hour, int minute) {
		String ampm = hour < 12 ? "am" : "pm";
		if (hour > 12) hour -= 12;
		else if (hour == 0) hour = 12;
		return hour + ":" + String.format("%02d", minute) + ampm;

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

	public static DateTimeFormatter getFriendlyDateFormat() {
		return DateTimeFormatter.ofPattern("MM-dd hh:mm a");
	}

	public static String getFriendlyDateString(LocalDateTime localDateTime) {
		String month = StringUtils.uppercaseWords(localDateTime.getMonth().name());
		int day = localDateTime.getDayOfMonth();
		int hour = localDateTime.getHour();
		int minute = localDateTime.getMinute();

		// check if years are important
		if (localDateTime.getYear() != LocalDateTime.now().getYear()) {
			return month + " " + day + getNumberSuffix(day) + ", " + localDateTime.getYear() + " at " + get24Hour(hour, minute);
		}
		else {
			return month + " " + day + getNumberSuffix(day) + " at " + get24Hour(hour, minute);
		}
	}

	public static String getNumberSuffix(int number) {
		int onesPlace = number % 10;
		return switch (onesPlace) {
			case 1 -> "st";
			case 2 -> "nd";
			case 3 -> "rd";
			default -> "th";
		};
	}

}
