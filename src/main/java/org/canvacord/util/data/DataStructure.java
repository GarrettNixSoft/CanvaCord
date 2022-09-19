package org.canvacord.util.data;

public abstract class DataStructure<T> {

	protected int size;
	protected int capacity;

	public boolean isEmpty() {
		return size == 0;
	}

	public boolean isFull() {
		return size >= capacity;
	}

	public abstract T peek();
	public abstract T poll();
	public abstract boolean push(T element);
	public abstract void remove();

	public abstract void clear();

	/**
	 * {@code pop()} is an alias for {@code remove()}.
	 */
	public void pop() {
		remove();
	}

}
