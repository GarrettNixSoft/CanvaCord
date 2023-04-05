package org.canvacord.util.time;

import org.canvacord.util.string.StringUtils;

import java.time.DayOfWeek;

public record CanvaCordWeekdayWrapper(DayOfWeek weekday) {

	@Override
	public String toString() {
		return StringUtils.uppercaseWords(weekday.name());
	}

	public static DayOfWeek getDayByName(String weekdayStr) {
		return switch (weekdayStr) {
			case "monday" -> DayOfWeek.MONDAY;
			case "tuesday" -> DayOfWeek.TUESDAY;
			case "wednesday" -> DayOfWeek.WEDNESDAY;
			case "thursday" -> DayOfWeek.THURSDAY;
			case "friday" -> DayOfWeek.FRIDAY;
			case "saturday" -> DayOfWeek.SATURDAY;
			case "sunday" -> DayOfWeek.SUNDAY;
			default -> null;
		};
	}

}
