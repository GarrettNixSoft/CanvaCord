package org.canvacord.util.data;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * A simple Stack implementation.
 * @param <T> the type of data to store
 */
public class Stack<T> extends DataStructure<T> {

	class Node {
		T data;
		Node next;
	}

	private Node first;

	public Stack() {
		this.size = 0;
		this.first = null;
		this.capacity = 100;
	}

	public Stack(int capacity) {
		this.capacity = capacity;
		this.size = 0;
		this.first = null;
	}

	/**
	 * Add an element to the top of this stack.
	 * @param element the element to add
	 * @return {@code true} if there is room in the stack and the element is added, or {@code false} if the stack is full.
	 */
	public boolean push(T element) {
		if (size == capacity) return false;
		Node newFirst = new Node();
		newFirst.data = element;
		newFirst.next = first;
		first = newFirst;
		size++;
		return true;
	}

	/**
	 * Get the element at the top of the stack.
	 * @return the next element
	 */
	public T peek() {
		if (first == null) throw new NoSuchElementException("This stack is empty!");
		else return first.data;
	}

	/**
	 * Get the element at the top of the stack,
	 * and remove it from the stack.
	 * @return the next element
	 */
	public T poll() {
		T result = peek();
		remove();
		return result;
	}

	/**
	 * Remove the element at the top of this stack.
	 */
	public void remove() {
		if (first == null) throw new NoSuchElementException("This stack is empty!");
		first = first.next;
		size--;
	}

	/**
	 * Clear the stack. Removes all elements and sets the size to 0 in O(1) time.
	 */
	public void clear() {
		size = 0;
		first = null;
	}

	/**
	 * Get the number of elements currently in the stack.
	 * @return the size of the stack
	 */
	public int size() {
		return size;
	}

	/**
	 * Check if the stack is empty.
	 * @return {@code true} if there are zero elements in this stack
	 */
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Check if this stack has another element.
	 * @return {@code true} if there is at least one element in this stack
	 */
	public boolean hasNext() {
		return size > 0;
	}

	/**
	 * Retrieve all elements from this Stack in order (bottom to top).
	 * @return an array containing all elements in the Stack
	 */
	public List<T> getElements() {

		List<T> result = new ArrayList<>(size);
		Stack<T> other = new Stack<>(size);

		while (!isEmpty()) {
			other.push(poll());
		}

		// now put everything back into this stack
		while (!other.isEmpty()) {
			T element = other.poll();
			push(element);
			result.add(element);
		}

		return result;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Stack[");
		for (T element : getElements()) {
			stringBuilder.append(element).append(", ");
		}
		stringBuilder.setLength(stringBuilder.length() - 2);
		stringBuilder.append(']');
		return stringBuilder.toString();
	}

}
