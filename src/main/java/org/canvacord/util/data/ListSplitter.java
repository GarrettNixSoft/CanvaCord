package org.canvacord.util.data;

import java.util.ArrayList;
import java.util.List;

public class ListSplitter<E> {

	public List<E>[] splitListIntoSublists(List<E> list, int numSublists) {

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
