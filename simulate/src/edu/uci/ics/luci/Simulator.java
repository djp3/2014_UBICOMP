package edu.uci.ics.luci;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Random;
import java.util.Scanner;
import java.util.TreeMap;

public class Simulator {
	
	static double top = -1*Double.MAX_VALUE;
	static double bottom = Double.MAX_VALUE;
	
	static double left = Double.MAX_VALUE;
	static double right = -1*Double.MAX_VALUE;
	
	static double slow = Double.MAX_VALUE;
	static double fast = -1*Double.MAX_VALUE;
	
	static final int ONE_SEC = 1000;
	static final int ONE_MIN = 60* ONE_SEC;
	static final int ONE_HOUR = 60* ONE_MIN;
	//static int END = ONE_HOUR;
	//END matches trace Tao collected
	static int END = 7158999;
	
	static final double WAKEUP_COST = 0.01;
	static final double LOCATION_ANNOUNCE_COST = 0.50;
	static final double TRANSMIT_COST = 1.00;
	
	static Random r = new Random(10L);
	
	
	public static TreeMap<Long,GeoPoint> makeRandomTweets(double hertz) {
		
		TreeMap<Long,GeoPoint> ret  = new TreeMap<Long,GeoPoint>();
		
		for(double i=0.0d ;i < END; i+= (1000.0d/hertz)){
			double jitter = r.nextDouble() * (1000.0d/hertz);
			double latitude = r.nextDouble() * (top-bottom) + bottom;
			double longitude = r.nextDouble() * (right-left) + left;
			double speed = 0.0d; //speed of a tweet?  
			ret.put(Math.round(i+jitter), new GeoPoint(latitude,longitude,speed));
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
		double speed = fraction * (after.getValue().getSpeed() - before.getValue().getSpeed()) + before.getValue().getSpeed();
		return new GeoPoint(latitude, longitude,speed);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TreeMap<Long,GeoPoint> trace = new TreeMap<Long,GeoPoint>();

		Scanner fileScanner = null;
		try {
			fileScanner = new Scanner(new File("location_trace.txt"));
			while (fileScanner.hasNext()) {
				String next = fileScanner.nextLine();
				String[] split = next.split("[\\t\\s]");
				Long time = Long.valueOf(split[0]);
				Double latitude = Double.valueOf(split[1]);
				Double longitude = Double.valueOf(split[2]);
				Double speed = Double.valueOf(split[3]);
				trace.put(time, new GeoPoint(latitude,longitude,speed));
				
				if(latitude > top){
					top = latitude;
				}
				if(latitude < bottom){
					bottom = latitude;
				}
				if(longitude > right){
					right = longitude;
				}
				if(longitude < left){
					left = longitude;
				}
				if(speed > fast){
					fast = speed;
				}
				if(speed < slow){
					slow = speed;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (fileScanner != null) {
				fileScanner.close();
			}
		}
		System.out.println("top: "+top);
		System.out.println("bottom: "+bottom);
		System.out.println("Height in km: "+GeoPoint._distance(top,left,bottom,left,'K'));
		System.out.println("left: "+left);
		System.out.println("right: "+right);
		System.out.println("Width in km: "+GeoPoint._distance(top,left,top,right,'K'));
		System.out.println("fast: "+fast);
		System.out.println("slow: "+slow);
		
		
		TreeMap<Integer,TreeMap<Long, GeoPoint>> trials = new TreeMap<Integer,TreeMap<Long, GeoPoint>>();
		
		if(top < bottom){
			throw new RuntimeException("top: "+top+" >= bottom: "+bottom);
		}
		if(right < left){
			throw new RuntimeException("top: "+right+" >= bottom: "+left);
		}
		if(fast < slow){
			throw new RuntimeException("fast: "+fast+" >= slow: "+slow);
		}
		if((fast < 0.0d) || (slow < 0.0d)){
			throw new RuntimeException("fast: "+fast+" or slow: "+slow+" is less than 0");
		}
		
		System.out.println("Making random tweets");
		for(int i=1; i<1000; i+= 75){
			if(i == 1){
				trials.put(i,makeRandomTweets(i));
			}
			else{
				trials.put(i-1,makeRandomTweets(i-1));
			}
		}
		System.out.println("\tDone");
		
		
		double range = 2.0d;
		
		System.out.println("\nHertz\tNaive Push\tSmart Push\tNaive Pull\tSmart Pull\tStatic Geofence\tGeoJourney");
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
			
			long pollInterval = 5*ONE_MIN;
			{
				cost.put("Naive Pull",0.0d);
				TreeMap<Long, GeoPoint> tweets = trial.getValue();
				for(long i=0; i < END ; i += pollInterval){
					NavigableMap<Long, GeoPoint> pulledTweets = tweets.subMap(i-pollInterval, false, i, true);
					Double old = cost.get("Naive Pull");
					cost.put("Naive Pull", old + WAKEUP_COST + pulledTweets.size()*TRANSMIT_COST);
				}
			}
			{
				cost.put("Smart Pull",0.0d);
				TreeMap<Long, GeoPoint> tweets = trial.getValue();
				for(long i=0; i < END ; i+= pollInterval){
					NavigableMap<Long, GeoPoint> pulledTweets = tweets.subMap(i-pollInterval, false, i, true);
					Double old = cost.get("Smart Pull");
					cost.put("Smart Pull", old + WAKEUP_COST +LOCATION_ANNOUNCE_COST);
					GeoPoint location = determineLocation(i,trace);
					for(Entry<Long, GeoPoint> tweet :pulledTweets.entrySet()){
						GeoPoint tweetLocation = tweet.getValue();
						if(location.distance(tweetLocation) < range){
							old = cost.get("Smart Pull");
							cost.put("Smart Pull",old + TRANSMIT_COST);
						}
					}
				}
			}
			
			{
				cost.put("Static Geofence",0.0d);
				TreeMap<Long, GeoPoint> tweets = trial.getValue();
				GeoPoint geoFenceCenter = null;
				double geoFenceInnerRadius = 0.0d;
				double geoFenceOuterRadius = 0.0d;
				
				for(long i=0; i < END ; i++){
					
					//Figure out where I am
					GeoPoint location = determineLocation(i,trace);
					
					//If I'm out of the geofence then update it
					if((geoFenceCenter == null) || (location.distance(geoFenceCenter) > geoFenceInnerRadius)){
						//Update center
						geoFenceCenter = new GeoPoint(location);
						geoFenceInnerRadius = (location.getSpeed() * 60)/1000; //in km
						if(geoFenceInnerRadius < 0.025){
							geoFenceInnerRadius = 0.025;
						}
						geoFenceOuterRadius = geoFenceInnerRadius *2;
						Double old = cost.get("Static Geofence");
						cost.put("Static Geofence", old + WAKEUP_COST +LOCATION_ANNOUNCE_COST);
					}
					
					//Check to see if any tweets came in
					if(tweets.containsKey(i)){
						GeoPoint tweet = tweets.get(i);
						//If the server knows I'm in range, wake me up and send it
						if(tweet.distance(geoFenceCenter) < geoFenceOuterRadius){
							Double old = cost.get("Static Geofence");
							cost.put("Static Geofence",old + WAKEUP_COST + TRANSMIT_COST);
						}
					}
				}
			}
			
			{
				cost.put("GeoJourney",0.0d);
				TreeMap<Long, GeoPoint> tweets = trial.getValue();
				GeoPoint geoFenceCenter = null;
				GeoPoint geoSpeed = null;
				double geoFenceInnerRadius = 0.0d;
				double geoFenceOuterRadius = 0.0d;
				
				for(long i=0; i < END ; i++){
					
					//Figure out where I am
					GeoPoint location = determineLocation(i,trace);
					
					//If I'm out of the dynamic geofence then update it with the server
					if((geoFenceCenter == null) || (location.distance(geoFenceCenter) > geoFenceInnerRadius)){
						//Update center
						geoFenceCenter = new GeoPoint(location);
						geoFenceInnerRadius = (location.getSpeed() * 60)/1000; //in km
						if(geoFenceInnerRadius < 0.025){
							geoFenceInnerRadius = 0.025;
						}
						geoFenceOuterRadius = geoFenceInnerRadius *2;
						
						//Figure out speed
						GeoPoint nextlocation = determineLocation(i+1,trace);
						//Not the right way to use a geopoint, but tevs
						geoSpeed = new GeoPoint((nextlocation.getLatitude()-location.getLatitude()),(nextlocation.getLongitude() - location.getLongitude()),0.0d);
						
						Double old = cost.get("GeoJourney");
						cost.put("GeoJourney", old + WAKEUP_COST +LOCATION_ANNOUNCE_COST);
					}
					else{
						//Else simulate the dynamic geofence
						geoFenceCenter.setLatitude(geoFenceCenter.getLatitude()+geoSpeed.getLatitude());
						geoFenceCenter.setLongitude(geoFenceCenter.getLongitude()+geoSpeed.getLongitude());
					}
					
					//Check to see if any tweets came in
					if(tweets.containsKey(i)){
						GeoPoint tweet = tweets.get(i);
						//If the server knows I'm in range, wake me up and send it
						if(tweet.distance(geoFenceCenter) < geoFenceOuterRadius){
							Double old = cost.get("GeoJourney");
							cost.put("GeoJourney",old + WAKEUP_COST + TRANSMIT_COST);
						}
					}
				}
			}
			
			System.out.print(trial.getKey()+"\t");
			System.out.print(cost.get("Naive Push")+"\t");
			System.out.print(cost.get("Smart Push")+"\t");
			System.out.print(cost.get("Naive Pull")+"\t");
			System.out.print(cost.get("Smart Pull")+"\t");
			System.out.print(cost.get("Static Geofence")+"\t");
			System.out.print(cost.get("GeoJourney")+"\t");
			System.out.println();
		}
		
		
		

	}



}
