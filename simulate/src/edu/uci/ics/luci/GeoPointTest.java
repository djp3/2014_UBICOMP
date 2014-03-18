package edu.uci.ics.luci;

import static org.junit.Assert.*;

import org.junit.Test;

public class GeoPointTest {
	final double e = 0.000001d;

	@Test
	public void test() {
		double latitude = 0.0d;
		double longitude = 0.0d;
		GeoPoint foo = new GeoPoint(latitude, longitude);
		assertTrue((foo.getLatitude() - latitude) < 0.00000001d);
		assertTrue((foo.getLongitude() - longitude) < 0.00000001d);
	}
	
	@Test
	public void testDistance() {
		double latitude = 0.0d;
		double longitude = 0.0d;
		GeoPoint foo = new GeoPoint(latitude, longitude);
		
		latitude = 3.0d;
		longitude = 4.0d;
		GeoPoint bar = new GeoPoint(latitude, longitude);
		assertTrue((foo.distance(bar) - 5.0d) < e);
	}

}
