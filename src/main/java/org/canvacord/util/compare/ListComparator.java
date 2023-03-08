package org.canvacord.util.compare;

import java.util.ArrayList;
import java.util.List;

public class ListComparator<T> {

	/**
	 * Check whether the contents of two lists are identical.
	 * Order does not matter, but every element in one list
	 * must exist somewhere in the other.
	 * @param list1 the first list
	 * @param list2 the second list
	 * @return {@code true} if the lists are the same size and every element in one list exists in the other
	 */
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

	/**
	 * Check whether two the contents of two lists are identical.
	 * Order is strictly enforced, so the lists must be the same size
	 * and each element in one list must exist at the same index in
	 * the other.
	 * @param list1 the first list
	 * @param list2 the second list
	 * @return {@code true} if the lists contain the same number of identical elements in the same order
	 */
	public boolean listsIdenticalOrdered(List<T> list1, List<T> list2) {

		// Lists of different sizes cannot be identical
		if (list1.size() != list2.size())
			return false;

		for (int i = 0; i < list1.size(); i++) {
			T elem1 = list1.get(i);
			T elem2 = list2.get(i);
			if (!elem1.equals(elem2))
				return false;
		}

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
