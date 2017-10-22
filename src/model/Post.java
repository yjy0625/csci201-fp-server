package model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Post implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String id;
	private Date timestamp;
	private String placeId;
	private String userId;
	private String postContent;
	private List<String> imageUrls;
	private boolean isPublic;
	
	public String getId() {
		return id;
	}
	
	public Date getTimestamp() {
		return timestamp;
	}
	public String getPlaceId() {
		return placeId;
	}
	public String getUserId() {
		return userId;
	}
	public String getPostContent() {
		return postContent;
	}
	public List<String> getImageUrls() {
		return imageUrls;
	}
	public boolean isPublic() {
		return isPublic;
	}
	
}
