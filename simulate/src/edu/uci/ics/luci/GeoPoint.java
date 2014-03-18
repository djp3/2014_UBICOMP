package edu.uci.ics.luci;

public class GeoPoint {
	private double latitude;
	private double longitude;
	
	GeoPoint(double latitude, double longitude){
		setLatitude(latitude);
		setLongitude(longitude);
	}
	
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

}
