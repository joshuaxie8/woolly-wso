package woollywso;

import java.util.*;
import java.text.Normalizer;

// this is our main file
public class Search {
	People p;	// automatically ingests people

	HashMap<Integer, Person> idToPerson = new HashMap<>(8192);
	HashMap<Person, Integer> personToId = new HashMap<>(8192);
	int id = 0;

	// Maybe we could create an array of tries/BK-trees for more elegant looping
	BKTree<String, Integer> bk; 	// bk tree for first names
	Trie<String, Integer> t;		// trie for first names
	BKTree<String, Integer> bkl; 	// bk tree for last names
	Trie<String, Integer> tl;		// trie for last names
	BKTree<String, Integer> bkht;	// bk tree for hometowns
	Trie<String, Integer> tht;		// trie for hometowns

	// We could also try to integrate MultiBKTrees in the future, but it is by no means a priority

	public Search(DistanceMetric<String> c, String filePath) {
		p = new People(filePath);
		bk = new BKTree<String, Integer>(c);
		t = new Trie<String, Integer>();

		bkl = new BKTree<String, Integer>(c);
		tl = new Trie<String, Integer>();
		bkht = new BKTree<String, Integer>(c);
		tht = new Trie<String, Integer>();

		// initialization
		for (Person person : p.peopleList) {
			idToPerson.put(id, person);
			personToId.put(person, id);

			ArrayList<String> f = simplify(person.getFirstName());
			ArrayList<String> l = simplify(person.getLastName());
			ArrayList<String> ht = simplify(person.getHomeTown());
			//keys[0] = ar.get(0);
			for (String s : f) {
				t.insert(s, id);
				bk.insert(s, id);
			}
			for (String s : l) {
				tl.insert(s, id);
				bkl.insert(s, id);
			}
			for (String s : ht) {
				tht.insert(s, id);
				bkht.insert(s, id);
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
	 * We insert all results into our BK-tree/Tries, but pointing to the same value field for information retrieval purposes
	 */
	public static ArrayList<String> simplify(String s) {
		String norm = Normalizer.normalize(s, Normalizer.Form.NFD)
			.replaceAll("\\p{InCombiningDiacriticalMarks}+", ""); // removes diacritics, e.g. José -> Jose
		norm = norm.toLowerCase();
		String[] parts = norm.split("[^a-zA-Z]+"); // split string by delimiters i.e. primarily ' . -

		StringBuilder concat = new StringBuilder(); // mutable StringBuilder to save memory
		StringBuilder spaces = new StringBuilder();

		boolean noConcat = false;

		for (int i = 0; i < parts.length; i++) {
			if (parts.length == 0) continue;

			if (i > 0 && parts[i - 1].length() > 2 && parts[i].length() > 2) {
				noConcat = true;
			}

			concat.append(parts[i]);
			spaces.append(parts[i]);

			if (i < parts.length - 1) spaces.append(" ");
		}

		ArrayList<String> results = new ArrayList<>();
		results.add(spaces.toString());
		if (!noConcat && !concat.toString().equals(spaces.toString())) results.add(concat.toString());

		return results;
	}


	/**
	 * MATCH FINDING
	 * 
	 * 
	 * 
	 * 
	 */
	public Set<Integer> findMatches(String input) {
		String[] fields = input.split(" ");

		LinkedHashSet<Integer> matches = new LinkedHashSet<>();

		if (fields.length == 0) {
			// do nothing

		} else if (fields.length == 1) { // no spaces - give first names priority
			ArrayList<String> queries = simplify(fields[0]); // parse search queries

			/** GUIDE TO INDICES
			 * [0]: exact first name matches (excl prefix matches)
			 * [1]: exact first name matches (incl prefix matches)
			 * [2]: fuzzy first name matches
			 * [3]: exact last name matches (excl prefix matches)
			 * [4]: exact last name matches (incl prefix matches)
			 * [5]: fuzzy last name matches
			 * [6]: exact hometown matches (excl prefix matches)
			 * [7]: exact hometown matches (incl prefix matches)
			 * [8]: fuzzy hometown matches
			 */
			int numMeasures = 9;
			int[] cnts = new int[numMeasures]; // keeps track of the amount of each type of result

			@SuppressWarnings("unchecked")
			Set<Integer>[] results = (Set<Integer>[]) new LinkedHashSet[numMeasures];
			for (int i = 0; i < numMeasures; i++) { results[i] = new LinkedHashSet<>(); } // initializing linked hash sets

			for (int i = 0; i < numMeasures; i++) {
				for (String s : queries) {
					switch (i) {
					// EXACT MATCHES (USED FOR WEIGHTING)
					case 0:
						results[i].addAll(t.getValues(t.probe(s)));
						break;
					case 3:
						results[i].addAll(tl.getValues(tl.probe(s)));
						break;
					case 6:
						results[i].addAll(tht.getValues(tht.probe(s)));
						break;
					// EXACT PREFIX MATCHES (INCLUDES EXACT MATCHES)
					case 1:
						results[i].addAll(t.traverseVals(t.probe(s), s));
						break;
					case 4:
						results[i].addAll(tl.traverseVals(tl.probe(s), s));
						break;
					case 7:
						results[i].addAll(tht.traverseVals(tht.probe(s), s));
						break;
					// FUZZY MATCHES (EXCLUDES EXACT MATCHES)
					case 2:
						results[i].addAll(bk.fuzzyVals(s, Math.min(2, s.length() / 3), false, true));
						break;
					case 5:
						results[i].addAll(bkl.fuzzyVals(s, Math.min(2, s.length() / 3), false, true));
						break;
					case 8:
						results[i].addAll(bkht.fuzzyVals(s, Math.min(2, s.length() / 3), false, true));
					}
				}
				cnts[i] = results[i].size();
			}

			/*
				We weight name matches twice as heavily as hometown matches
				We weight exact matches 8x as heavily and prefix matches 3x as heavily as fuzzy matches

				We could further improve this by de-weighting looser matches (i.e. matches with higher OSA distances)
			*/

			if ((cnts[0]*5 + cnts[1]*3 + cnts[2] + cnts[3]*5 + cnts[4]*3 + cnts[5])*2 > // names
				cnts[6]*5 + cnts[7]*3 + cnts[8]) {											// hometowns
				matches.addAll(results[1]); // prefix first name
				matches.addAll(results[4]); // prefix last name
				matches.addAll(results[2]); // fuzzy first name
				matches.addAll(results[5]); // fuzzy last name
			}
			else {
				matches.addAll(results[7]); // prefix hometown
				matches.addAll(results[8]); // fuzzy hometown
			}
		}

		/*
			* Currently, this program is only implemented for two fields (meaning that after two spaces the rest of the input is ignored)
			* We were working on expanding it fully but we don't have enough time to complete it before the project deadline :(
		*/

		// When the user inputs multiple fields, we assume that the first field is a first name and the second field is a last name
		// OR the entire query is a single first or last name
		// OR the entire query is the name of a hometown
		// If we had more time we would've liked to implement more flexible searching, i.e. "Joshua Shanghai"
		else {
			ArrayList<String> full = simplify(input);
			ArrayList<String> queries1 = simplify(fields[0]); 
			ArrayList<String> queries2 = simplify(fields[1]);

			int numMeasures = 15;
			int constructedCounts = 6;
			int[] cnts = new int[numMeasures + constructedCounts]; // keeps track of the amount of each type of result

			@SuppressWarnings("unchecked")
			Set<Integer>[] results = (Set<Integer>[]) new LinkedHashSet[numMeasures];

			for (int i = 0; i < numMeasures; i++) { results[i] = new LinkedHashSet<>(); } // initializing linked hash sets

			/** GUIDE TO INDICES
			 * [0]: exact first name matches (excl prefix matches, both fields)
			 * [1]: exact first name matches (incl prefix matches)
			 * [2]: fuzzy first name matches
			 * [3]: exact last name matches (excl prefix matches)
			 * [4]: exact last name matches (incl prefix matches)
			 * [5]: fuzzy last name matches
			 * 
			 * [6]: exact hometown matches (excl prefix matches)
			 * [7]: exact hometown matches (incl prefix matches)
			 * [8]: fuzzy hometown matches
			 * 
			 * [9]: exact first name matches (excl prefix matches, first field only)
			 * [10]: exact first name matches (incl prefix matches, first field only)
			 * [11]: fuzzy first name matches (first field only)
			 * [12]: exact last name matches (excl prefix matches, second field only)
			 * [13]: exact last name matches (incl prefix matches, second field only)
			 * [14]: fuzzy last name matches (second field only)
			 * 
			 * CONSTRUCTED COUNTS:
			 * [15]: # of matches with exact first (excl prefix) & exact last (incl prefix)
			 * [16]: # of matches with exact first (excl prefix) & fuzzy last
			 * [17]: # of matches with fuzzy first & exact last (incl prefix)
			 * [18]: # of matches with fuzzy first & fuzzy last
			 * 
			 * [19]: # of matches with exact first (excl prefix) & exact last (excl prefix)
			 * [20]: # of matches with fuzzy first & exact last (excl prefix)
			 */

			for (int i = 0; i < numMeasures; i++) {
				for (String s : full) { // treating entire query as one field
					switch (i) {
					// EXACT MATCHES (USED FOR WEIGHTING)
					case 0:
						results[i].addAll(t.getValues(t.probe(s)));
						break;
					case 3:
						results[i].addAll(tl.getValues(tl.probe(s)));
						break;
					case 6:
						results[i].addAll(tht.getValues(tht.probe(s)));
						break;
					// EXACT PREFIX MATCHES (INCLUDES EXACT MATCHES)
					case 1:
						results[i].addAll(t.traverseVals(t.probe(s), s));
						break;
					case 4:
						results[i].addAll(tl.traverseVals(tl.probe(s), s));
						break;
					case 7:
						results[i].addAll(tht.traverseVals(tht.probe(s), s));
						break;
					// FUZZY MATCHES (EXCLUDES EXACT MATCHES)
					case 2:
						results[i].addAll(bk.fuzzyVals(s, Math.min(2, s.length() / 3), false, true));
						break;
					case 5:
						results[i].addAll(bkl.fuzzyVals(s, Math.min(2, s.length() / 3), false, true));
						break;
					case 8:
						results[i].addAll(bkht.fuzzyVals(s, Math.min(2, s.length() / 3), false, true));
						break;
					}
				}
				for (String s : queries1) { // first name only
					switch (i) {
					case 9:
						results[i].addAll(t.getValues(t.probe(s)));
						break;
					case 10:
						results[i].addAll(t.traverseVals(t.probe(s), s));
						break;
					case 11:
						results[i].addAll(bk.fuzzyVals(s, Math.min(2, s.length() / 3), false, true));
						break;
					}
				}
				for (String s : queries2) { // last name only
					switch (i) {
					case 12:
						results[i].addAll(tl.getValues(tl.probe(s)));
						break;
					case 13:
						results[i].addAll(tl.traverseVals(tl.probe(s), s));
						break;
					case 14:
						results[i].addAll(bkl.fuzzyVals(s, Math.min(2, s.length() / 3), false, true));
						break;
					}
				}
				cnts[i] = results[i].size();
			}

			Set<Integer> efpl = new LinkedHashSet<>(results[9]); efpl.retainAll(results[13]); // exact first + prefix last
			Set<Integer> effl = new LinkedHashSet<>(results[9]); effl.retainAll(results[14]); // exact first + fuzzy last
			Set<Integer> ffpl = new LinkedHashSet<>(results[11]); ffpl.retainAll(results[13]); // fuzzy first + prefix last
			Set<Integer> fffl = new LinkedHashSet<>(results[11]); fffl.retainAll(results[14]); // fuzzy first + fuzzy last
			cnts[15] = efpl.size();
			cnts[16] = effl.size();
			cnts[17] = ffpl.size();
			cnts[18] = fffl.size();

			Set<Integer> efel = new LinkedHashSet<>(efpl); efel.retainAll(results[12]);
			Set<Integer> ffel = new LinkedHashSet<>(ffpl); ffel.retainAll(results[12]);
			cnts[19] = efel.size(); // subset of efpl
			cnts[20] = ffel.size(); // subset of ffpl

			/*
				We weight name matches twice as heavily as hometown matches
				We weight exact matches 8x as heavily and prefix matches 3x as heavily as fuzzy matches

				This means an exact first & last name match is weighted 64x as heavily as a fuzzy match
				An exact first name & exact last name prefix match is weighted 24x as heavily as a fuzzy match
				etc.
				This does complicate hometown weightings - we chose values that seemed to respond well experimentally
			*/

			if ((cnts[0]*5 + cnts[1]*3 + cnts[2] + cnts[3]*5 + cnts[4]*3 + cnts[5])*2 + // single field names
				cnts[15]*24 + cnts[16]*8 + cnts[17]*3 + cnts[18] + cnts[19]*40 + cnts[20]*5 >
				cnts[6]*32 + cnts[7]*3 + cnts[8]) {
				// exact first name
				matches.addAll(results[1]);
				matches.addAll(efpl);
				matches.addAll(effl);
				// exact last name
				matches.addAll(results[4]);
				matches.addAll(ffpl);
				// fuzzy matches
				matches.addAll(fffl);
				matches.addAll(results[2]);
				matches.addAll(results[5]);

			}
			else {
				matches.addAll(results[7]); // prefix hometown
				matches.addAll(results[8]); // fuzzy hometown
			}
		}
		return matches;
	}
}


