package bktree;

import java.util.*;

// this is our main file
public class Search {
	BKTree<String, String> bk; 	// bk tree!
	Trie<String, String> t;		// trie!
	People p = new People();	// automatically ingests people

	public Search(DistanceMetric<String> c) {
		bk = new BKTree<String, String>(c);
		t = new Trie<String, String>();
	}

	public ArrayList<String> getResults(String query, int maxDist, int priority) {
		return null;
	}

	public ArrayList<String> simplify(String s) {
		StringBuilder sb = new StringBuilder(s); // mutable StringBuilder to save memory
		return null;
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
		Search tests = new Search(MetricFunctions.Lev);

		for (Person person : tests.p.peopleList) { // iterates through each person
			//System.out.println(person.getFirstName());
			tests.t.insert(person.getFirstName().toLowerCase(), person.getFullName()); // trie insertion
			tests.bk.insert(person.getFirstName().toLowerCase(), person.getFullName()); // bk-tree insertion
		}

		while (true) {
			System.out.print("Enter a name: ");
			String a = getUserInput(); a = a.toLowerCase();
			System.out.print("Show results within __ characters: ");
			int d = getUserInt();

			ArrayList<String> exactMatches = tests.t.traverseData(tests.t.probe(a), a);
			System.out.println("Exact prefix matches: ");
			if (exactMatches.size() > 4) {
				for (int i = 0; i < 3; i++) {
					System.out.println(exactMatches.get(i));
				}
				System.out.print(exactMatches.size() - 3); System.out.println(" more results");
			}
			else {
				for (String s : exactMatches) {
					System.out.println(s);
				}
			}

			ArrayList<String> fuzzyMatches = tests.bk.searchData(a, d);
			System.out.println("Fuzzy matches: ");
			for (String s : fuzzyMatches) {
				System.out.println(s);
			}

			// TO DO: 
		}

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