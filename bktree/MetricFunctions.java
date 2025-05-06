package bktree;

import java.util.*;

public class MetricFunctions {

	// Levenshtein distance
	// Runs in O(m*n) where m and n are the lengths of a and b
	public static int Lev(String a, String b) {
		int m = a.length();
		int n = b.length();

		if (m == 0) return n;
		if (n == 0) return m;

		int[] d1 = new int[n + 1];
		int[] d2 = new int[n + 1];
		
		Arrays.setAll(d1, i -> i);

		for (int i = 0; i < m; i++) {
			d2[0] = i + 1;

			for (int j = 0; j < n; j++) {
				int del = d1[j + 1] + 1;
				int ins = d2[j] + 1;
				int sub = d1[j];
				if (a.charAt(i) != b.charAt(j)) {
					sub++;
				}
				d2[j + 1] = Math.min(del, Math.min(ins, sub));
			}
			int[] temp = d1;
			d1 = d2;
			d2 = temp;
		}
		return d1[n];
	}

	// Damerau-Levenshtein distance
	public static int DamLev(String a, String b, int i, int j) {
		return 0;
	}

	// testing
	public static void main(String[] args) {
		System.out.println(Lev("aaron", "aardvark"));
	}
}