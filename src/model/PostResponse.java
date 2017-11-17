package model;

import java.io.Serializable;
import java.util.Date;

public class PostResponse implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String id;
	private long timestamp;
	private String userName;
	private String placeName;
	private int numImages;
	private boolean isPublic;
	
	public PostResponse(String id, Date timestamp, String userName, String placeName, int numImages, boolean isPublic) {
		this.id = id;
		this.timestamp = timestamp.getTime();
		this.userName = userName;
		this.placeName = placeName;
		this.numImages = numImages;
		this.isPublic = isPublic;
	}
	
	public String getId() {
		return id;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public String getUserName() {
		return userName;
	}
	public String getPlaceName() {
		return placeName;
	}
	public int getNumImages() {
		return numImages;
	}
	public boolean isPublic() {
		return isPublic;
	}
	
}
