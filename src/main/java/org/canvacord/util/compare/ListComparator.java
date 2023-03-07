package org.canvacord.util.compare;

import java.util.ArrayList;
import java.util.List;

public class ListComparator<T> {

	public boolean listsIdentical(List<T> list1, List<T> list2) {

		// Lists of different sizes cannot be identical
		if (list1.size() != list2.size())
			return false;

		// Order is irrelevant; lists must contain all equal elements
		for (T element : list1)
			if (!list2.contains(element))
				return false;

		return true;

	}

	public List<T> listDifference(List<T> expected, List<T> actual) {
		List<T> difference = new ArrayList<>();
		for (T element : expected)
			if (!actual.contains(element))
				difference.add(element);
		return difference;
	}

}
