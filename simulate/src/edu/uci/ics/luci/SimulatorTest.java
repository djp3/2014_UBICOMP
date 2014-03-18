package edu.uci.ics.luci;

import static org.junit.Assert.*;

import java.util.Map.Entry;
import java.util.TreeMap;

import org.junit.Test;

public class SimulatorTest {
	
	final double e = 0.000001d;

	@Test
	public void test() {
		double hertz = 1.0;
		TreeMap<Double, GeoPoint> r = Simulator.makeRandomTweets(hertz);
		assertTrue((Simulator.END/(1000/hertz) - r.size()) < e);
		assertTrue((r.lastEntry().getKey() - r.firstEntry().getKey()) > (Simulator.END - 2*(1000.0/hertz)));
		
		hertz = 2.0;
		r = Simulator.makeRandomTweets(hertz);
		assertTrue((Simulator.END/(1000/hertz) - r.size()) < e);
		assertTrue((r.lastEntry().getKey() - r.firstEntry().getKey()) > (Simulator.END - 2*(1000.0/hertz)));
		
		r = Simulator.makeRandomTweets(50);
		assertTrue((Simulator.END/(1000/hertz) - r.size()) < e);
		assertTrue((r.lastEntry().getKey() - r.firstEntry().getKey()) > (Simulator.END - 2*(1000.0/hertz)));
		
		for(Entry<Double, GeoPoint> e: r.entrySet()){
			assertTrue(e.getValue().getLatitude() > Simulator.bottom);
			assertTrue(e.getValue().getLatitude() < Simulator.top);
			assertTrue(e.getValue().getLongitude() > Simulator.left);
			assertTrue(e.getValue().getLongitude() < Simulator.right);
		}
	}

}