package bktree;

import java.util.*;
import java.text.Normalizer;

// this is our main file
public class Search {
	People p = new People();	// automatically ingests people
	BKTree<String, String> bk; 	// bk tree!
	Trie<String, String> t;		// trie!

	public Search(DistanceMetric<String> c) {
		bk = new BKTree<String, String>(c);
		t = new Trie<String, String>();
	}

	public ArrayList<String> getResults(String query, int maxDist, int priority) {
		return null;
	}

	/*
	STRING SIMPLIFICATION - FOR BOTH USER INPUT AND STORED VALUES

	given a String s, returns an array list of all normalized, "reasonable" parsings of s

	ex.
	simplify("Waage-Pickle") -> {"waagepickle", "waage pickle"}
	simplify("José") -> {"jose"}
	simplify("O'Connor") -> {"oconnor", "o connor"}
	simplify("J. B.") -> {"jb", "j b"}
	simplify("John A. Doe") -> {"john a doe"} - only concatenate for single letter pairs?

	(tentative)
	For stored names, insert all results into BK-tree/Trie, but all with the same value field for later retrieval
	*/
	public static ArrayList<String> simplify(String s) {
		String norm = Normalizer.normalize(s, Normalizer.Form.NFD)
			.replaceAll("\\p{InCombiningDiacriticalMarks}+", ""); // removes diacritics, e.g. José -> Jose
		norm = norm.toLowerCase();
		String[] parts = norm.split("[^a-zA-Z]+"); // split string by delimiters i.e. primarily ' . -

		StringBuilder concat = new StringBuilder(); // mutable StringBuilder to save memory
		StringBuilder spaces = new StringBuilder();

		for (int i = 0; i < parts.length; i++) {
			if (parts.length == 0) continue;
			concat.append(parts[i]);
			spaces.append(parts[i]);

			if (i < parts.length - 1) spaces.append(" ");
		}

		ArrayList<String> results = new ArrayList<>();
		results.add(concat.toString());
		if (!concat.toString().equals(spaces.toString())) results.add(spaces.toString());

		return results;
	}

	public static String getUserInput() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    public static int getUserInt() {
        Scanner scanner = new Scanner(System.in);
        while (!scanner.hasNextInt()) { scanner.nextLine(); }
        return scanner.nextInt();
    }

	public static void main(String[] args) {
		Search tests = new Search(MetricFunctions.osa);

		System.out.println(Search.simplify("José"));
		System.out.println(Search.simplify("J. B."));
		// for (Person person : tests.p.peopleList) { // iterates through each person
		// 	//System.out.println(person.getFirstName());
		// 	tests.t.insert(person.getFirstName().toLowerCase(), person.getFullName()); // trie insertion
		// 	tests.bk.insert(person.getFirstName().toLowerCase(), person.getFullName()); // bk-tree insertion
		// }

		// while (true) {
		// 	System.out.print("Enter a name: ");
		// 	String a = getUserInput(); a = a.toLowerCase();
		// 	System.out.print("Show results within __ characters: ");
		// 	int d = getUserInt();

		// 	ArrayList<String> exactMatches = tests.t.traverseVals(tests.t.probe(a), a);
		// 	System.out.println("Exact prefix matches: ");
		// 	if (exactMatches.size() > 4) {
		// 		for (int i = 0; i < 3; i++) {
		// 			System.out.println(exactMatches.get(i));
		// 		}
		// 		System.out.print(exactMatches.size() - 3); System.out.println(" more results");
		// 	}
		// 	else {
		// 		for (String s : exactMatches) {
		// 			System.out.println(s);
		// 		}
		// 	}

		// 	ArrayList<String> fuzzyMatches = tests.bk.fuzzyVals(a, d, false);


		// 	// ArrayList<String> fuzzyMatches = tests.bk.searchData(a, d);
		// 	System.out.println("Fuzzy matches: ");
		// 	for (String s : fuzzyMatches) {
		// 		System.out.println(s);
		// 	}

		// 	// TO DO: 
		// }

		// ArrayList<String> print = tests.t.traverseData();
        // for (int i = 0; i < print.size(); i++) {
        //     System.out.println(print.get(i));
        // }
        // print = tests.bk.traverseVals();
        // for (int i = 0; i < print.size(); i++) {
        //     System.out.println(print.get(i));
        // }
	}
}