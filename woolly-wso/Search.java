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

	BKTree<String, Integer> bkl;	// more stuff for demo
	Trie<String, Integer> tl;
	BKTree<String, Integer> bkht;
	Trie<String, Integer> tht;

	//MultiBKTree<Integer> mbk;
	// to do: integrate multibktree

	public Search(DistanceMetric<String> c) {
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


	/**
	 * MATCH FINDING
	 * 
	 * 
	 * 
	 * 
	 */
	public Set<Integer> findMatches(String input) {
		String[] fields = input.split(" ");
		System.out.println("FIELDS: " + fields);
		LinkedHashSet<Integer> matches = new LinkedHashSet<>();
		if (fields.length == 1) { // no spaces - give first names priority
			ArrayList<String> queries = simplify(fields[0]);

			Set<Integer> e = new LinkedHashSet<>(); // exact matches
			Set<Integer> f = new LinkedHashSet<>(); // fuzzy matches
			Set<Integer> ht_exact = new LinkedHashSet<>(); // hometown exact matches
			Set<Integer> ht_fuzzy = new LinkedHashSet<>(); // hometown fuzzy matches

			for (String s : queries) { // exact first name matches
				e.addAll(t.traverseVals(t.probe(s), s));
			}
			for (String s : queries) { // exact last name matches
				e.addAll(tl.traverseVals(tl.probe(s), s));
			}
			for (String s : queries) { // fuzzy first name matches
				f.addAll(bk.fuzzyVals(s, Math.min(2, s.length() / 3), false, true));
			}
			for (String s : queries) { // fuzzy last name matches
				f.addAll(bkl.fuzzyVals(s, Math.min(2, s.length() / 3), false, true));
			}

			matches.addAll(e); matches.addAll(f);


			
			for (String s : queries) {
				ht_exact.addAll(tht.traverseVals(tht.probe(s), s));
				ht_fuzzy.addAll(bkht.fuzzyVals(s, Math.min(2, s.length() / 3), false, true));
			}

			if (((ht_exact.size() * 5)) > matches.size()) { // weighting home town exact at 5 times and then ht fuzzy at .2 times
				matches.clear();
				matches.addAll(ht_exact);
				matches.addAll(ht_fuzzy);
			}
			
		}
		else if (fields.length == 2) { // first name + last name
			ArrayList<String> full = simplify(input);
			ArrayList<String> queries1 = simplify(fields[0]); // assume first name
			ArrayList<String> queries2 = simplify(fields[1]); // assume last name

			Set<Integer> ef = new LinkedHashSet<>(); // exact first name matches
			Set<Integer> el = new LinkedHashSet<>(); // exact last name matches
			Set<Integer> ff = new LinkedHashSet<>(); // fuzzy first name matches
			Set<Integer> fl = new LinkedHashSet<>(); // fuzzy last name matches

			Set<Integer> ht_exact = new LinkedHashSet<>(); // hometown exact matches
			Set<Integer> ht_fuzzy = new LinkedHashSet<>(); // hometown fuzzy matches

			for (String s : queries1) {
				ef.addAll(t.traverseVals(t.probe(s), s));
				ff.addAll(bk.fuzzyVals(s, Math.min(2, s.length() / 3), false, true));
			}
			for (String s : queries2) {
				el.addAll(tl.traverseVals(tl.probe(s), s));
				fl.addAll(bkl.fuzzyVals(s, Math.min(2, s.length() / 3), false, true));
			}
			Set<Integer> a = new LinkedHashSet<>(ef); a.retainAll(el); // exact first + last  
			Set<Integer> b = new LinkedHashSet<>(ef); b.retainAll(fl); // exact first + fuzzy last
			Set<Integer> c = new LinkedHashSet<>(ff); c.retainAll(el); // fuzzy first + exact last
			Set<Integer> d = new LinkedHashSet<>(ff); d.retainAll(fl); // fuzzy first + last
			int as = a.size(), bs = b.size(), cs = c.size(), ds = d.size();

			matches.addAll(a); matches.addAll(b); matches.addAll(c); matches.addAll(d);



			for (String s : full) {
				ht_exact.addAll(tht.traverseVals(tht.probe(s), s));
				ht_fuzzy.addAll(bkht.fuzzyVals(s, Math.min(2, s.length() / 3), false, true));
			}


			if (((ht_exact.size() * 5) + (ht_fuzzy.size() * .2)) > matches.size()) {
				matches.clear();
				matches.addAll(ht_exact);
				matches.addAll(ht_fuzzy);
			}
			
		}
		return matches;
	}

}


