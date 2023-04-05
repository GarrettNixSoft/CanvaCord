package org.canvacord.util.time;

public class Profiler {

	public interface Task {
		void execute();
	}

	public static long executeProfiled(Task task) {
		long start = System.nanoTime();
		task.execute();
		return (System.nanoTime() - start) / 1_000_000;
	}

}
