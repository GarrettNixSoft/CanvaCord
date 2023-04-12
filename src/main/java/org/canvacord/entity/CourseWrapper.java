package org.canvacord.entity;

import edu.ksu.canvas.model.Course;

public record CourseWrapper(Course course) {
	@Override
	public String toString() {
		return course().getName();
	}
}
