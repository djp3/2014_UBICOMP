package edu.uci.ics.luci;

public class GeoPoint {
	private double latitude;
	private double longitude;
	private double speed;
	
	GeoPoint(double latitude, double longitude, double speed){
		setLatitude(latitude);
		setLongitude(longitude);
		setSpeed(speed);
	}
	
	GeoPoint(GeoPoint cloneMe){
		setLatitude(cloneMe.getLatitude());
		setLongitude(cloneMe.getLongitude());
		setSpeed(cloneMe.getSpeed());
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
	
	public double getSpeed() {
		return speed;
	}
	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public double distance(GeoPoint p) {
		return _distance(this.getLatitude(),this.getLongitude(),p.getLatitude(),p.getLongitude(),'K');
	}
	
	static double _distance(double lat1, double lon1, double lat2, double lon2, char unit) {
		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;
		if (unit == 'K') {
			dist = dist * 1.609344;
		} else if (unit == 'N') {
			dist = dist * 0.8684;
		}
		return (dist);
	}

	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*::  This function converts decimal degrees to radians             :*/
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	static double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*::  This function converts radians to decimal degrees             :*/
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	static double rad2deg(double rad) {
		return (rad * 180.0 / Math.PI);
	}

}
