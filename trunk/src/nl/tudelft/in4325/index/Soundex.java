/**
 * Implementation of the soundex algorithm
 * 
 * @author Thijs Zandvliet
 * @version 0.2
 */

package nl.tudelft.in4325.index;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import nl.tudelft.in4325.main.Main;

public class Soundex
{

	/**
	 * Empty constructor
	 */
	public Soundex()
	{
		
	}
	
	
	/**
	 * Converts all the words in the index to a soundex presentations
	 * 
	 * @return Hashtable
	 */
	public Hashtable<String, ArrayList<String>> convertIndex()
	{
		/* declare variables */
		Hashtable<String, ArrayList<String>> sIndex = new Hashtable<String, ArrayList<String>>();
		ArrayList<String> values;
		String key, value = "";
		
		/* create an enumerator for all the keys in the index */
		Enumeration<String> e = Main.index.keys();
		
		/* as long as there are more elements in the index */
		while(e.hasMoreElements())
		{
			/* save the next word into the string value */
			value = e.nextElement();
			
			/* convert the value with the soundex algorithm */
			key = convertToken(value);
			
			/* if the key already exists in the index, than add the word to the list if it isn't already there */
			if(sIndex.containsKey(key))
			{
				values = sIndex.get(key);
				if(!values.contains(value))
					values.add(value);
				sIndex.put(key, values);
			}
			
			/* else make a new ArrayList and add it to a new key into the Hashtable */
			else
			{
				values = new ArrayList<String>();
				values.add(value);
				sIndex.put(key, values);
			}	
		}
		
		/* return the soundex index */
		return sIndex;
	}
	
	
	/**
	 * Convert the given word to a soundex presentation
	 * 
	 * @param s
	 * @return String
	 */
	public String convertToken(String s)
	{
		/* if the string is empty, return it */
		if(s.trim().equals("")) return s;
		
		/* make all the characters in the string uppercase */
		String wordStr = s.toUpperCase();
		
		/* save the first letter */
		char firstLetter = wordStr.charAt(0);
		
		/* replace all the characters with an integer according to the rules of soundex */
		wordStr = wordStr.replaceAll("[^A-Z]", "0");
		wordStr = wordStr.replaceAll("[AEIOUHWY]", "0");
		wordStr = wordStr.replaceAll("[BFPV]", "1");
		wordStr = wordStr.replaceAll("[CGJKQSXZ]", "2");
		wordStr = wordStr.replaceAll("[DT]", "3");
		wordStr = wordStr.replaceAll("[L]", "4");
		wordStr = wordStr.replaceAll("[MN]", "5");
		wordStr = wordStr.replaceAll("[R]", "6");
		
		/* remove all pairs of consecutive digits and remove all the zeros  */
		String output = "" + firstLetter;
		for (int i = 1; i < wordStr.length(); i++)
			if (wordStr.charAt(i) != wordStr.charAt(i-1) && wordStr.charAt(i) != '0')
				output += wordStr.charAt(i);
		
		/* pad the resulting string with trailing zeros */
		output += "0000";
		
		/* return the first four positions, which is of the form <uppercase letter><digit><digit><digit> */
		return output.substring(0, 4);
	}
	
}
