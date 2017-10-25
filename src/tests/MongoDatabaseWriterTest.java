package tests;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.*;

import databaseAccess.MongoDatabaseWriter;
import model.Post;
import model.User;

// Testing functionalities of database writer
// Note that the User table in MongoDB must have no entry with id 1 and 1 entry with id 0
// Otherwise test cases will fail to return the correct result
public class MongoDatabaseWriterTest {
	
	private static MongoDatabaseWriter dbWriter;
	
	@BeforeClass
	public static void init() {
		dbWriter = new MongoDatabaseWriter();
	}

	@Test
	public void testAddUserSuccess() {
		Gson gson = new Gson();
		User mockUser = gson.fromJson("{\"id\":\"1\",\"email\":\"tommy.trojan@usc.edu\",\"name\":\"Tommy Trojan\",\"password\":\"asdf\",\"score\":0}", User.class);
		
		boolean succeeded = dbWriter.addUser(mockUser);
		assertEquals(true, succeeded);
	}
	
	@Test
	public void testAddUserFail() {
		Gson gson = new Gson();
		User mockUser = gson.fromJson("{\"id\":\"0\",\"email\":\"tommy.trojan@usc.edu\",\"name\":\"Tommy Trojan\",\"password\":\"asdf\",\"score\":0}", User.class);
		
		boolean succeeded = dbWriter.addUser(mockUser);
		assertEquals(false, succeeded);
	}
	
	@Test
	public void testEditUserName() {
		boolean succeeded = dbWriter.editUserName("0", "Tommy Trojan");
		assertEquals(true, succeeded);
	}
	
	@Test 
	public void testEditUserPassword() {
		boolean succeeded = dbWriter.editUserPassword("0", "asdfhjkl");
		assertEquals(true, succeeded);
	}
	
	@Test
	public void testAddPost() {
		Gson gson = new GsonBuilder()
				.registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (json, typeOfT, context) -> new Date(new Gson().fromJson(json, JsonObject.class).get("$date").getAsLong()))
				.create();
		
		Post mockPost = gson.fromJson("{\"id\":\"0\",\"timestamp\":{\"$date\":2020},\"placeId\":\"0\",\"userId\":\"0\",\"postContent\":\"Hello world!\",\"numImages\":1,\"isPublic\":true}", Post.class);
		
		boolean succeeded = dbWriter.addPost(mockPost);
		assertEquals(true, succeeded);
	}
	
	@Test
	public void testIncreaseScoreToUser() {
		boolean succeeded = dbWriter.incScoreToUser("0", 10);
		assertEquals(true, succeeded);
	}
	
	@Test
	public void testIncreaseScoreToUserFail() {
		boolean succeeded = dbWriter.incScoreToUser("1984", 10);
		assertEquals(false, succeeded);
	}
	
	@Test
	public void testEditPostVisibility() {
		boolean succeeded = dbWriter.editPostVisibility("1", false);
		assertEquals(true, succeeded);
	}
	
	@Test
	public void testIncPlaceNumberOfVisits() {
		boolean succeeded = dbWriter.incPlaceNumberOfVisit("0");
		assertEquals(true, succeeded);
	}
	
}
