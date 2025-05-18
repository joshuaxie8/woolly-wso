package woollywso;

import java.util.Comparator;

public interface Tree<K, V> {

	// trivial method - true if tree is empty and false if not
	public abstract boolean isEmpty();

	// trivial method - returns number of elements in tree
	public abstract int size();

	// returns true or false depending on whether the tree contains a given key
	public abstract boolean contains(K key);

	// inserts a new node into the tree
	// returns false if key-value pair already exists
	public abstract boolean insert(K key, V value);

	// removes a node from a tree
	// true if deletion is successful
	// public abstract boolean delete(K key, V value);
}