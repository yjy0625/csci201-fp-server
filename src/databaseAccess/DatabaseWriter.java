package databaseAccess;

import model.*;

public interface DatabaseWriter {
	
	public boolean addUser(User user);
	
	public boolean editUserName(String id, String editedName);
	
	public boolean editUserPassword(String id, String editedPassword);
	
	public boolean addPost(Post post);
	
	public boolean incScoreToUser(String id, int score);
	
	public boolean editPostVisibility(String id, boolean isPublic);
	
	public boolean incPlaceNumberOfVisit(String id);

}
