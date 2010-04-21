package nl.tudelft.in4325.index;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import nl.tudelft.in4325.main.Main;

/**
 * 
 * @author Pierre Lopez Barbosa
 * 
 * Class to process wildcard queries
 *
 */

public class BTreeProcessor {
	/**
	 * BTree wildcard * implementation:
	 * 
	 * This method distinguishes 3 cases:
	 * 1. Search term ending with wildcard *
	 * 2. Search term starting with wildcard *
	 * 3. Search term including wildcard * inside the word
	 * 
	 * Note: permuterm index is not handled in this assignment
	 * 
	 * @param s String to use for wildcard search
	 * @author Pierre Lopez Barbosa
	 */
	public void processBTreeSearchResult(String s) {
		/* Converts all of the characters in this String to lower case 
		 * and removes characters except a-z, A-Z, 0-9, white space and the * sign
		 */
		s = Normalizer.normalize(s, Normalizer.Form.NFC).toLowerCase().replaceAll("[^a-zA-Z 0-9*]+","");
		
		/* List for holding results for all 3 cases */
		List<String> resultList = new ArrayList<String>();
		/* Create a new BTree object */
		BTree bTree = new BTree('n');
		/* Create a new reversed BTree object */
		BTree revBTree = new BTree('r');
		/* 
		 * Three cases:
		 * 1: String ends with *: i.e. 'mon*' means look up words that start with mon 
		 */
		if (s.endsWith("*")) {
			resultList = manageTrailingQueries(s, bTree);
			if(resultList.size() != 0) {
				for(String i : resultList) {
					System.out.println("[" + i + "] in " + getDocuments(i));
				}
			} else {
				noResults();
			}
		}

		/* 
		 * 2: String starts with *: i.e. '*mon' means look up words that end with mon
		 * use reverse B-tree
		 * reverse word than call insert method
		 */
		else if(s.startsWith("*")) {
			resultList = manageLeadingQueries(s, revBTree);

			if(resultList.size() != 0) {
				for(String i : resultList) {
					/* reverse retrieved words back */
					String j = revBTree.reverseWord(i);
					System.out.println("[" + j + "] in " + getDocuments(j));
				}
			} else {
				noResults();
			}
		}

		/* 3: String contains a * inside the word */
		else { /* Both normal and reverse subtrees */
			List<String> resultA = new ArrayList<String>();
			List<String> resultB = new ArrayList<String>();
			
			/* Split word */
			String[] words = s.trim().split("\\*");
			
			if(words.length == 2) {
				/* first part do straight btree search */
				resultA = manageTrailingQueries(words[0]+"*", bTree);

				/* second part do reverse btree search */
				resultB = manageLeadingQueries("*"+words[1], revBTree);

				/* If one of both lists is empty no results have been found */
				if(resultA.size() == 0 || resultB.size() == 0) {
					noResults();
				}
				else {
					/* reverse words in resultB and add to resultList */
					for(String i : resultB) {						
						resultList.add(revBTree.reverseWord(i));
					}
					
					/* Compute the intersection of both resultList and resultA */
					resultList.retainAll(resultA);
					
					/* If intersection is empty: no results were found */
					if(resultList.size() == 0) {
						noResults();
					}
					
					/* Print out results */
					for (String i : resultList) {
						System.out.println("[" + i  + "] in " + getDocuments(i));
					}
				}
			} else {
				System.out.println("Multiple wildcard terms not implemented....");
			}
		}
	}

	/**
	 * Returns all found words in BTree for the search term
	 * @param s
	 * @param bTree
	 * @return
	 * @author Pierre Lopez Barbosa
	 */
	private List<String> manageTrailingQueries(String s, BTree bTree) {
		List<String> resultList;
		
		/* Get result list  */
		resultList = (bTree.search(s.substring(0,s.length()-1)));
		return resultList;
	}
	
	/**
	 * Returns all found words in the reversed BTree and reverses 
	 * the found words back to the original readable way
	 * @param s
	 * @param revBTree
	 * @return
	 * @author Pierre Lopez Barbosa
	 */
	private List<String> manageLeadingQueries(String s, BTree revBTree) {
		List<String> resultList;
		
		/* Get result list */
		resultList = revBTree.search(revBTree.reverseWord(s.substring(1,s.length())));
		return resultList;
	}

	/**
	 * Prints no results message
	 * 
	 * @author Pierre Lopez Barbosa
	 */
	private void noResults() {
		System.out.println("\nNo results were found for the wildcard query");
	}

	/**
	 * Gets documents corresponding to given keyword
	 * @param keyword
	 * @return
	 */
	public String getDocuments(String keyword) {
		/* create a new ArrayList for the results */
		ArrayList<Hashtable<Integer,Integer>> results = new ArrayList<Hashtable<Integer,Integer>>();
		Hashtable<Integer,Integer> result = Main.index.get(keyword);
		results.add(result);
		/* convert the docId's back to the document names */
		String output = " | ";
		
		if(results != null && results.size() > 0 && results.get(0) != null && results.get(0).size() > 0)
			for(Integer docId : results.get(0).keySet())
				output += Main.docs.get(docId) + " | ";
		else
			output = "\nNo matching documents found in the index!";
		
		return output;
	}
}
