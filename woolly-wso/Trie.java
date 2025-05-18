package woollywso;

import java.util.*;

// Trie class for exact matches before moving to BK tree
// Adapted from lab 7 using hash maps instead of arrays for more flexible matching
// Note: allows for multiple value entries into the same terminal node
public class Trie<K, V> implements Tree<K, V> {
	private class Node {
		Map<Character, Node> children;
        public boolean isTerminal;
        ArrayList<V> values = new ArrayList<>();

        public Node() {
            this.children = new HashMap<>();	// hashmap for more flexible children matching
            this.isTerminal = false;			// currently store names as lowercase for simplicity
        }
    }

    private Node root;
	private int size = 0;

    public Trie() {
    	root = new Node(); 
    }

    public boolean isEmpty() {
		if (size == 0) return true;
		return false;
	}

    public int size() {
    	return size;
    }

    public ArrayList<V> getValues(Node node) {
        if (node.values == null) return new ArrayList<V>();
        return node.values;
    }

    public boolean insert(K key, V val) {
    	String word = "";
        if (key == null) return false;
    	if (key instanceof String) word = (String) key;             // turns key into a String

        int len = word.length(), asNum;
        Node current = root;

        for (int i = 0; i < len; i++) {
            if (current.children.get(word.charAt(i)) == null) {
                current.children.put(word.charAt(i), new Node());   // if path doesn't exist, create it
            }
            current = current.children.get(word.charAt(i));
        }

        if (!current.isTerminal) current.isTerminal = true;         // if current node isn't marked as terminal, mark it
        if (current.values.contains(val)) return false;             // if key-value pair exists, don't insert and return false
        current.values.add(val);                                    // add value to node
        size++;
        return true;
    }

    public boolean contains(K key) {
    	String word = "";
    	if (key instanceof String) word = (String) key;

        int len = word.length(), asNum;
        Node current = root;

        for (int i = 0; i < len; i++) {
        	current = current.children.get(word.charAt(i));
        	if (current == null) return false;
        }

        if (current.isTerminal) return true;
    	return false; 
    }

    /*
    given a string, finds the node whose path corresponds to that string and returns it
    */
    public Node probe(K key) { 
    	String word = "";
        if (key instanceof String) word = (String) key;

		int len = word.length();
        Node current = root;

        for (int i = 0; i < len; i++) {
            current = current.children.get(word.charAt(i));
        	if (current == null) return null; // if node doesn't exist, return null
        }
        return current; // returns the node whether or not it is a terminal
    }

    /*
    TREE TRAVERSAL

    We use StringBuilders for memory efficiency
    Note: maybe consolidate traverseKeys() and traverseVals() logic?
    */
    public ArrayList<String> traverseKeys(Node start, String s) {
        ArrayList<String> result = new ArrayList<String>();
        if (start == null) return result;
        traverseKeysHelper(new StringBuilder(s), start, result);
        return result;
    }
    private void traverseKeysHelper(StringBuilder sb, Node current, ArrayList<String> result) {
        if (current.isTerminal) {
        	result.add(sb.toString());
        }
        for (Character c : current.children.keySet()) {
        	sb.append(c);						// add character
        	traverseKeysHelper(sb, current.children.get(c), result);
        	sb.deleteCharAt(sb.length() - 1); 	// backtrack
        }
    }

    // when traversing values, we might end up with a larger array list!
    public ArrayList<V> traverseVals(Node start, String s) {
        ArrayList<V> result = new ArrayList<V>();
        if (start == null) return result;
        traverseValsHelper(new StringBuilder(s), start, result);
        return result;
    }
    private void traverseValsHelper(StringBuilder sb, Node current, ArrayList<V> result) {
        if (current.isTerminal) { // every terminal node has at least one value, and all values are stored in terminal nodes
            result.addAll(current.values);
        }
        for (Character c : current.children.keySet()) {
            sb.append(c);                       // add character
            traverseValsHelper(sb, current.children.get(c), result);
            sb.deleteCharAt(sb.length() - 1);   // backtrack
        }
    }

    public static void main(String[] args){
        Trie<String, String> test = new Trie<String, String>();
        // System.out.println(test.insert("anteater"));
        // System.out.println(test.insert("ant"));
        // System.out.println(test.insert("anteater"));
        // System.out.println(test.insert("and"));

        // System.out.println(test.delete("anteater"));
        // System.out.println(test.delete("anteater"));
        // System.out.println(test.delete("ant"));
        // System.out.println(test.delete("beetle"));

        // System.out.println(test.contains("and"));
        // System.out.println(test.contains("anteater"));
        // test.insert("anteater");
        // test.insert("andeater");
        // test.insert("computer");
        // test.insert("beetle");
        // System.out.println(test.contains("anteater"));

        // test.insert("a b c", "Andrew Bartholomew Charles");
        // test.insert("a beetle");
        // test.insert("a b c");

        // System.out.println("Keys:");
        // ArrayList<String> print = test.traverseKeys(null, "");
        // for (int i = 0; i < print.size(); i++) {
        //     System.out.println(print.get(i));
        // }

        // System.out.println("data:");
        // print = test.traverseData(null, "");
        // for (int i = 0; i < print.size(); i++) {
        //     System.out.println(print.get(i));
        // }
    }
}