package org.canvacord.util.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class DataStructureConversion<K, V> {

	public Map<K, V> listToMap(List<V> values, Function<V, K> mapper) {
		Map<K, V> result = new HashMap<>();
		for (V value : values) {
			K key = mapper.apply(value);
			result.put(key, value);
		}
		return result;
	}

}
