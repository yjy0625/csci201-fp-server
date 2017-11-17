package model;

import java.io.Serializable;
import java.util.Date;

public class PostResponse implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String id;
	private long timestamp;
	private String userId;
	private String userName;
	private String placeId;
	private String placeName;
	private String postContent;
	private int numImages;
	private boolean isPublic;
	
	public PostResponse(String id, Date timestamp, String userId, String userName, String placeId, String placeName, String postContent, int numImages, boolean isPublic) {
		this.id = id;
		this.timestamp = timestamp.getTime();
		this.userId = userId;
		this.userName = userName;
		this.placeId = placeId;
		this.placeName = placeName;
		this.postContent = postContent;
		this.numImages = numImages;
		this.isPublic = isPublic;
	}
	
	public String getId() {
		return id;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public String getUserId() {
		return userId;
	}
	public String getUserName() {
		return userName;
	}
	public String getPlaceId() {
		return placeId;
	}
	public String getPlaceName() {
		return placeName;
	}
	public String getPostContent() {
		return postContent;
	}
	public int getNumImages() {
		return numImages;
	}
	public boolean isPublic() {
		return isPublic;
	}
	
}
