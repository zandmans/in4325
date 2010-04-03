package nl.tudelft.in4325.index;

import java.util.Scanner;
import java.util.Vector;

import nl.tudelft.in4325.main.Main;

public class Search {

	private static Vector<Vector<Integer>> results;
	
	public static void booleanSearch(String query)
	{
		Scanner sc = new Scanner(query);
		results = new Vector<Vector<Integer>>();
		
		while(sc.hasNext())
		{
			String keyword = sc.next();
			if(results.size() > 0)
			{
				if(sc.hasNext())
				{
					Vector<Integer> result = Main.index.get(sc.next());
					results.add(result);
				}
				else
				{
					System.err.println("Syntax error in Query");
					return;
				}
				if(keyword.equals("OR")) booleanOR();
				else if(keyword.equals("AND")) booleanAND();
				else if(keyword.equals("NOT")) booleanNOT();
				else booleanOR();
			}
			else
			{
				Vector<Integer> result = Main.index.get(keyword);
				results.add(result);
			}
		}
	}
	
	
	public static void booleanOR()
	{
		System.out.println("OR-query");
		System.out.println(results);
	}
	
	
	public static void booleanAND()
	{
		System.out.println("AND-query");
		System.out.println(results);
	}
	
	
	public static void booleanNOT()
	{
		System.out.println("NOT-query");
		System.out.println(results);
	}
	
}
