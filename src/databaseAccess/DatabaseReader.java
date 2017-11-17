package databaseAccess;

import java.util.Date;
import java.util.List;

import model.*;

public interface DatabaseReader {

	public boolean signIn(String email, String password);

	public User getUserById(String id);

	public User getUserByEmail(String email);

	public long getUserRank(int score);

	public Place getPlaceById(String id);

	public List<Place> getPlacesInArea(double minLat, double maxLat, double minLon, double maxLon);

	public List<Place> getPlacesVisitedByUserWithId(String id);

	public List<PostResponse> getPosts();

	public List<PostResponse> getPosts(int maxLength);

	public List<PostResponse> getPosts(String id, int maxLength);

	public List<PostResponse> getPosts(Date endTime, int maxLength);

	public List<PostResponse> getPosts(String id, Date endTime, int maxLength);

	public void updateUserToCache(User user);

	public void updatePlaceToCache(Place place);
	
}
