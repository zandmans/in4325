/**
 * This class contains all the different search methods
 * 
 * @version 0.1
 */

package nl.tudelft.in4325.index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.TreeSet;
import java.util.Vector;

import nl.tudelft.in4325.main.Main;

public class Search {

	/* variable declarations */
	private static ArrayList<Hashtable<Integer,Integer>> results;
	private static Hashtable<String,Hashtable<Integer,Integer>> pertermResults;
	private static Hashtable<String,Hashtable<Integer,Integer>> optimizedResults;
	
	/**
	 * Performs a boolean search on the given query
	 * 
	 * @author Thijs Zandvliet
	 * @param query
	 */
	public static void booleanSearch(String query, int mode)
	{
		/* if in optimize cosine ranking mode */
		if(mode == 1)
		{
			/* strip the opening $ character */
			query = query.substring(1);
		}
		
		/* create a new scanner which reads the query one word at the time */
		Scanner sc = new Scanner(query);
		
		/* create a new ArrayList for the results */
		results = new ArrayList<Hashtable<Integer,Integer>>();
		pertermResults = new Hashtable<String,Hashtable<Integer,Integer>>();
		
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
					Hashtable<Integer,Integer> result = Main.index.get(nextWord);
					results.add(result);
					pertermResults.put(nextWord, result);
				}
				else
				{
					/* print the suggestions by using soundex */
					printSuggestions(keyword);
					
					/* search for the keyword in the Hashtable and add it to the results */
					Hashtable<Integer,Integer> result = Main.index.get(keyword);
					results.add(result);
					pertermResults.put(keyword, result);
				}

				/* receive the first two ArrayLists */
				Hashtable<Integer,Integer> r1 = results.get(0);
				Hashtable<Integer,Integer> r2 = results.get(1);
				
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
				
				Hashtable<Integer,Integer> result = Main.index.get(keyword);
				
