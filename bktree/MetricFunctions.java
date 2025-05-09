package bktree;

import java.util.*;

public class MetricFunctions {

	// Levenshtein distance
	// Runs in O(m*n) where m and n are the lengths of a and b
	public static DistanceMetric<String> Lev = new DistanceMetric<String>() {
		@Override
		public int compute(String a, String b) {
			a = a.toLowerCase();
			b = b.toLowerCase();
			int m = a.length();
			int n = b.length();

			if (m == 0) return n;
			if (n == 0) return m;

			int[] d1 = new int[n + 1];
			int[] d2 = new int[n + 1];
			
			Arrays.setAll(d1, i -> i); // d1 = {0, 1, 2, ... , n};

			for (int i = 0; i < m; i++) {
				d2[0] = i + 1;

				for (int j = 0; j < n; j++) {
					int del = d1[j + 1] + 1;
					int ins = d2[j] + 1;
					int sub = (a.charAt(i) == b.charAt(j)) ? d1[j] : d1[j] + 1;
					d2[j + 1] = Math.min(del, Math.min(ins, sub));
				}
				int[] temp = d1; // swap d1 and d2
				d1 = d2;
				d2 = temp;
			}
			return d1[n];
		}
	};

	// Damerau-Levenshtein distance (unfinished)

	public static int DamLev(String a, String b) {
		int m = a.length();
		int n = b.length();

		if (m == 0) return n;
		if (n == 0) return m;

		return 0;
	}

	// testing
	public static void main(String[] args) {
		System.out.println(MetricFunctions.Lev.compute("aardvark", "AAAAAAAK"));

	}
}