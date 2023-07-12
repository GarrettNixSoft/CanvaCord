package org.canvacord.persist;

import edu.ksu.canvas.model.assignment.Assignment;
import org.canvacord.instance.Instance;
import org.canvacord.util.data.Pair;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class AssignmentFilter {

	public static List<Pair<Assignment, Pair<Date, Date>>> getAssignmentsWithChangedDueDates(Instance instance) {

		List<Pair<Assignment, Pair<Date, Date>>> result = new ArrayList<>();

		Map<Long, Assignment> cachedAssignments = CacheManager.getCachedAssignments(instance.getInstanceID());
		Map<Long, Pair<Date, Date>> changedDueDates = CacheManager.getCachedChangedDueDates(instance.getInstanceID());

		for (long id : cachedAssignments.keySet()) {
			if (changedDueDates.containsKey(id)) {
				Pair<Date, Date> datePair = changedDueDates.get(id);
				Assignment assignment = cachedAssignments.get(id);
				result.add(new Pair<>(assignment, datePair));
			}
		}

		return result;

	}

}
