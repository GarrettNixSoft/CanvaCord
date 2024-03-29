package org.canvacord.entity;

import org.canvacord.util.string.StringUtils;
import org.canvacord.util.time.CanvaCordWeekdayWrapper;
import org.json.JSONObject;

import java.time.DayOfWeek;

public class ClassMeeting {

	private DayOfWeek weekday;
	private JSONObject startTime;
	private JSONObject endTime;

	public ClassMeeting(DayOfWeek weekday, JSONObject startTime, JSONObject endTime) {
		this.weekday = weekday;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public ClassMeeting(JSONObject meetingData) {
		weekday = CanvaCordWeekdayWrapper.getDayByName(meetingData.getString("day"));
		startTime = meetingData.getJSONObject("start");
		endTime = meetingData.getJSONObject("end");
	}

	public DayOfWeek getWeekday() {
		return weekday;
	}

	public JSONObject getStartTime() {
		return startTime;
	}

	public JSONObject getEndTime() {
		return endTime;
	}

	public void setWeekday(DayOfWeek weekday) {
		this.weekday = weekday;
	}

	public void setStartTime(JSONObject startTime) {
		this.startTime = startTime;
	}

	public void setEndTime(JSONObject endTime) {
		this.endTime = endTime;
	}

	// ================ UTILITY ================
	public String getWeekdayStr() {
		return StringUtils.uppercaseWords(weekday.name());
	}

	public String getStartDescription() {
		return String.format("%d:%02d", startTime.getInt("hour"), startTime.getInt("minute"));
	}

	public String getEndDescription() {
		return String.format("%d:%02d", endTime.getInt("hour"), endTime.getInt("minute"));
	}

	public String getTimeDescription() {
		String startTimeStr = getStartDescription();
		String endTimeStr = getEndDescription();
		if (startTime.getString("ampm").equals(endTime.getString("ampm"))) {
			return startTimeStr + " ---> " + endTimeStr + " " + startTime.getString("ampm");
		}
		else {
			return startTimeStr + " " + startTime.getString("ampm") + " ---> " + endTimeStr + " " + endTime.getString("ampm");
		}
	}

	public JSONObject getJSON() {
		JSONObject result = new JSONObject();
		result.put("start", startTime);
		result.put("end", endTime);
		result.put("day", weekday.toString().toLowerCase());
		return result;
	}

}
