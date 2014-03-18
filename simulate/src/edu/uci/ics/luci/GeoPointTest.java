package edu.uci.ics.luci;

import static org.junit.Assert.*;

import org.junit.Test;

public class GeoPointTest {

	@Test
	public void test() {
		double latitude = 0.0d;
		double longitude = 0.0d;
		GeoPoint foo = new GeoPoint(latitude, longitude);
		assertTrue((foo.getLatitude() - latitude) < 0.00000001d);
		assertTrue((foo.getLongitude() - longitude) < 0.00000001d);
		
	}

}
