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

	public double distance(GeoPoint p) {
		double y = this.getLatitude() - p.getLatitude(); 
		double x = this.getLongitude() - p.getLongitude(); 
		return Math.sqrt(x*x + y*y);
	}

}
