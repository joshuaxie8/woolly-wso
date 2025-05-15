package bktree;

import java.util.*;
import java.text.Normalizer;

// this is our main file
public class Search {
	People p = new People();	// automatically ingests people
	BKTree<String, Integer> bk; 	// bk tree!
	Trie<String, Integer> t;		// trie!
	HashMap<Integer, Person> idToPerson = new HashMap<>(8192);
	HashMap<Person, Integer> personToId = new HashMap<>(8192);
	int id = 0;

	public Search(DistanceMetric<String> c) {
		bk = new BKTree<String, Integer>(c);
		t = new Trie<String, Integer>();

		// initialization
		for (Person person : p.peopleList) {
			idToPerson.put(id, person);
			personToId.put(person, id);

			ArrayList<String> ar = simplify(person.getFirstName());
			for (String s : ar) {
				t.insert(s, id);
				bk.insert(s, id);
			}
			id++;
		}
	}

	/**
	 * STRING SIMPLIFICATION - FOR BOTH USER INPUT AND STORED VALUES
	 * Given a String s, returns an array list of normalized, "reasonable" parsings of s
	 * ex.
	 * simplify("Waage-Pickle") -> {"waagepickle", "waage pickle"}
	 * simplify("José") -> {"jose"}
	 * simplify("O'Connor") -> {"oconnor", "o connor"}
	 * simplify("J. B.") -> {"jb", "j b"}
	 * simplify("John A. Doe") -> {"john a doe"} - only concatenate for single letter pairs?
	 * 
	 * For stored names, insert all results into BK-tree/Trie, but all with the same value field for later retrieval?
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

		while (true) {
			System.out.print("Enter a name: ");
			String a = getUserInput(); a = a.toLowerCase();
			System.out.print("Show results within __ characters: ");
			int d = getUserInt();

			ArrayList<Integer> exactMatches = tests.t.traverseVals(tests.t.probe(a), a);
			System.out.println("Exact prefix matches: ");
			if (exactMatches.size() > 4) {
				for (int i = 0; i < 3; i++) {
					System.out.println(tests.idToPerson.get(i).getFullName());
				}
				System.out.print(exactMatches.size() - 3); System.out.println(" more results");
			}
			else {
				for (int i : exactMatches) {
					System.out.println(tests.idToPerson.get(i).getFullName());
				}
			}

			ArrayList<Integer> fuzzyMatches = tests.bk.fuzzyVals(a, d, false);

			// ArrayList<String> fuzzyMatches = tests.bk.searchData(a, d);
			System.out.println("Fuzzy matches: ");
			for (int i : fuzzyMatches) {
				System.out.println(tests.idToPerson.get(i).getFullName());
			}
		}
	}
}