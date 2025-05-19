package woollywso;

import java.util.*;

// implementation of BK tree with key value pairs
public class BKTree<K, V> implements Tree<K, V> {
	private class Node {
		K key; // key
		V val; // value
		Map<Integer, Node> children = new HashMap<>();

		// TO-DO: implement storage of individual data in hash map outside of bk-tree
		// (good for actual implementation to WSO database)
		public Node(K key) {
			this.key = key;
		}
	}

	private Node root;
	private int size = 0;
	private DistanceMetric<K> c; // a BK-tree "comparator" must return a range of integer values, not just -1, 0, and 1

	public BKTree(DistanceMetric<K> comparator) {
		c = comparator;
	}

	public boolean isEmpty() {
		if (size == 0) return true;
		return false;
	}

	public int size() {
		return size;
	}

	// whether or not the BK-tree contains an exact match to our key
	// if an exact match exists, find all prefix matches using a trie or similar data structure
	public boolean contains(K key) {
		if (isEmpty()) {
			return false;
		}

		Node curr = root;

		return containsHelper(curr, key);
	}
	public boolean containsHelper(Node node, K key) {
		int dist = c.compute(node.key, key);

		if (dist == 0) {
			return true;
		}

		Node child = node.children.get(dist);

		if (child != null) {
			return containsHelper(child, key);
		}

		return false;
	}

	// gets the shallowest node corresponding to the given value; returns null if no match is found
	// return type is TBD - currently Node for generalizability
	public Node get(K key) {
		if (isEmpty()) {
			return null;
		}

		Node curr = root;

		return getHelper(curr, key);
	}
	private Node getHelper(Node node, K key) {
		int dist = c.compute(node.key, key);

		if (dist == 0) {
			return node;
		}

		Node child = node.children.get(dist);

		if (child != null) {
			return getHelper(child, key);
		}

		return null;
	}

	public boolean insert(K key, V val) {
		Node node = new Node(key);
		node.val = val;
		return insert(node);
	}
	public boolean insert(Node node) {
		if (node.key == null) return false;
		if (isEmpty()) { // initial node
			root = node;
			size++;
			return true;
		}

		Node current = root;
		int dist = c.compute(current.key, node.key);

		while (current.children.containsKey(dist)) {
			current = current.children.get(dist);
			if (current.val.equals(node.val)) return false; // key value pair already exists
			dist = c.compute(current.key, node.key);
		}
		current.children.put(dist, node);
		size++;
		return true;
	}

	/*
	APPROXIMATE STRING MATCHING

	Fuzzily searches for all stored nodes with values within a tolerance distance tol from the given key
	If exactMatches is FALSE, does not add nodes where key is an exact match (distance = 0)
	*/

	// note: both fuzzy methods search by KEY only

	// performs a fuzzy search on the BK tree, returning an array list of keys
	// fuzzyKeys() is used primarily for testing
	public ArrayList<K> fuzzyKeys(K key, int tol, boolean exactMatches, boolean sort) {
		ArrayList<Node> results = fuzzy(key, tol, exactMatches, sort); // get results
		ArrayList<K> keys = new ArrayList<>();
		for (Node node : results) keys.add(node.key);
		return keys;
	}

	// performs a fuzzy search on the BK tree, returning an array list of values
	public ArrayList<V> fuzzyVals(K key, int tol, boolean exactMatches, boolean sort) {
		ArrayList<Node> results = fuzzy(key, tol, exactMatches, sort); // get results
		ArrayList<V> vals = new ArrayList<>();
		for (Node node : results) vals.add(node.val);
		return vals;
	}

	class ScoredNode {
		Node node;
		int score;
		ScoredNode(Node node, int score) {
			this.node = node;
			this.score = score;
		}
	}

	private ArrayList<Node> fuzzy(K key, int tol, boolean exactMatches, boolean sort) {

		ArrayList<Node> results = new ArrayList<>();
		fuzzyHelper(root, key, tol, results, exactMatches);

		if (sort) {
			ArrayList<ScoredNode> scored = new ArrayList<>();
			for (Node node : results) {
				scored.add(new ScoredNode(node, MetricFunctions.commonPrefixLength((String) node.key, (String) key)));
			}
			Collections.sort(scored, new Comparator<ScoredNode>() {
			    @Override
			    public int compare(ScoredNode a, ScoredNode b) {
			        return Integer.compare(b.score, a.score);
			    }
			});
			ArrayList<Node> sortedResults = new ArrayList<>();
			for (ScoredNode sn : scored) {
				sortedResults.add(sn.node);
			}
			return sortedResults;
		}

		return results;
	}
	private void fuzzyHelper(Node node, K key, int tol, ArrayList<Node> results, boolean exactMatches) {
		if (node == null) {
			return;
		}

		int dist = c.lazyCompute(node.key, key, tol);

		if (dist <= tol && (dist != 0 || exactMatches)) {
			results.add(node); // adds the node if key distance is within range
		}

		for (int i = (dist - tol); i <= (dist + tol); i++) {
			Node currChild = node.children.get(i);

			if (currChild != null) {
				fuzzyHelper(currChild, key, tol, results, exactMatches);
			}
		}
	}

