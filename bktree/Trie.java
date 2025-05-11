package bktree;

import java.util.*;

// Trie class for exact matches before moving to BK tree
// Adapted from lab 7
public class Trie<T> implements Tree<T> {
	public class Node {
        public Node[] children;
        public boolean isTerminal;

        public Node() {
            this.children = (Node[]) new Object[26]; // one slot for each lowercase letter (expand later)
            this.isTerminal = false;
        }
    }

    public Node root;
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
            asNum = word.charAt(i) - 'a';

            if (current.children[asNum] == null) {
                current.children[asNum] = new Node(); // if path doesn't exist, create it
            }
            current = current.children[asNum];
        }

        if (!current.isTerminal) {
            current.isTerminal = true;
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

        int len = word.length();
        Node current = root;

        for (int i = 0; i < len; i++) {
            current = current.children[word.charAt(i) - 'a'];
            if (current == null) { return false; }
        }
        
        if (current.isTerminal) {
            current.isTerminal = false;
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

        int len = word.length();
        Node current = root;

        for (int i = 0; i < len; i++) {
            current = current.children[word.charAt(i) - 'a'];
            if (current == null) { return false; }
        }

        if (current.isTerminal) { return true; }
    	return false; 
    }

    public ArrayList<String> traverse(){
        ArrayList<String> result = new ArrayList<String>();
        traverseHelper("", root, result); 
        return result;
    }

    private void traverseHelper(String s, Node current, ArrayList<String> list) {
        if (current.isTerminal) {
            list.add(s);
        }
        for (int i = 0; i < 26; i++) {
            if (current.children[i] != null) {
                traverseHelper(s + (char) ('a' + i), current.children[i], list);
            }
        }
    }
}