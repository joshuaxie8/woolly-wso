package bktree;

import java.util.*;

public class BKTree<T, Data> implements Tree<T> {
	private class Node {
		T value;
		Data data;
		Map<Integer, Node> children = new HashMap<>();

		// to do: define data object (class year, major, other info etc.)
		// or should we store student data in a separate map?
		public Node(T value, Data data) {
			this.value = value;
			this.data = data;
		}
	}

	private Node root;
	private int size = 0;
	private Comparator<T> c;

	public BKTree(Comparator<T> comparator) {
		c = comparator;
	}

	public boolean isEmpty() {
		return true;
	}

	public int size() {
		return size;
	}

	// whether or not the BK-tree contains an exact match to our value
	// if an exact match exists, find all prefix matches using a trie or similar data structure
	public boolean contains(T value) {
		return false;
	}

	// gets the node corresponding to the given value; returns null if no match is found
	public Node get(T value) {
		return null;
	}

	// decide which one is better
	public void insert(T value) { // insertion with value

	}

	public void insert(Node node) { // insertion with node

	}

	public boolean delete(T value) {
		return contains(value);
	}

	// edits a node - in practice just removes the old node and inserts one with the new value
	public boolean edit(Node node, T newValue) {
		return false;
	}

	// searches for all stored values within a distance d from the given value (fuzzy search)
	public ArrayList<T> search(T value, int d) {
		return new ArrayList<T>();
	}

	public static void main(String[] args) {
		// test cases here
	}
}