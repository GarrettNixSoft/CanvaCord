package org.canvacord.util.data;

import java.util.ArrayList;
import java.util.List;

public class ListSplitter<E> {

	/**
	 * Split the given lists into smaller lists, in order. i.e., the resulting
	 * sub-lists will be like ordered slices of the parent list
	 * @param list the list to split
	 * @param numSublists the number of smaller lists to split it into
	 * @return an array of lists
	 */
	public List<E>[] splitListIntoSublists(List<E> list, int numSublists) {

		if (numSublists == 0) throw new IllegalArgumentException("Cannot split into zero lists");

		List<E>[] result = new List[numSublists];

		// create the lists
		for (int i = 0; i < numSublists; i++) {
			result[i] = new ArrayList<>();
		}

		// put elements into the lists
		for (int i = 0; i < list.size(); i++) {
			int listIndex = i / (list.size() / numSublists);
			result[listIndex].add(list.get(i));
		}

		return result;

	}

}
