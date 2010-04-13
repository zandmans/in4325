/**
 * This class takes care of the indexing procedure of the documents
 * 
 * @author Thijs Zandvliet
 */

package nl.tudelft.in4325.index;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;

import nl.tudelft.in4325.main.Main;

public class Indexer
{
	/* variable declarations */ 
	private Hashtable<String, Hashtable<Integer,Integer>> index;
	private Hashtable<Integer, String> docs;

	
	/**
	 * Constructor
	 */
	public Indexer()
	{
		/* create a new Hashtable<word, doc ids> for the index */
		index = new Hashtable<String, Hashtable<Integer,Integer>>();
		/* create a new Hashtable<doc id, doc name> for the documents */
		docs = new Hashtable<Integer, String>();
	}
	
	
	/**
	 * Initialize
	 * Read all the documents in the directory docs
	 * 
	 * @throws FileNotFoundException
	 */
	public void init() throws FileNotFoundException
	{
		/* define a new ArrayList for the found words */
		Hashtable<Integer,Integer> values;
		
		/* define a new file object with the directory to read and retrieve all the content */
		File dir = new File("src/docs");
		String[] children = dir.list();
		
		/* if the directory isn't empty... */
		if(children != null)
		{
			/* document counter */
			int i = 0;
			
			/* for each filename in the directory... */
			for(String filename : children)
			{
				/* if the filename doesn't start with a dot, then... , otherwise it could be a configuration file like .svn */
				if(filename.charAt(0) != '.')
				{	
					/* increase the document counter */
					i++;
					
					/* create a new scanner for the found file and put the filename to the docs ArrayList */
					Scanner sc = new Scanner(new File("src/docs/" + filename));
					docs.put(i, filename);
					
					/* as long as the file contains words... */
					while(sc.hasNext())
					{
						/* save the next word into the string key */
						String key = Main.formatString(sc.next());
						
						/* if the key already exists in the index, than add the document to the list if it isn't already there */
						if(index.containsKey(key))
						{
							values = index.get(key);
							if(!values.containsKey(i)){
								values.put(i,1);
							} else {
								values.put(i, values.get(i)+1);
							}
							index.put(key, values);
						}
						/* else make a new ArrayList and add it to a new key into the Hashtable */
						else
						{
							values = new Hashtable<Integer,Integer>();
							values.put(i,1);
							index.put(key, values);
						}
					}
				}
			}
		}
	}
	
	
	/**
	 * Return the index
	 * 
	 * @return index
	 */
	public Hashtable<String, Hashtable<Integer,Integer>> getIndex()
	{
		return index;
	}
	
	
	/**
	 * Return the names of the documents
	 * 
	 * @return docs
	 */
	public Hashtable<Integer, String> getDocs()
	{
		return docs;
	}
	
}
