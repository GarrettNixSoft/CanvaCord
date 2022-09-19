package org.canvacord.util.data;

import java.util.ArrayList;
import java.util.List;

public class LinkedList<T> {

	public class Node {
		public T data;
		public Node previous;
		public Node next;
	}

	private int capacity;
	private int size;
	private Node first;
	private Node last;

	/**
	 * Create a new empty Linked List. The capacity is initialized to
	 * {@code Integer.MAX_VALUE}.
	 */
	public LinkedList() {
		capacity = Integer.MAX_VALUE;
		size = 0;
		first = last = null;
	}

	/**
	 * Create a new empty linked list with the specified capacity.
	 * @param capacity the maximum capacity of the Linked List
	 */
	public LinkedList(int capacity) {
		this.capacity = capacity;
		size = 0;
		first = last = null;
	}

	/**
	 * Generate a new Node object. Useful for manually
	 * manipulating the structure of the Linked List. Only
	 * use this if you know what you're doing.
	 * @return a new empty Node object
	 */
	public Node generateNode() {
		return new Node();
	}

	/**
	 * Get the capacity of this Linked List.
	 * @return the capacity of the list
	 */
	public int getCapacity() {
		return capacity;
	}

	/**
	 * Get the current size of this Linked List.
	 * @return the number of elements in the list
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Check whether this Linked List is empty.
	 * @return {@code true} if there are zero elements in the list
	 */
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Get the first Node in this Linked List.
	 * @return the first Node in the list
	 */
	public Node getFirst() {
		return first;
	}

	/**
	 * Get the last Node in this Linked List.
	 * @return the last Node in the list
	 */
	public Node getLast() {
		return last;
	}

	/**
	 * Set the maximum capacity of this linked list.
	 * @param newCapacity the new capacity
	 * @throws RuntimeException if the new capacity is smaller than the current size
	 * @throws IllegalArgumentException if the new capacity is negative
	 */
	public void setCapacity(int newCapacity) {
		if (newCapacity < size)
			throw new RuntimeException("New capacity is smaller than current size of this Linked List");
		else if (newCapacity < 0)
			throw new IllegalArgumentException("Linked List capacity cannot be negative");
		else capacity = newCapacity;
	}

	/**
	 * Set a new first node for this Linked List.
	 * Only do this if you know what you're doing!
	 * The size of the list will be automatically re-counted.
	 * @param newFirst the new first Node
	 */
	public void setFirst(Node newFirst) {
		first = newFirst;
		recomputeSize();
	}

	/**
	 * Set a new last node for this Linked List.
	 * Only do this if you know what you're doing!
	 * The size of the list will be automatically re-counted.
	 * @param newLast the new first Node
	 */
	public void setLast(Node newLast) {
		last = newLast;
		recomputeSize();
	}

	private void recomputeSize() {
		size = 0;
		Node ptr = first;
		while (ptr != null) {
			ptr = ptr.next;
			size++;
		}
	}

	/**
	 * Add an element to the beginning of this Linked List.
	 * @param element the element to add
	 */
	public void addFirst(T element) {
		Node newFirst = new Node();
		newFirst.data = element;
		newFirst.next = first;
		newFirst.previous = null;
		if (isEmpty()) last = newFirst;
		else first.previous = newFirst;
		first = newFirst;
		size++;
	}

	/**
	 * Add an element to the end of this Linked List.
	 * @param element the element to add
	 */
	public void addLast(T element) {
		Node newLast = new Node();
		newLast.data = element;
		newLast.next = null;
		newLast.previous = last;
		if (isEmpty()) first = newLast;
		else last.next = newLast;
		last = newLast;
		size++;
	}

	/**
	 * Get the first element from this Linked List.
	 * Does not mutate the list.
	 * @return the first element
	 */
	public T peekFirst() {
		return first.data;
	}

	/**
	 * Get the last element from this Linked List.
	 * Does not mutate the list.
	 * @return the last element
	 */
	public T peekLast() {
		return last.data;
	}

	/**
	 * Get the first element from this Linked List and remove it from the list.
	 * @return the first element from the Linked List
	 */
	public T pollFirst() {
		T data = first.data;
		popFirst();
		return data;
	}

	/**
	 * Get the last element from this Linked List and remove it from the list.
	 * @return the first element from the linked list
	 */
	public T pollLast() {
		T data = last.data;
		popLast();
		return data;
	}

	/**
	 * Remove the first element from this Linked List.
	 */
	public void popFirst() {
		first = first.next;
		size--;
	}

	/**
	 * Remove the last element from this Linked List.
	 */
	public void popLast() {
		last = last.previous;
		size--;
	}

	/**
	 * Clear this Linked List.
	 */
	public void clear() {
		size = 0;
		first = last = null;
	}

	/**
	 * Get all of the elements from this Linked List in a standard Java List.
	 * This operation runs in O(n) (linear) time.
	 * @return every element from the Linked List in a List
	 */
	public List<T> getElements() {
		List<T> elements = new ArrayList<>();
		Node ptr = first;
		while (ptr.next != null) {
			elements.add(ptr.data);
			ptr = ptr.next;
		}
		return elements;
	}

}
