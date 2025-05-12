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

    public ArrayList<String> traverse(boolean getData){ // getData: if true, adds data to array list instead of string
        ArrayList<String> result = new ArrayList<String>();
        traverseHelper(new StringBuilder(), root, result, getData); // use StringBuilder to save memory
        return result;
    }

    private void traverseHelper(StringBuilder sb, Node current, ArrayList<String> list, boolean getData) {
        if (current.isTerminal) {
        	if (getData) {
                if (current.data != null) {
                    list.add(current.data.toString());
                }
        		else {
                    list.add("missing data!");
                }
        	}
        	else {
        		list.add(sb.toString());
        	}
        }
        for (Character c : current.children.keySet()) {
        	sb.append(c);						// add character
        	traverseHelper(sb, current.children.get(c), list, getData);
        	sb.deleteCharAt(sb.length() - 1); 	// backtrack

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

        System.out.println("traversing");
        ArrayList<String> print = test.traverse(true);
        for (int i = 0; i < print.size(); i++) {
            System.out.println(print.get(i));
        }
    }
}