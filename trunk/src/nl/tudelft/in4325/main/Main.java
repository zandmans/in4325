/**
 * This class contains the main functions to make the search-engine work
 * 
 * @version 0.1
 */

package nl.tudelft.in4325.main;

import java.io.FileNotFoundException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;

import nl.tudelft.in4325.index.Indexer;
import nl.tudelft.in4325.index.Search;
import nl.tudelft.in4325.index.Soundex;
import nl.tudelft.in4325.index.BTree;

public class Main 
{
	/* variable declarations */
	public static Hashtable<String, Hashtable<Integer,Integer>> index;
	public static Hashtable<Integer, String> docs;
	public static Hashtable<String, ArrayList<String>> sIndex;

	/**
	 * Java main function
	 * Makes it possible to input search queries and to perform a specific search method on it
	 * 
	 * @param args
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException
	{		
		/* create a new scanner for the input from the console */
		Scanner sc = new Scanner(System.in);

		/* create a new Indexer object */
		Indexer indexer = new Indexer();

		/* create a new index */
		indexer.init();
		index = indexer.getIndex();
		docs = indexer.getDocs();

		/* create a new soundex index based on the current index */
		Soundex soundex = new Soundex();
		sIndex = soundex.convertIndex();

		/* notify the user about the number of words indexed */
		System.out.println("Index build successfully, number of words indexed: " + index.size());
		System.out.println("Soundex index build successfully, number of words indexed: " + sIndex.size());

		/* create an 'infinite' loop for the input, break it when the input is q */
		while(true)
		{
			/* create the possibility to input a query */
			System.out.println("\nQuery: ");
			String query = sc.nextLine();

			/* if the query is 'q', exit the program */
			if(query.equals("q")) System.exit(0);


			if(query.contains("*")) {
				/* perform b-tree search using wild cards */
				processBTreeSearchResult(query);
			}
			else if(query.charAt(0) == '$') {
				/* perform a boolean search/optimized cosine ranking on the query */
				Search.booleanSearch(query, 1);
			}else {
				/* perform a boolean search/cosine ranking on the query */
				Search.booleanSearch(query, 0);
			}
			
		}
	}

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
	public static void processBTreeSearchResult(String s) {
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
			if(resultList != null) {
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

			if(resultList != null) {
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
				
				/* reverse words in resultB and at to resultList */
				if(resultB != null) {
					for(String i : resultB) {						
						resultList.add(revBTree.reverseWord(i));
					}
				} else {
					noResults();
				}
				
				/* Compute the intersection of both resultList and resultA */
				resultList.retainAll(resultA);
				
				/* Print out results */
				for (String i : resultList) {
					System.out.println("[" + i  + "] in " + getDocuments(i));
				}
				
			} else {
				System.out.println("Coming soon?? Maybe....");
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
	private static List<String> manageTrailingQueries(String s, BTree bTree) {
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
	private static List<String> manageLeadingQueries(String s, BTree revBTree) {
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
	private static void noResults() {
		System.out.println("No results were found for your wildcard query");
	}

	/**
	 * Gets documents corresponding to given keyword
	 * @param keyword
	 * @return
	 */
	public static String getDocuments(String keyword) {
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
			output = "No matching results found in the index !";
		
		return output;
	}
	
	/**
	 * Normalize the given string, 
	 * the new format is lowercase and contains only alphabetical letters and numbers
	 * 
	 * @author Thijs Zandvliet
	 * @param str
	 * @return string
	 */
	public static String formatString(String str)
	{
		/* try to normalize the string */
		try
		{
			/* normalize the string, make it lowercase and remove all special characters */
			return Normalizer.normalize(str, Normalizer.Form.NFC)
			.toLowerCase()
			.replaceAll("[^a-zA-Z 0-9]+","");
		}

		/* if the function fails,  notify the user and return null */
		catch(Exception ex)
		{
			/* print the error and return null */
			System.err.println(ex);
			return null;
		}
	}

	/**
	 * Test function
	 * Print all the elements contained in the index
	 * 
	 * @author Thijs Zandvliet
	 */
	public static void testIndex()
	{
		Enumeration<String> e = sIndex.keys();
		while(e.hasMoreElements())
			System.out.println(e.nextElement());
	}

}
