/**
 * This class contains all the different search methods
 * 
 * @version 0.1
 */

package nl.tudelft.in4325.index;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

import nl.tudelft.in4325.main.Main;

public class Search {

	/* variable declarations */
	private static ArrayList<ArrayList<Integer>> results;
	
	/**
	 * Performs a boolean search on the given query
	 * 
	 * @author Thijs Zandvliet
	 * @param query
	 */
	public static void booleanSearch(String query)
	{
		/* create a new scanner which reads the query one word at the time */
		Scanner sc = new Scanner(query);
		
		/* create a new ArrayList for the results */
		results = new ArrayList<ArrayList<Integer>>();
		
		/* as long as the scanner reads new words... */
		while(sc.hasNext())
		{
			/* normalize the next word and save it into the string keyword */
			String keyword = Main.formatString(sc.next());
			
			/* if the ArrayList results already contains elements... */
			if(results.size() > 0)
			{
				/* if one of the boolean terms is used and there's a next word to read... */
				if((keyword.equals("or") || keyword.equals("and") || keyword.equals("not")) && sc.hasNext())
				{
					/* save the next word */
					String nextWord = sc.next();
					
					/* print the suggestions by using soundex */
					printSuggestions(nextWord);
					
					/* search for the next word in the Hashtable and add it to the results */
					ArrayList<Integer> result = Main.index.get(nextWord);
					results.add(result);
				}
				else
				{
					/* print the suggestions by using soundex */
					printSuggestions(keyword);
					
					/* search for the keyword in the Hashtable and add it to the results */
					ArrayList<Integer> result = Main.index.get(keyword);
					results.add(result);
				}
				
				/* receive the first two ArrayLists */
				ArrayList<Integer> r1 = results.get(0);
				ArrayList<Integer> r2 = results.get(1);
				
				/* find the right function for the right term */
				if(keyword.equals("or")) booleanOR(r1, r2);
				else if(keyword.equals("and")) booleanAND(r1, r2);
				else if(keyword.equals("not")) booleanNOT(r1, r2);
				else booleanOR(r1, r2);
			}
			
			/* if the ArrayList results is empty, search for the keyword in the Hashtable and add it to the results */
			else
			{
				/* print the suggestions by using soundex */
				printSuggestions(keyword);
				
				ArrayList<Integer> result = Main.index.get(keyword);
				results.add(result);
			}
		}
		
		/* convert the docId's back to the document names */
		String output = "| ";
		if(results.get(0) != null)
			for(Integer docId : results.get(0))
				output += Main.docs.get(docId) + " | ";
		else
			output = "No matching results found in the index !";
		
		/* print the results */
		System.out.println(output);
	}
	
	
	public static void printSuggestions(String keyword)
	{
		/* create new Soundex object */
		Soundex soundex = new Soundex();
		
		ArrayList<String> suggestions = Main.sIndex.get(soundex.convertToken(keyword));
		if(suggestions != null)
		{
			suggestions.remove(keyword);
			System.out.println("Or did u mean: " + suggestions);
		}
	}
	
	
	/**
	 * Boolean OR-function
	 * Compares the first two ArrayLists of the results ArrayList, empties the results ArrayList
	 * and if the answer isn't null, set's the new ArrayList as first record of results
	 * 
	 * @author Thijs Zandvliet
	 */
	public static void booleanOR(ArrayList<Integer> r1, ArrayList<Integer> r2)
	{
		/* detect empty ArrayLists */
		if(detectNull(r1, r2)) return;
		
		/* add a null-value at the end of both ArrayLists for the iterator */
		r1.add(null);
		r2.add(null);
		
		/* create two iterators for both ArrayLists */
		Iterator<Integer> p1 = r1.iterator();
		Iterator<Integer> p2 = r2.iterator();
		
		/* set the two first Integers so they can be compared */
		Integer i1 = p1.next();
		Integer i2 = p2.next();
		
		/* create a new ArrayList for the answer */
		ArrayList<Integer> answer = new ArrayList<Integer>();
		
		/* as long as there are at least the null-values add the end, loop */
		while(p1.hasNext() && p2.hasNext())
		{
			/* if both Integers are equal, add the answer and increase both iterators */
			if(i1 == i2)
			{
				answer.add(i1);
				i1 = p1.next();
				i2 = p2.next();
			}
			
			/* if the first Integer is smaller than the second, add it to the answer and increase the first iterator */
			else if(i1 < i2)
			{
				answer.add(i1);
				i1 = p1.next();
			}
			
			/* else, add it to the answer and increase the second iterator */
			else
			{
				answer.add(i2);
				i2 = p2.next();
			}
		}
		
		/* clear the results ArrayList and add the answer iff not null */
		results.clear();
		if(answer != null)
			results.add(answer);
	}
	
	
	/**
	 * Boolean AND-function
	 * Compares the first two ArrayLists of the results ArrayList, empties the results ArrayList
	 * and if the answer isn't null, set's the new ArrayList as first record of results
	 * 
	 * @author Thijs Zandvliet
	 */
	public static void booleanAND(ArrayList<Integer> r1, ArrayList<Integer> r2)
	{
		/* if one of the ArrayLists is empty, clear the results ArrayList and return */
		if(r1 == null || r2 == null)
		{
			results.clear();
			return;
		}
		
		/* add a null-value at the end of both ArrayLists for the iterator */
		r1.add(null);
		r2.add(null);
		
		/* create a new ArrayList for the answer */
		ArrayList<Integer> answer = new ArrayList<Integer>();
		
		/* create two iterators for both ArrayLists */
		Iterator<Integer> p1 = r1.iterator();
		Iterator<Integer> p2 = r2.iterator();
		
		/* set the two first Integers so they can be compared */
		Integer i1 = p1.next();
		Integer i2 = p2.next();
		
		/* as long as there are at least the null-values add the end, loop */
		while(p1.hasNext() && p2.hasNext())
		{
			/* if both Integers are equal, add the answer and increase both iterators */
			if(i1 == i2)
			{
				answer.add(i1);
				i1 = p1.next();
				i2 = p2.next();
			}
			
			/* if the first Integer is smaller than the second, increase the first iterator */
			else if(i1 < i2)
				i1 = p1.next();
			
			/* else, increase the second iterator */
			else
				i2 = p2.next();
		}
		
		/* clear the results ArrayList and add the answer iff not null */
		results.clear();
		if(answer != null)
			results.add(answer);
	}
	
	
	/**
	 * Boolean NOT-function
	 * Compares the first two ArrayLists of the results ArrayList, empties the results ArrayList
	 * and if the answer isn't null, set's the new ArrayList as first record of results
	 * 
	 * @author Thijs Zandvliet
	 */
	public static void booleanNOT(ArrayList<Integer> r1, ArrayList<Integer> r2)
	{
		/* detect empty ArrayLists */
		if(detectNull(r1, r2)) return;
		
		/* add a null-value at the end of both ArrayLists for the iterator */
		r1.add(null);
		r2.add(null);
		
		/* create a new ArrayList for the answer */
		ArrayList<Integer> answer = new ArrayList<Integer>();
		
		/* create two iterators for both ArrayLists */
		Iterator<Integer> p1 = r1.iterator();
		Iterator<Integer> p2 = r2.iterator();
		
		/* set the two first Integers so they can be compared */
		Integer i1 = p1.next();
		Integer i2 = p2.next();
		
		/* as long as there are at least the null-values add the end, loop */
		while(p1.hasNext() && p2.hasNext())
		{
			/* if both Integers are equal, increase both iterators */
			if(i1 == i2)
			{
				i1 = p1.next();
				i2 = p2.next();
			}
			
			/* if the first Integer is smaller than the second, increase the first iterator */
			else if(i1 < i2)
			{
				answer.add(i1);
				i1 = p1.next();
			}
			
			/* else, increase the second iterator */
			else
			{
				answer.add(i1);
				i2 = p2.next();
			}
		}
		
		/* clear the results ArrayList and add the answer iff not null */
		results.clear();
		if(answer != null)
			results.add(answer);
	}
	
	
	/**
	 * Detect if one the ArrayLists is empty
	 * 
	 * @author Thijs Zandvliet
	 * @param r1
	 * @param r2
	 * @return boolean
	 */
	public static boolean detectNull(ArrayList<Integer> r1, ArrayList<Integer> r2)
	{
		/* if one of the ArrayLists is empty, clear the results */
		if(r1 == null || r2 == null)
			results.clear();
		
		/* if both ArrayLists are empty, return true */
		if(r1 == null && r2 == null)
			return true;
		
		/* if r1 is empty, add r2 to the results and return true */
		else if(r1 == null)
		{
			results.add(r2);
			return true;
		}
		
		/* if r2 is empty, add r1 to the results and return true */
		else if(r2 == null)
		{
			results.add(r1);
			return true;
		}
		
		return false;
	}
	
}
