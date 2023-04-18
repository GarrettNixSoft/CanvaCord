package org.canvacord.util.data;

public record Pair<T1, T2>(T1 data1, T2 data2) {

	public T1 first() {
		return data1;
	}

	public T2 second() {
		return data2;
	}

}
