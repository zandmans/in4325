package nl.tudelft.in4325.index;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.PriorityQueue;

import nl.tudelft.in4325.main.Main;
/**
 * implements the cosine ranking algorithm
 * @author Peter Dijkshoorn
 *
 */
public class Ranking {
	public static PriorityQueue<RankedNode> result; // heap based prio queue

	public static PriorityQueue<RankedNode> getHeap(Hashtable<String,Hashtable<Integer,Integer>> unranked, ArrayList<Hashtable<Integer,Integer>> booleanresults){
		// calculate each cosine and construct heap
		result = new PriorityQueue<RankedNode>();
		
		// per document the tfidf vector
		Hashtable<Integer,Hashtable<String,Double>> intermediate = new Hashtable<Integer,Hashtable<String,Double>>();
		
		
		int N = Main.docs.size();
		int tf,df;
		double tfidf;
		Hashtable<String,Double> queryVector = new Hashtable<String,Double>();
		boolean inActualResult;
		
		// for each term
		for(String term : unranked.keySet()){
			df = Main.index.get(term).size();
			
			queryVector.put(term, tfidf(1,df,N));
			
			
			// for each document
			for(Integer docID : unranked.get(term).keySet()){
				
				// check whether result is somewhere in boolean result
				inActualResult = false;
				for(Hashtable<Integer,Integer> match : booleanresults){
					if(match.containsKey(docID)){
						inActualResult = true;
					}
				}
				if(!inActualResult)continue;
				// calculate score and put in prioqueue
				tf = unranked.get(term).get(docID);
				tfidf = tfidf(tf,df,N);
				
				if(!intermediate.containsKey(docID)){
					intermediate.put(docID, new Hashtable<String,Double>());
				}
				intermediate.get(docID).put(term, tfidf);
			}
		}
		
		
		// each document: normalize, cosines and store score in heap
		for(int docID : intermediate.keySet()){
			result.add(new RankedNode(docID, cosines(normalize(queryVector), normalize(intermediate.get(docID)))));
		}
		
		return result;
		
	}
	
	/**
	 * for each term and document hit, tf-idf needs to be calculated
	 * @param tf term frequency, how many times is this term stated in this document
	 * @param df document frequency, how many douments contain this word
	 * @param N total count of documents in system
	 * @return
	 * 
	 */
	public static double tfidf(int tf, int df, int N){
		return (1+Math.log(tf))*Math.log10(((double)N)/((double)df));
	}
	/**
	 * returns normalized version of vector
	 * 
	 * @param x
	 * @return y
	 */
	public static Hashtable<String,Double> normalize(Hashtable<String,Double> x){
		Hashtable<String,Double> y = new Hashtable<String,Double>();
		
		// calculate length
		double length = 0;
		for(double xi: x.values()){
			length += Math.pow(xi, 2);
		}
		length = Math.sqrt(length);
		
		// normalize
		for(String term: x.keySet()){
			y.put(term, x.get(term)/length);
		}
		return y;
	}
	/**
	 * short cosines implementation for normalized vectors
	 * @param q
	 * @param d
	 * @return
	 */
	public static double cosines(Hashtable<String,Double> q, Hashtable<String,Double> d){
		double sum = 0;
		for(String term : q.keySet()){
			if(d.containsKey(term))
				sum += q.get(term)*d.get(term);
		}
		return sum;
	}
}
