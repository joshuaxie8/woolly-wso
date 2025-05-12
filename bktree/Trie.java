package bktree;

import java.util.*;

// Trie class for exact matches before moving to BK tree
// Adapted from lab 7 using hash maps instead of arrays for more flexible matching
public class Trie<T, Data> implements Tree<T> {	// generic T is lowkey annoying
	private class Node {
		Map<Character, Node> children;
        public boolean isTerminal;
        Data data;

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

    public boolean insert(T value) {
    	String word = "";
    	if (value instanceof String) { // casts generic type to a string
		    word = (String) value;
		}

        int len = word.length(), asNum;
        Node current = root;

        for (int i = 0; i < len; i++) {
            if (current.children.get(word.charAt(i)) == null) {
                current.children.put(word.charAt(i), new Node()); // if path doesn't exist, create it
            }
            current = current.children.get(word.charAt(i));
        }

        if (!current.isTerminal) {
            current.isTerminal = true;
            size++;
            return true;
        }
        return false; // word already exists
    }

    public boolean insert(T value, Data data) {
    	String word = "";
    	if (value instanceof String) {
		    word = (String) value;
		}

        int len = word.length(), asNum;
        Node current = root;

        for (int i = 0; i < len; i++) {
            if (current.children.get(word.charAt(i)) == null) {
                current.children.put(word.charAt(i), new Node()); // if path doesn't exist, create it
            }
            current = current.children.get(word.charAt(i));
        }

        if (!current.isTerminal) {
            current.isTerminal = true;
            current.data = data;
            size++;
            
            return true;
        }
        return false;
    }

    public boolean delete(T value) {
    	String word = "";
    	if (value instanceof String) {
		    word = (String) value;
		}

        int len = word.length(), asNum;
        Node current = root;

        for (int i = 0; i < len; i++) {
        	current = current.children.get(word.charAt(i));
        	if (current == null) return false;
        }
        
        if (current.isTerminal) {
            current.isTerminal = false;
            current.data = null;
            size--;

            return true;
        }

        return false;
    }

    public boolean contains(T value) {
    	String word = "";
    	if (value instanceof String) {
		    word = (String) value;
		}

        int len = word.length(), asNum;
        Node current = root;

        for (int i = 0; i < len; i++) {
        	current = current.children.get(word.charAt(i));
        	if (current == null) return false;
        }

        if (current.isTerminal) return true;
    	return false; 
    }

    public Node probe(T value) { // given a string, finds the node whose path corresponds to that string and returns it
    	String word = "";
    	if (value instanceof String) {
		    word = (String) value;
		}

		int len = word.length();
        Node current = root;

        for (int i = 0; i < len; i++) {
            current = current.children.get(word.charAt(i));
        	if (current == null) return null;
        }
        return current; // returns the node whether or not it is a terminal
    }

    public ArrayList<String> traverseVals(Node start, String s) {
        if (start == null) start = root;
        ArrayList<String> result = new ArrayList<String>();
        traverseValsHelper(new StringBuilder(s), start, result); // use StringBuilder to save memory
        return result;
    }

    private void traverseValsHelper(StringBuilder sb, Node current, ArrayList<String> result) {
        if (current.isTerminal) {
        	result.add(sb.toString());
        }
        for (Character c : current.children.keySet()) {
        	sb.append(c);						// add character
        	traverseValsHelper(sb, current.children.get(c), result);
        	sb.deleteCharAt(sb.length() - 1); 	// backtrack

        }
    }

    public ArrayList<Data> traverseData(Node start, String s) {
        if (start == null) start = root;
        ArrayList<Data> result = new ArrayList<Data>();
        traverseDataHelper(new StringBuilder(s), start, result);
        return result;
    }

    private void traverseDataHelper(StringBuilder sb, Node current, ArrayList<Data> result) {
        if (current.isTerminal) {
            result.add(current.data); // might add null data
        }
        for (Character c : current.children.keySet()) {
            sb.append(c);                       // add character
            traverseDataHelper(sb, current.children.get(c), result);
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

        test.insert("a b c", "Andrew Bartholomew Charles");
        test.insert("a beetle");
        test.insert("a b c");

        System.out.println("values:");
        ArrayList<String> print = test.traverseVals(null, "");
        for (int i = 0; i < print.size(); i++) {
            System.out.println(print.get(i));
        }

        System.out.println("data:");
        print = test.traverseData(null, "");
        for (int i = 0; i < print.size(); i++) {
            System.out.println(print.get(i));
        }
    }
}