package nl.tudelft.in4325.main;

import java.io.FileNotFoundException;
import java.text.Normalizer;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.Vector;

import nl.tudelft.in4325.index.Indexer;
import nl.tudelft.in4325.index.Search;

public class Main
{

	public static Hashtable<String, Vector<Integer>> index;
	
	public static void main(String[] args) throws FileNotFoundException
	{
		Scanner sc = new Scanner(System.in);
		Indexer indexer = new Indexer();
		
		indexer.init();
		index = indexer.getIndex();
		
		System.out.println("Index build successfully, number of words indexed: " + index.size());
		
		while(true)
		{
			System.out.println("Query: ");
			
			String query = sc.nextLine();
			if(query.equals("q")) System.exit(0);
			
			Search.booleanSearch(query);
		}
	}
	
	
	public static String formatString(String str)
	{
		try
		{
			return Normalizer.normalize(str, Normalizer.Form.NFC)
			.toLowerCase()
			.replaceAll("[^a-zA-Z 0-9]+","");
		}
		catch(Exception ex)
		{
			return null;
		}
    }
	
	
	public static void testIndex()
	{
		Enumeration<String> e = index.keys();
		while(e.hasMoreElements())
			System.out.println(e.nextElement());
	}
	
}
