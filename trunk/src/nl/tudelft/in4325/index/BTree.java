package nl.tudelft.in4325.index;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import nl.tudelft.in4325.main.Main;

/**
 * class Btree3 which implements the B-tree for 
 * searching words in dictionary using wildcards
 * 
 * @author Pierre Lopez Barbosa
 *
 */

public class BTree {
	private Node root;

	/**
	 * Constructor: create root node with no character
	 * 'n' for normal BTree
	 * 'r' for reversed BTree
	 * Insert all words in dictionary in the balanced tree
	 */
	public BTree(char type) {
		root = new Node();
		Enumeration<String> e = Main.index.keys();
		
		/* insert all keys (words) in the tree */
		while (e.hasMoreElements()) {
			String next = e.nextElement();
			if (!next.isEmpty()) {
				if(type == 'n') { /* Add words to tree in normal order */
					addWord(next); 
				} else if(type == 'r') { /* Reverse word to create reversed BTree */
					String reversedWord = reverseWord(next);
					addWord(reversedWord);
				}
			}
		}
	}

	/**
	 * Adds a word to the BTree
	 * @param word
	 */
	public void addWord(String word) {
		/* in case of having capital letters, convert all to lower case  */
		root.addWord(word.toLowerCase());
	}

	/**
	 * get words in the BTree with given prefix
	 * @param prefix
	 * @return list containing String objects containing the words in
	 *         the BTree with the given prefix.
	 */
	public List<String> search(String prefix) {
		/* Find the node which represents the last letter of the prefix  */
		Node lastNode = root;
		for (int i=0; i<prefix.length(); i++) {
			lastNode = lastNode.getNode(prefix.charAt(i));

			/* If no node matches, then no words exist, return empty list */
			if (lastNode == null) return new ArrayList<String>();	 
		}
		/* Return the words which start which start with the provided prefix */
		return lastNode.getWords();
	}
	
	/**
	 * Reverses the input string
	 * 
	 * @param s word to be reversed
	 * @return reversed word
	 */
	public String reverseWord(String s) {
		char last;
		String reversed = "";
		for(int i = s.length()-1; i >= 0; i--) {
			last = s.charAt(i);
			reversed += last;
		}
		return reversed;
	}
}