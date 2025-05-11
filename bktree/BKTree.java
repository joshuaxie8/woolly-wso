package bktree;

import java.util.*;

public class BKTree<T, Data> implements Tree<T> {
	private class Node {
		T value; // key
		Data data; // value
		Map<Integer, Node> children = new HashMap<>();

		// TO-DO: implement storage of individual data in hash map outside of bk-tree
		// (good for actual implementation to WSO database)
		// thus data field may not be necessary
		public Node(T value) {
			this.value = value;
		}
	}

	private Node root;
	private int size = 0;
	private DistanceMetric<T> c; // a BK-tree "comparator" must return a range of integer values, not just -1, 0, and 1

	public BKTree(DistanceMetric<T> comparator) {
		c = comparator;
	}

	public boolean isEmpty() {
		if (size == 0) return true;
		return false;
	}

	public int size() {
		return size;
	}

	public boolean containsHelper(Node node, T val) {
		int dist = c.compute(node.value, val);

		if (dist == 0) {
			return true;
		}

		Node child = node.children.get(dist);

		if (child != null) {
			return containsHelper(child, val);
		}

		return false;

	}

	// whether or not the BK-tree contains an exact match to our value
	// if an exact match exists, find all prefix matches using a trie or similar data structure
	public boolean contains(T value) {
		if (isEmpty()) {
			return false;
		}

		Node curr = root;

		return containsHelper(curr, value);
	}

	private Node getHelper(Node node, T val) {
		int dist = c.compute(node.value, val);

		if (dist == 0) {
			return node;
		}

		Node child = node.children.get(dist);

		if (child != null) {
			return getHelper(child, val);
		}

		return null;

	}

	// gets the node corresponding to the given value; returns null if no match is found
	// return type is TBD - currently Node for generalizability
	public Node get(T value) {
		if (isEmpty()) {
			return null;

		}

		Node curr = root;

		return getHelper(curr, value);
	}

	// adds a new node to the BK-tree
	public void insert(T value) {
		insert(new Node(value));
	}
	public void insert(Node node) {
		if (isEmpty()) { // initial node
			root = node;
			size++;
			return;
		}

		Node current = root;
		int dist = c.compute(current.value, node.value);

		while (current.children.containsKey(dist)) {
			current = current.children.get(dist);
			dist = c.compute(current.value, node.value);
		}
		current.children.put(dist, node);
		size++;
	}

	/*
	Removes a node from the BK-tree - returns true if deletion is successful and false if not
	IMPORTANT: deletion in BK-trees is complicated, expensive and thus ideally rare
	In terms of WSO search, we shouldn't need to remove people after loading the BK-tree

	Deletes the node, then recursively reinserts child subtrees
	*/
	public boolean delete(T value) {
		if (root == null) return false; // if tree is empty

		if (root.value.equals(value)) { // special logic for deleting root
			Node old = root;
			root = null;
			size = 0;
			for (Node child : old.children.values()) {
				reinsertSubtree(child); // puts children back into tree
			}
			return true;
		}
		return deleteHelper(null, root, value); // normal case - node is NOT the root
	}

	// recursive delete method
	private boolean deleteHelper(Node parent, Node current, T value) {
		// INCOMPLETE
		int dist = c.compute(current.value, value);

		Node target	= current.children.get(dist); // target is a child of current
		if (target == null) return false; 	// node is missing

		if (target.value.equals(value)) { 	// if node is found
			current.children.remove(dist);	// remove hash map reference
			int oldSize = size;

			for (Node child : target.children.values()) { // reinserts children
				reinsertSubtree(child);
			}
			size = oldSize - 1;
			return true;
		}
		else { // keep recursing
			return deleteHelper(current, target, value);
		}
	}

	// so slow, wow
	private void reinsertSubtree(Node node) {
		for (Node child : node.children.values()) {
			reinsertSubtree(child);
		}
		node.children.clear();
		insert(node);

	}

	// updates a node - in practice just removes the old node and inserts one with the new key
	// returns true if edit is successful
	public boolean update(T oldValue, T newValue) {
		Node result = get(oldValue);
		if (result != null) { // if value exists
			delete(result.value);
			result.value = newValue;
			result.children.clear();
			insert(result);
			return true;
		}
		return false;
	}

	// searches for all stored values within a distance d from the given value (fuzzy search)
	// return type is TBD - currently Node for generalizability
	public ArrayList<Node> search(T value, int d) {
		return new ArrayList<Node>();
	}

	ArrayList<T> traverse() {
		ArrayList<T> result = new ArrayList<T>();
		traverseHelper(root, result);
		return result;
	}

	private void traverseHelper(Node node, ArrayList<T> result) {
		if (node == null) return;

		result.add(node.value);

		for (Node child : node.children.values()) {
			traverseHelper(child, result);
		}
	}

	public static void main(String[] args) {
		//test cases
		BKTree<String,Integer> tests = new BKTree<String, Integer>(MetricFunctions.Lev);
		tests.insert("book");
		tests.insert("cake");
		tests.insert("books");
		tests.insert("boo");
		tests.insert("cape");
		tests.insert("cart");
		tests.insert("boon");
		tests.insert("cook");

		System.out.println("GET: " + tests.get("cook").value);
		System.out.println(tests.delete("book"));
		System.out.println(tests.delete("book"));
		tests.update("cook", "gurt");

		ArrayList<String> names = tests.traverse();
		System.out.println("Printing tree:"); // weird bug where 'books' appears twice
		for (String name : names) {
			System.out.println(name);
		}
	}
}