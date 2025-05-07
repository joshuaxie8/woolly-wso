package bktree;

import java.util.*;

public class BKTree<T, Data> implements Tree<T> {
	private class Node {
		T value;
		Data data;
		Map<Integer, Node> children = new HashMap<>();

		// TO-DO: implement storage of individual data in hash map outside of bk-tree
		// (good for actual implementation to WSO database)
		// data field may not be necessary
		public Node(T value) {
			this.value = value;
		}
	}

	private Node root;
	private int size = 0;
	private Comparator<T> c; // a BK-tree comparator must return a range of integer values, not just -1, 0, and 1

	public BKTree(Comparator<T> comparator) {
		c = comparator;
	}

	public boolean isEmpty() {
		if (size == 0) return true;
		return false;
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
	// return type is TBD - currently Node for generalizability
	public Node get(T value) {
		return null;
	}

	// adds a new node to the BK-tree
	public void insert(T value) {
		Node node = new Node(value);
	}
	public void insert(Node node) {

	}

	// removes a node from the BK-tree - returns true if deletion is successful
	public boolean delete(T value) {
		return contains(value);
	}

	// updates a node - in practice just removes the old node and inserts one with the new value
	// returns true if edit is successful
	public boolean update(Node node, T newValue) {
		return false;
	}

	// searches for all stored values within a distance d from the given value (fuzzy search)
	// return type is TBD - currently Node for generalizability
	public ArrayList<Node> search(T value, int d) {
		return new ArrayList<Node>();
	}

	public static void main(String[] args) {
		// test cases here
	}
}