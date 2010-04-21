package nl.tudelft.in4325.index;

import java.util.ArrayList;
import java.util.List;

/**
 * class Node representing a node in the BTree or (also called Trie from reTRIEval)
 * @author Pierre Lopez Barbosa
 */

public class Node {	
	private Node parent = null;
	/** Array to hold children of each node */
	private Node[] children;
	/** Quick way to check if any children exist */
	private boolean isLeaf;	
	/** Does this node represent the last character of a word */
	private boolean isWord;	
	/** The character this node represents */
	private char character;
	
	/**
	 * Constructor for top level root node.
	 */
	public Node() {
		children = new Node[50];
		isLeaf = true;
		isWord = false;
	}

	/**
	 * Constructor for child node.
	 */
	public Node(char character) {
		this();
		setCharacter(character);
	}

	/**
	 * Adds a word to this node. This method is called recursively and
	 * adds child nodes for each successive letter in the word, therefore
	 * recursive calls will be made with partial words.
	 * @param word the word to add
	 */
	protected void addWord(String word) {
		setIsLeaf(false);
		/* position of the letter in the array alphabetical order */
		int charPos = Math.abs(word.charAt(0) - 'a');
		
		/* if character does not exist in tree yet, create new child for the root */
		if (getChildAt(charPos) == null) {
			setChildAt(new Node(word.charAt(0)), charPos);
			getChildAt(charPos).setParent(this);
		}

		/* if word is not only one character, continue checking for next characters */
		if (word.length() > 1) {
			/* recursively invoke this method for the string starting at character-position 1 */
			getChildAt(charPos).addWord(word.substring(1));
		} else {
			/* reach the end of the word, indicate for this node that some word ends at this node */
			getChildAt(charPos).setIsWord(true);
		}
	}

	/**
	 * Returns the child Node representing the given char,
	 * or null if no node exists.
	 * @param c
	 * @return
	 */
	protected Node getNode(char c) {
		try {
			return children[Math.abs(c - 'a')];
		} catch(Exception ex) {
			return null;			
		}
	}

	/**
	 * Returns a List of String objects which are lower in the
	 * hierarchy than this node.
	 * @return
	 */
	protected List<String> getWords() {
		/* Create a list to return */
		List<String> list = new ArrayList<String>();

		/* If this node represents a word, add it */
		if (getIsWord()) {
				list.add(toString());
		}

		/* If any children */
		if (!getIsLeaf()) {
			/* Add any words belonging to any children */
			for (int i=0; i < children.length; i++) {
				if (getChildAt(i) != null) {
					/* recursively invoke method to get lower level characters */
					list.addAll(getChildAt(i).getWords());
				}
			}
		}
		return list;  
	}

	/**
	 * Gets the String that this node represents.
	 * Concatenates the result in a bottom-up way.
	 * For example, if this node represents the character t, whose parent
	 * represents the character a, whose parent represents the character
	 * c, then the String would be concatenated to "cat".
	 * @return
	 */
	public String toString() {
		/* parent is the root node */
		if (getParent() == null) {
			return "";
		}
		else {
			/* concatenate in bottom-up way */
			return getParent().toString() + 
				new String( new char[] { character } );
		}
	}  
	
	////////// Getters and setters \\\\\\\\\\\\
	
	/** Getter parent */
	public Node getParent() {
		return parent;
	}
	
	/** Setter parent */
	public void setParent(Node parent) {
		this.parent = parent;
	}
	
	/** Getter character */
	public char getCharacter() {
		return character;
	}
	
	/** Setter character */
	public void setCharacter(char character) {
		this.character = character;
	}
	
	/** Getter isLeaf */
	public boolean getIsLeaf() {
		return isLeaf;
	}
	
	/** Setter isLeaf */
	public void setIsLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}
	
	/** Getter children array */
	public Node[] getChildren() {
		return children;
	}
	
	/** Setter children array */
	public void setChildren(Node[] children) {
		this.children = children;
	}
	
	/** Getter children array */
	public Node getChildAt(int i) {
		return children[i];
	}
	
	/** Setter children array */
	public void setChildAt(Node child, int i) {
		children[i] = child;
	}
	
	/** Getter isWord */
	public boolean getIsWord() {
		return isWord;
	}
	
	/** Setter isWord */
	public void setIsWord(boolean isWord) {
		this.isWord = isWord;
	}
}
