package edu.uci.ics.luci;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Scanner;
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
	static final double LOCATION_ANNOUNCE_COST = 0.50;
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
	

	public static GeoPoint determineLocation(long i, TreeMap<Long, GeoPoint> trace) {
		Entry<Long, GeoPoint> before = trace.floorEntry(i);
		Entry<Long, GeoPoint> after = trace.ceilingEntry(i+1);
		if(before == null){
			throw new RuntimeException("Nothing with i:"+i);
		}
		if(after == null){
			throw new RuntimeException("Nothing after i:"+i+1);
		}
		long interval = (i - before.getKey());
		double fraction = (interval+0.0d) / ((after.getKey()-before.getKey())+0.0d);
		double latitude = fraction * (after.getValue().getLatitude() - before.getValue().getLatitude()) + before.getValue().getLatitude();
		double longitude = fraction * (after.getValue().getLongitude() - before.getValue().getLongitude()) + before.getValue().getLongitude();
		return new GeoPoint(latitude, longitude);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TreeMap<Long,GeoPoint> trace = new TreeMap<Long,GeoPoint>();

		Scanner fileScanner = null;
		try {
			fileScanner = new Scanner(new File("trace.txt"));
			while (fileScanner.hasNext()) {
				String next = fileScanner.nextLine();
				String[] split = next.split("[\\t\\s]");
				Long time = Long.valueOf(split[0]);
				Double latitude = Double.valueOf(split[1]);
				Double longitude = Double.valueOf(split[2]);
				trace.put(time, new GeoPoint(latitude,longitude));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (fileScanner != null) {
				fileScanner.close();
			}
		}
		
		
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
		System.out.println("Hertz:	Naive Push:	Smart Push:");
		for(Entry<Integer, TreeMap<Long, GeoPoint>> trial :trials.entrySet()){
			TreeMap<String, Double> cost = new TreeMap<String,Double>();
			
			{
				cost.put("Naive Push",0.0d);
				TreeMap<Long, GeoPoint> tweets = trial.getValue();
				for(long i=0; i < END ; i++){
					if(tweets.containsKey(i)){
						Double old = cost.get("Naive Push");
						cost.put("Naive Push",old + WAKEUP_COST + TRANSMIT_COST);
					}
				}
			}
			
			{
				cost.put("Smart Push",0.0d);
				TreeMap<Long, GeoPoint> tweets = trial.getValue();
				double range = 2.0d;
				for(long i=0; i < END ; i++){
					GeoPoint location = determineLocation(i,trace);
					if(tweets.containsKey(i)){
						Double old = cost.get("Smart Push");
						cost.put("Smart Push",old + WAKEUP_COST + LOCATION_ANNOUNCE_COST);
						GeoPoint tweetLocation = tweets.get(i);
						if(location.distance(tweetLocation) < range){
							old = cost.get("Smart Push");
							cost.put("Smart Push",old + TRANSMIT_COST);
						}
					}
				}
			}
			
			System.out.print(trial.getKey()+"\t");
			System.out.print(cost.get("Naive Push")+"\t");
			System.out.print(cost.get("Smart Push")+"\t");
			System.out.println();
		}
		
		
		

	}



}
