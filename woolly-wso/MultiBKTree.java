package woollywso;

import java.util.*;

public class MultiBKTree<V> {
	private class Node {
		String[] keys; // {fullname, firstname, middlename, lastname, hometown, etc}
		V val;
		@SuppressWarnings("unchecked")
		Map<Integer, Node>[] children = (Map<Integer, Node>[]) new HashMap[numKeys];

		public Node(String[] keys) {
			this.keys = keys;
		}

		public Node(String[] keys, V val) {
			this.keys = keys;
			this.val = val;
			numKeys = keys.length;
		}
	}

	private Node root;
	private int size = 0;
	private DistanceMetric<String> c; // a BK-tree "comparator" must return a range of integer values, not just -1, 0, and 1
	int numKeys;

	public MultiBKTree(DistanceMetric<String> comparator, int numKeys) {
		c = comparator;
		this.numKeys = numKeys;
	}

	public boolean isEmpty() {
		if (size == 0) return true;
		return false;
	}

	public int size() {
		return size;
	}


	public boolean contains(String key, int x) {
		Node node = get(key, x);
		return node == null;
	}

	// gets the shallowest node corresponding to the given value; returns null if no match is found
	public Node get(String key, int x) {
		if (isEmpty()) {
			return null;
		}

		Node curr = root;

		return getHelper(curr, key, x);
	}
	private Node getHelper(Node node, String key, int x) {
		int dist = c.compute(node.keys[x], key);

		if (dist == 0) {
			return node;
		}

		Node child = node.children[x].get(dist);

		if (child != null) {
			return getHelper(child, key, x);
		}

		return null;
	}

	public boolean insert(String[] keys, V val) {
		Node node = new Node(keys, val);
		return insert(node);
	}
	public boolean insert(Node node) {
		if (isEmpty()) { // initial node
			root = node;
			size++;
			return true;
		}

		for (int i = 0; i < numKeys; i++) {
			Node current = root;
			int d = c.compute(current.keys[i], node.keys[i]); // distance

			while (current.children[i].containsKey(d)) {
				current = current.children[i].get(d);
				if (current.val.equals(node.val)) return false;
				d = c.compute(current.keys[i], node.keys[i]);
			}
			current.children[i].put(d, node);
		}
		size++;
		return true;
	}

	/*
	APPROXIMATE STRING MATCHING

	Fuzzily searches for all stored nodes with values within a tolerance distance tol from the given key
	If exactMatches is FALSE, does not add nodes where key is an exact match (distance = 0)
	*/

	// note: both fuzzy methods search by KEY only

	// performs a fuzzy search on a specific tree, returning an array list of keys
	// fuzzyKeys() is used primarily for testing
	public ArrayList<String> fuzzyKeys(String key, int tol, int x, boolean exactMatches, boolean sort) {
		ArrayList<Node> results = fuzzy(key, tol, x, exactMatches, sort); // get results
		ArrayList<String> keys = new ArrayList<>();
		for (Node node : results) keys.add(node.keys[x]);
		return keys;
	}

	// performs a fuzzy search on a specific tree, returning an array list of values
	public ArrayList<V> fuzzyVals(String key, int tol, int x, boolean exactMatches, boolean sort) {
		ArrayList<Node> results = fuzzy(key, tol, x, exactMatches, sort); // get results
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

	private ArrayList<Node> fuzzy(String key, int tol, int x, boolean exactMatches, boolean sort) {
		ArrayList<Node> results = new ArrayList<>();

		fuzzyHelper(root, key, tol, x, results, exactMatches);

		if (sort) {
			ArrayList<ScoredNode> scored = new ArrayList<>();
			for (Node node : results) {
				scored.add(new ScoredNode(node, MetricFunctions.commonPrefixLength(node.keys[x], key)));
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
	private void fuzzyHelper(Node node, String key, int tol, int x, ArrayList<Node> results, boolean exactMatches) {
		if (node == null) {
			return;
		}

		int d = c.compute(node.keys[x], key);

		if (d <= tol && (d != 0 || exactMatches)) {
			results.add(node); // adds the node if key distance is within range
		}

		for (int i = (d - tol); i <= (d + tol); i++) {
			Node currChild = node.children[x].get(i);

			if (currChild != null) {
				fuzzyHelper(currChild, key, tol, x, results, exactMatches);
			}
		}
	}

	/*
	IN-ORDER TREE TRAVERSAL

	Note: though traverse() uses in-order traversal, the resulting array list is not in a sorted order!
	Whatever x is, traverse() still returns the full list of nodes.
	*/
	private ArrayList<Node> traverse(int x) {
		ArrayList<Node> results = new ArrayList<>(size);
		traverseHelper(root, x, results);
		return results;
	}
	private void traverseHelper(Node node, int x, ArrayList<Node> results) {
		if (node == null) return;
		results.add(node);
		for (Node child : node.children[x].values()) {
			traverseHelper(child, x, results);
		}
	}

	// these traverse methods simply recast traverse() into the desired type
	// somewhat slow, but primarily for testing
	public ArrayList<String> getAllKeys(int x) {
		ArrayList<Node> results = traverse(x);
		ArrayList<String> keys = new ArrayList<>(size);
		for (Node node : results) keys.add(node.keys[x]);
		return keys;
	}

	public ArrayList<V> getAllVals(int x) {
		ArrayList<Node> results = traverse(x);
		ArrayList<V> vals = new ArrayList<>(size);
		for (Node node : results) vals.add(node.val);
		return vals;
	}

}