package nl.tudelft.in4325.main;

import java.io.FileNotFoundException;
import java.text.Normalizer;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.Vector;

public class Main
{

	public static void main(String[] args) throws FileNotFoundException
	{
		Scanner sc = new Scanner(System.in);
		Indexer indexer = new Indexer();
		
		indexer.init();
		//indexer.indexData();
		Hashtable<String, Vector<String>> index = indexer.getIndex();
		
		System.out.println("Index build successfully, number of words indexed: " + index.size());
		
		while(true)
		{
			
			System.out.println("Query: ");
			String query = sc.nextLine();
			
			if(query.equals("q")) break;
		
			Object result = index.get(query);
			System.out.println(result);
		}
		
		/*
		Enumeration<String> e = index.keys();
		while(e.hasMoreElements())
			System.out.println(e.nextElement());
		*/
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
	
}
