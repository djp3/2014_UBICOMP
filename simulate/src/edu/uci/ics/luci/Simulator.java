package edu.uci.ics.luci;

import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;

public class Simulator {
	
	static double top = 90.0d;
	static double bottom = 0.0d;
	static double left = -90.0d;
	static double right = 0.0d;
	static final int ONE_SEC = 1000;
	static final int ONE_MIN = 60* ONE_SEC;
	static final int ONE_HOUR = 60* ONE_MIN;
	static int END = ONE_HOUR;
	
	static final double WAKEUP_COST = 0.01;
	static final double TRANSMIT_COST = 1.00;
	
	static Random r = new Random();
	
	
	public static TreeMap<Long,GeoPoint> makeRandomTweets(double hertz) {
		
		TreeMap<Long,GeoPoint> ret  = new TreeMap<Long,GeoPoint>();
		
		for(double i=0.0 ;i < END; i+= (1000.0/hertz)){
			double jitter = r.nextDouble() * (1000.0/hertz);
			double latitude = r.nextDouble() * (top-bottom) + bottom;
			double longitude = r.nextDouble() * (right-left) + left;
			ret.put(Math.round(i+jitter), new GeoPoint(latitude,longitude));
		}
		
		return ret;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TreeMap<Integer,TreeMap<Long, GeoPoint>> trials = new TreeMap<Integer,TreeMap<Long, GeoPoint>>();
		
		if(top < bottom){
			throw new RuntimeException("top: "+top+" >= bottom: "+bottom);
		}
		if(right < left){
			throw new RuntimeException("top: "+right+" >= bottom: "+left);
		}
		
		for(int i=1; i<1000; i+=100){
			trials.put(i,makeRandomTweets(i));
		}
		
		/*Naive push */
		System.out.println("Naive Push:");
		System.out.println("Hertz:	Cost:");
		for(Entry<Integer, TreeMap<Long, GeoPoint>> trial :trials.entrySet()){
			double cost = 0.0d;
			TreeMap<Long, GeoPoint> tweets = trial.getValue();
			for(long i=0; i < END ; i++){
				if(tweets.containsKey(i)){
					cost += WAKEUP_COST + TRANSMIT_COST;
				}
			}
			System.out.println(trial.getKey()+"\t"+cost);
		}
		
		
		

	}


}