	/*
	IN-ORDER TREE TRAVERSAL

	Note: though travers() uses in-order traversal, the resulting array list is not in a sorted order!
	*/
	private ArrayList<Node> traverse() {
		ArrayList<Node> results = new ArrayList<>(size);
		traverseHelper(root, results);
		return results;
	}

	private void traverseHelper(Node node, ArrayList<Node> results) {
		if (node == null) return;
		results.add(node);
		for (Node child : node.children.values()) traverseHelper(child, results);
	}

	// these traverse methods simply recast traverse() into the desired type
	// somewhat slow, but primarily for testing
	public ArrayList<K> getAllKeys() {
		ArrayList<Node> results = traverse();
		ArrayList<K> keys = new ArrayList<>(size);
		for (Node node : results) keys.add(node.key);
		return keys;
	}

	public ArrayList<V> getAllVals() {
		ArrayList<Node> results = traverse();
		ArrayList<V> vals = new ArrayList<>(size);
		for (Node node : results) vals.add(node.val);
		return vals;
	}

	// NODE DELETION NOT USED

	/*
	Removes a node from the BK-tree - returns true if deletion is successful and false if not
	IMPORTANT: deletion in BK-trees is complicated, expensive and thus ideally rare
	In terms of WSO search, we shouldn't need to remove people after loading the BK-tree

	Deletes the node, then recursively reinserts child subtrees
	*/
	// public boolean delete(K key, V val) {
	// 	if (root == null) return false; // if tree is empty

	// 	if (root.key.equals(key)) { // special logic for deleting root
	// 		Node old = root;
	// 		root = null;
	// 		size = 0;
	// 		for (Node child : old.children.values()) {
	// 			reinsertSubtree(child); // puts children back into tree
	// 		}
	// 		return true;
	// 	}
	// 	return deleteHelper(null, root, key); // normal case - node is NOT the root
	// }

	// // recursive delete method
	// private boolean deleteHelper(Node parent, Node current, K key) {
	// 	int dist = c.compute(current.key, key);

	// 	Node target	= current.children.get(dist); // target is a child of current
	// 	if (target == null) return false; 	// node is missing

	// 	if (target.key.equals(key)) { 	// if node is found
	// 		current.children.remove(dist);	// remove hash map reference
	// 		int oldSize = size;

	// 		for (Node child : target.children.values()) { // reinserts children
	// 			reinsertSubtree(child);
	// 		}
	// 		size = oldSize - 1;
	// 		return true;
	// 	}
	// 	else { // keep recursing
	// 		return deleteHelper(current, target, key);
	// 	}
	// }

	// // so slow, wow
	// private void reinsertSubtree(Node node) {
	// 	for (Node child : node.children.values()) {
	// 		reinsertSubtree(child);
	// 	}
	// 	node.children.clear();
	// 	insert(node);
	// }

	public static void main(String[] args) {
		//test cases
		// BKTree<String,Integer> tests = new BKTree<String, Integer>(MetricFunctions.lev);
		// tests.insert("book", 1);
		// tests.insert("cake", 2);
		// tests.insert("books", 3);
		// tests.insert("boo", 4);
		// tests.insert("cape", 5);
		// tests.insert("cart", 6);
		// tests.insert("boon", 7);
		// tests.insert("cook", 8);

		// System.out.println("GET: " + tests.get("cook").value);
		// System.out.println(tests.delete("book"));
		// System.out.println(tests.delete("book"));
		// tests.update("cook", "gurt");

		// ArrayList<String> names = tests.traverseVals();
		// System.out.println("Printing tree:");
		// for (String name : names) {
		// 	System.out.println(name);
		// }

		// ArrayList<Integer> data = tests.traverseData();
		// System.out.println("Printing tree data:");
		// System.out.println(data);
	}
}