				/* only add if there are results */
				if(result != null)
				{
					results.add(result);
					pertermResults.put(keyword, result);
				}
				else System.out.println("No results.");
			}
		}
		
		/* if there are results to be processed */
		if(!pertermResults.isEmpty() && mode == 0)
		{
			// rank them
			PriorityQueue<RankedNode> ranked = Ranking.getHeap(pertermResults);
			//System.out.println(pertermResults);
			printRanked(ranked);
			printResult();
		}
		
		/* if there are results to be processed and optimization to be done */
		else if(!pertermResults.isEmpty() && mode == 1)
		{
			System.out.println("Running optimized cosine ranking.");
			
			/* we only want to process docs that contain at least half of the terms, ceilinged */
			double size = (double)pertermResults.size()/2;
			size = Math.ceil(size);
			
			System.out.println("Unoptimized result: " + pertermResults);
			
			/* filter the results */
			optimizeResults((int)size);
			
			System.out.println("Optimized result: " + optimizedResults);
			
			// rank only the docs that contain 'size' of the terms
			PriorityQueue<RankedNode> ranked = Ranking.getHeap(optimizedResults);
			
			printRanked(ranked);
			printResult();
		}
	}
	
	/**
	 * Selects only the documents that contain the amount of terms determined by parameter size,
	 * and adds them to variable optimizedResults.
	 * 
	 * @author Kristian Slabbekoorn
	 * @param size
	 */
	@SuppressWarnings("unchecked")
	public static void optimizeResults(int size) {
		
		/* copy the full result list to the optimized list */
		optimizedResults = (Hashtable<String,Hashtable<Integer,Integer>>)pertermResults.clone();
		
		/* retrieve all query terms */
		Vector<String> terms = new Vector<String>(pertermResults.keySet());
		
		/* retrieve the value collection belonging to each term (<docs containing this word, number of instances>)*/
		Collection<Hashtable<Integer, Integer>> values = pertermResults.values();
		
		/* construct and init iterator for values */
		Iterator<Hashtable<Integer, Integer>> ti = values.iterator();
	    Hashtable<Integer, Integer> currentTable = ti.next();
	    
	    /* retrieve a sorted union set of all documents that contain at least one of the terms */
	    TreeSet<Integer> allDocuments = new TreeSet<Integer>();
	    allDocuments.addAll(currentTable.keySet());
	    while(ti.hasNext()){
	    	currentTable = ti.next();
	    	allDocuments.addAll(currentTable.keySet());
	    }
	    
		/* construct and init iterator for all documents */
	    Iterator<Integer> di = allDocuments.iterator();
	    int currentDoc;
	    
	    /* create counter that will count the number of query terms present in the current doc */
	    int counter;
	    
	    /* while there are still docs in the union set... */
	    while(di.hasNext()){
	    	
	    	/* get next doc */
	    	currentDoc = di.next();
	    	
	    	/* reinit values iterator */
		    ti = values.iterator();
		    
		    /* init counter */
		    counter = 0;
		    
		    /* iterate over hashtables linked to each term */
		    while(ti.hasNext()){
		    	currentTable = ti.next();
		    	/* if the current hashtable contains the current document, increase the counter */
				if(currentTable.containsKey(currentDoc))
					counter++;
			}
		    
		    /* if the current document contained less than 'size' terms... */
		    if(counter < size) {
		    	
		    	/* remove the current document from the result list */
		    	for (int i = 0; i < terms.size(); i++) {
		 			optimizedResults.get(terms.get(i)).remove(currentDoc);
		 		}
		    }
	    }
	}
	
	public static void printRanked(PriorityQueue<RankedNode> p){
		String output = "Ranked result (top 3 of "+p.size()+")";
		RankedNode n;
		for(int i = 0;i<3 && !p.isEmpty();i++){
			n = p.poll();
			output += "\n"+Main.docs.get(n.getDocID())+" ["+n.getScore()+"]";
		}
		System.out.println(output);
	}
	
	public static void printResult(){
		/* convert the docId's back to the document names */
		String output = "| ";
		
		if(results != null && results.size() > 0 && results.get(0) != null && results.get(0).size() > 0)
			for(Integer docId : results.get(0).keySet())
				output += Main.docs.get(docId) + " | ";
		else
			output = "No matching results found in the index !";
		
		/* print the results */
		System.out.println(output);
	}
	
	
	/**
	 * Print suggestions based on the soundex algorithm
	 * 
	 * @author Thijs Zandvliet
	 * @param keyword
	 */
	@SuppressWarnings("unchecked")
	public static void printSuggestions(String keyword)
	{
		/* create new Soundex object */
		Soundex soundex = new Soundex();
		
		/* find words with the same soundex-string as the keyword */
		//ArrayList<String> suggestions = (ArrayList<String>)Main.sIndex.get(soundex.convertToken(keyword)).clone();
		ArrayList<String> suggestions = (ArrayList<String>)Main.sIndex.get(soundex.convertToken(keyword));
		
		/* if there are suggestions available, remove the keyword and print the rest */
		if(suggestions != null)
		{
			suggestions.remove(keyword);
			System.out.println("Or did u mean: " + suggestions);
		} else System.out.println("No suggestions available.");
	}
	
	
	/**
	 * Boolean OR-function
	 * Compares the first two ArrayLists of the results ArrayList, empties the results ArrayList
	 * and if the answer isn't null, set's the new ArrayList as first record of results
	 * 
	 * @author Thijs Zandvliet
	 */
	public static void booleanOR(Hashtable<Integer,Integer> r1, Hashtable<Integer,Integer> r2)
	{
		/* detect empty HashTables */
		if(detectNull(r1, r2)) return;
		
		/* sort hashtable elements */
		Vector<Integer> v1 = new Vector<Integer>(r1.keySet());
		Vector<Integer> v2 = new Vector<Integer>(r2.keySet());
	    Collections.sort(v1);
	    Collections.sort(v2);
	    
	    /* add a null-value at the end of both Vectors for the iterator */
	    v1.add(null);
	    v2.add(null);
		
		/* create two iterators for both ArrayLists */
		Iterator<Integer> p1 = v1.iterator();
		Iterator<Integer> p2 = v2.iterator();
		
		/* set the two first Integers so they can be compared */
		Integer i1 = p1.next();
		Integer i2 = p2.next();

		/* create a new ArrayList for the answer */
		Hashtable<Integer,Integer> answer = new Hashtable<Integer,Integer>();
		
		/* as long as there are at least the null-values add the end, loop */
		while(p1.hasNext() && p2.hasNext())
		{
			/* if both Integers are equal, add the answer and increase both iterators */
			if(i1 == i2)
			{
				answer.put(i1,r1.get(i1));
				i1 = p1.next();
				i2 = p2.next();
			}
			
			/* if the first Integer is smaller than the second, add it to the answer and increase the first iterator */
			else if(i1 < i2)
			{
				answer.put(i1,r1.get(i1));
				i1 = p1.next();
			}
			
			/* else, add it to the answer and increase the second iterator */
			else
			{
				answer.put(i2,r2.get(i2));
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
	public static void booleanAND(Hashtable<Integer,Integer> r1, Hashtable<Integer,Integer> r2)
	{
		/* if one of the ArrayLists is empty, clear the results ArrayList and return */
		if(r1 == null || r2 == null)
		{
			results.clear();
			return;
		}
		
		/* create a new HashTable for the answer */
		Hashtable<Integer,Integer> answer = new Hashtable<Integer,Integer>();
		
		/* sort hashtable elements */
		Vector<Integer> v1 = new Vector<Integer>(r1.keySet());
		Vector<Integer> v2 = new Vector<Integer>(r2.keySet());
	    Collections.sort(v1);
	    Collections.sort(v2);
	    
	    /* add a null-value at the end of both Vectors for the iterator */
	    v1.add(null);
	    v2.add(null);
		
		/* create two iterators for both ArrayLists */
		Iterator<Integer> p1 = v1.iterator();
		Iterator<Integer> p2 = v2.iterator();
		
		/* set the two first Integers so they can be compared */
		Integer i1 = p1.next();
		Integer i2 = p2.next();
		
		/* as long as there are at least the null-values add the end, loop */
		while(p1.hasNext() && p2.hasNext())
		{
			/* if both Integers are equal, add the answer and increase both iterators */
			if(i1 == i2)
			{
				answer.put(i1,r1.get(i1));
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
	public static void booleanNOT(Hashtable<Integer,Integer> r1, Hashtable<Integer,Integer> r2)
	{
		/* detect empty ArrayLists */
		if(detectNull(r1, r2)) return;
		
		/* create a new ArrayList for the answer */
		Hashtable<Integer,Integer> answer = new Hashtable<Integer,Integer>();
		
		/* sort hashtable elements */
		Vector<Integer> v1 = new Vector<Integer>(r1.keySet());
		Vector<Integer> v2 = new Vector<Integer>(r2.keySet());
	    Collections.sort(v1);
	    Collections.sort(v2);
	    
	    /* add a null-value at the end of both Vectors for the iterator */
	    v1.add(null);
	    v2.add(null);
		
		/* create two iterators for both ArrayLists */
		Iterator<Integer> p1 = v1.iterator();
		Iterator<Integer> p2 = v2.iterator();
		
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
				answer.put(i1,r1.get(i1));
				i1 = p1.next();
			}
			
			/* else, increase the second iterator */
			else
			{
				answer.put(i1,r1.get(i1));
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
	public static boolean detectNull(Hashtable<Integer,Integer> r1, Hashtable<Integer,Integer> r2)
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
