package edu.uci.ics.luci;

import static org.junit.Assert.*;

import java.util.Map.Entry;
import java.util.TreeMap;

import org.junit.Test;

public class SimulatorTest {
	
	final double e = 0.001d;

	@Test
	public void test() {
		double hertz = 1.0;
		TreeMap<Long, GeoPoint> r = Simulator.makeRandomTweets(hertz);
		//System.out.println((Simulator.END*hertz)/1000.0d+" "+ r.size());
		assertTrue(Math.abs((Simulator.END*hertz)/1000.0d - r.size()) <= 2000);
		assertTrue((r.lastEntry().getKey() - r.firstEntry().getKey()) > (Simulator.END - 2*(1000.0/hertz)));
		
		hertz = 2.0;
		r = Simulator.makeRandomTweets(hertz);
		//System.out.println((Simulator.END*hertz)/1000.0d+" "+ r.size());
		assertTrue(Math.abs((Simulator.END*hertz)/1000.0d - r.size()) <= 2000);
		assertTrue((r.lastEntry().getKey() - r.firstEntry().getKey()) > (Simulator.END - 2*(1000.0/hertz)));
		
		hertz = 100.0;
		r = Simulator.makeRandomTweets(hertz);
		//System.out.println((Simulator.END*hertz)/1000.0d+" "+ r.size());
		assertTrue(Math.abs((Simulator.END*hertz)/1000.0d - r.size()) <= 2000);
		assertTrue((r.lastEntry().getKey() - r.firstEntry().getKey()) > (Simulator.END - 2*(1000.0/hertz)));
		
		for(Entry<Long, GeoPoint> e: r.entrySet()){
			assertTrue(e.getValue().getLatitude() > Simulator.bottom);
			assertTrue(e.getValue().getLatitude() < Simulator.top);
			assertTrue(e.getValue().getLongitude() > Simulator.left);
			assertTrue(e.getValue().getLongitude() < Simulator.right);
		}
	}
	
	@Test
	public void testDetermineLocation() {
		TreeMap<Long, GeoPoint> trace = new TreeMap<Long,GeoPoint>();;
		trace.put(0L, new GeoPoint(0.0d, 0.0d,1.0d));
		trace.put(10L, new GeoPoint(10.0d, 10.0d,1.0d));
		trace.put(20L, new GeoPoint(10.0d, 20.0d,1.0d));
		
		
		GeoPoint x = null;
		for(int i=0; i < 10 ; i++){
			x = Simulator.determineLocation(i, trace);
			//System.out.println("("+x.getLatitude()+","+x.getLongitude()+")");
			assertTrue(Math.abs(x.getLatitude() - (i+0.0d)) < e);
			assertTrue(Math.abs(x.getLongitude() - (i+0.0d)) < e);
		}
		for(int i=10; i < 20 ; i++){
			x = Simulator.determineLocation(i, trace);
			//System.out.println("("+x.getLatitude()+","+x.getLongitude()+")");
			assertTrue(Math.abs(x.getLatitude() - (10.0d)) < e);
			assertTrue(Math.abs(x.getLongitude() - (i+0.0d)) < e);
		}
		
	}

}
