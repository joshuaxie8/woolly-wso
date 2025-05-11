package bktree;

import java.util.Comparator;

public interface Tree<T> {

	// trivial method - true if tree is empty and false if not
	public abstract boolean isEmpty();

	// trivial method - returns number of elements in tree
	public abstract int size();

	// returns true or false depending on whether the tree contains a given value
	public abstract boolean contains(T value);

	// inserts a new value into the tree
	// returns false if value already exists
	public abstract boolean insert(T value);

	// removes a value from a tree
	// true if deletion is successful
	public abstract boolean delete(T value);
}