package model;

import java.io.Serializable;

public class Place implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String id;
	private String name;
	private double lat;
	private double lon;
	private int points;
	private String avatarUrl;
	private int numVisits;
	
	public String getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public double getLat() {
		return lat;
	}
	public double getLon() {
		return lon;
	}
	public int getPoints() {
		return points;
	}
	public String getAvatarUrl() {
		return avatarUrl;
	}
	public int getNumVisits() {
		return numVisits;
	}
	
	public void incNumOfVisit() {
		numVisits++;
	}
	
}
