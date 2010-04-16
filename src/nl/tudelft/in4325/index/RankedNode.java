package nl.tudelft.in4325.index;
/**
 * container class for a document in a heap, proper compare function implemented
 * @author Peter Dijkshoorn
 *
 */
public class RankedNode implements Comparable<RankedNode>{
	private double score;
	private int docID;
	
	public RankedNode(int docID, double s){
		this.setDocID(docID);
		this.setScore(s);
	}
	public void setDocID(int d){docID = d;}
	public int getDocID(){return docID;}
	public double getScore(){return score;}
	public void setScore(double s){score = s;}
	public int compareTo(RankedNode o){
		double s1 = this.getScore();
		double s2 = o.getScore();
		int i = 0;
		if(s1 < s2) i--;
		if(s1 > s2) i++;
		return i;
	}
}
