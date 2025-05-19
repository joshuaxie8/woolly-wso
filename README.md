# Woolly WSO

Using BK-trees for a tidier, more flexible Williams College online campus directory. Final project for CSCI 136 at Williams College, taught by Katie Keith in Spring 2025.
## Overview

Williams Students Online (WSO)'s [Facebook](https://wso.williams.edu/facebook/help) (no relation to Mark Zuckerberg's Facebook, or Meta) is currently the primary campus directory used by members of the campus community to look up information.

We store information using a combination of BK-trees and modified tries, enabling fast retrieval in approximate logarithmic time. By using BK-trees, we are able to find and display fuzzy matches, which the current WSO Facebook does not support. A woollier WSO, if you will.
## Usage

Upon running the program, the user will be prompted by a window with a search bar, mimicking the current version of the WSO Facebook. From there, they are able to search for people currently at Williams College by their name and hometown. Results are shown based on their similarity to the search query, with exact matches on top.

Currently, the search is somewhat rudimentary, with support for up to two space-separated fields. We hope to further develop this logic, enabling multiple search fields within the same line, more filter types (e.g. country, major, class year) as well as implementing the search labels supported by the current WSO Facebook.
## Implementation

Our implementation utilizes our `Person.java` class, which stores information about an individual, an instance of a `People.java` class which ingests data from a .csv file into an arrayList of Person instances, and several pairs of tries and BK-trees working in conjunction. Each trie-BK-tree pair is configured to store one specific piece of information, such as first name, last name, or hometown, with each terminal node in a trie and each node in a BK-tree pointing back to the corresponding index in our arrayList.

During data ingestion, data fields are parsed and normalized, occasionally entailing a repeated insertion with different spacings (for instance, someone with the first name 'Tommy-Lee' would be inserted as both 'tommy lee' and 'tommylee'). The user's search queries undergo a similar process but in reverse, whereupon the program traverses through its tries and BK-trees, searching for both exact and fuzzy matches. Due to the speed advantage of using pre-processed, read-only tries and BK-trees, the program is able to respond to changes in the user's input in real time with almost no delay.
### Tree ADT, Tries, and BK-Trees

Both our `BKTree.java` and `Trie.java` classes implement a Tree abstract data type, which defines isEmpty(), size(), contains(), and insert() methods. Since BK-trees and tries differ significantly in terms of structure and traversal, `Tree.java` has been kept relatively bare.

We implemented a modified trie class that allows for the insertion of multiple identical keys, which accounts for cases where multiple people share the same data field, such as a shared first name or hometown. This is done by defining a List of values at each node—if one were to insert a key-value pair in a trie where that key already exists, the value is simply added to the end of the list at that node.Tries allow us to find the root node of the subtree of prefix matches in O(n) time, where n is the length of the prefix (practically, this usually corresponds to the length of one field in the user's search query, which typically contains no more than 10-15 characters). Once this root node is reached, the list of values contained in every subsequent node traversed gives the incides in our People instance of those who match the user's search.

BK-trees, short for Burkhard-Keller trees, are trees wherein the *k*-th subtree of any node consists of all elements that are a distance *k* from that node. Any node *b* stored in the *i*-th subtree of some parent node *a* satisfies a given distance function where d(a,b) = *i*. No two children of the same parent node are the same distance from their parent.

![example BK-tree](https://github.com/joshuaxie8/cs136-jx4-aa37-final/blob/main/figs/bktree-0.png?raw=true)

To insert a node, we repeatedly calculate distances between keys and navigate into the corresponding subtree, starting from the root node, until we reach an unoccupied index. Here, for example, we insert a node with the key 'boot':

![BK-tree node insertion](https://github.com/joshuaxie8/cs136-jx4-aa37-final/blob/main/figs/bktree-1.png?raw=true)

### Distance Metrics

We chose optimal string alignment (OSA) distances as our distance metric. The OSA distance between two strings is the minimum amount of operations required to change one string into the other. An operation is defined as the insertion, deletion, or substitution of a character, or the transposition of a pair of adjacent characters. Importantly, OSA distance calculations assume that no character is modified more than once, which distinguishes it from the Damerau-Levenshtein distance metric.

An OSA distance calculation on strings of length m and n can be performed in O(m*n) time.

![The OSA distance of 'occlude' and 'conclude' is 2.](https://github.com/joshuaxie8/cs136-jx4-aa37-final/blob/main/figs/osa.png?raw=true)

Our `MetricFunctions.java` file also includes an (unused) Levenshtein distance implementation, which is identical to OSA but does not account for character transpositions. [There exists an algorithm](https://dl.acm.org/doi/10.1145/316542.316550) developed by Eugene Myers that performs Levenshtein distance calculations in O(n) time, but the optimization is barely noticeable for our purposes and requires the use of what is, in our opinion, a worse string metric.

### Search Priority and Fuzzy Matching

BK-trees enable fast fuzzy searches—when the user makes a search query, we want to return not just exact results, but also any results that are "close enough" to the query. For example, if the user types 'jon' in the search bar, we want the program to return both 'Jon Smith' and 'John Doe', if they exist in the directory.

If we are currently at some BK-tree node with key *a* and are only interested in nodes within some tolerance distance *t* from our query *q*, we only need to look through subtrees with indices in the range *d*(*a*, *q*) ± *t*, where d is our distance metric. This allows us to quickly prune branches that do not contain results matching the query.

The user's search fields are first fed through tries to find prefix matches before fuzzy searches are performed in BK-trees. By storing results in a linked hash set, we guarantee that exact matches are displayed first and also that there will be no repeat results. Finally, we compare the amount of results for each type of data (currently just name and hometown), with each result weighted by both priority (i.e. names are prioritized over hometowns) and whether it is exact or fuzzy, to determine which group of results to display to the user. For example, if the user were to search for 'Sydney', and there are two people named Sydney and two other people from Sydney, they will see two results of people named Sydney rather than the two people from Sydney; however, if there were 100 people from Sydney, the user will see those 100 results instead. We think that displaying all results regardless of field (e.g. showing both the Sydneys and the Sydneysiders in the search results), which is what WSO's Facebook currently does, is unnecessary and complicates the user experience, since those matches are often not initially visible or obvious.
## Installation

To install this program, first download and extract the `.zip` or `.tar.gz` file from the releases tab. Then, navigate to the directory containing this `README.md` on your terminal. Then, type the following commands in your terminal to compile and run the program:
```
mkdir bin
javac -d bin woolly-wso/*.java
java -cp bin woollywso/Graphics
```

For development and on-campus demos, we used a `data.csv` file containing access-controlled institutional information about real people at Williams College. In the interest of keeping this data protected, we have replaced the public facing version with a dummy file, generated using [Mockaroo](https://www.mockaroo.com/). 
## Authors

- Joshua Xie [@joshuaxie8](https://www.github.com/joshuaxie8)
- Aaron Anidjar [@koalascode](https://www.github.com/koalascode)