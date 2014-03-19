package edu.uci.ics.luci;

import static org.junit.Assert.*;

import org.junit.Test;

public class GeoPointTest {
	final double e = 0.000001d;

	@Test
	public void test() {
		double latitude = 0.0d;
		double longitude = 0.0d;
		double speed = 0.0d;
		GeoPoint foo = new GeoPoint(latitude, longitude,speed);
		assertTrue(Math.abs(foo.getLatitude() - latitude) < 0.00000001d);
		assertTrue(Math.abs(foo.getLongitude() - longitude) < 0.00000001d);
		assertTrue(Math.abs(foo.getSpeed() - speed) < 0.00000001d);
	}
	
	@Test
	public void testDistance() {
		double latitude = 33.634380715792391253d;
		double longitude = -117.82716751098633d; 
		double speed = 0.0d;
		GeoPoint foo = new GeoPoint(latitude, longitude,speed);
		
		latitude = 33.63034303571295d;
		longitude = -117.81455039978027d;
		GeoPoint bar = new GeoPoint(latitude, longitude,speed);
		//System.out.println(""+foo.distance(bar));
		assertTrue(Math.abs(foo.distance(bar) - 1.251365909d) < e);
	}

}
