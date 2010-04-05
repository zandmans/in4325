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
import java.util.Scanner;

import nl.tudelft.in4325.index.Indexer;
import nl.tudelft.in4325.index.Search;
import nl.tudelft.in4325.index.Soundex;

public class Main
{

	/* variable declarations */
	public static Hashtable<String, ArrayList<Integer>> index;
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
		System.out.println("Soundex index build successfully, number of words indexed: " + index.size());
		
		/* create an 'infinite' loop for the input, break it when the input is q */
		while(true)
		{
			/* create the possibility to input a query */
			System.out.println("Query: ");
			String query = sc.nextLine();
			
			/* if the query is 'q', exit the program */
			if(query.equals("q")) System.exit(0);
			
			/* perform a boolean search on the query */
			Search.booleanSearch(query);
		}
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
	 * Testfunction
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
