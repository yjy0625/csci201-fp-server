package tests;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import databaseAccess.MongoDatabaseReader;
import model.Place;
import model.Post;
import model.PostResponse;
import model.User;

// Testing functionalities of database writer
public class MongoDatabaseReaderTest {
	
	private static MongoDatabaseReader dbReader;
	
	@BeforeClass
	public static void init() {
		dbReader = new MongoDatabaseReader();
	}
	
	@Test
	public void testGetUserWithIdSuccess() {
		User user = dbReader.getUserById("0");
		assertEquals(false, user == null);
		assertEquals("Tommy Trojan", user.getName());
		assertEquals(true, user.getEmail() != null);
		assertEquals(true, user.getId() != null);
	}
	
	@Test
	public void testGetUserWithIdFail() {
		User user = dbReader.getUserById("-");
		assertEquals(true, user == null);
	}
	
	@Test
	public void testGetUserWithEmailSuccess() {
		User user = dbReader.getUserByEmail("example@usc.edu");
		assertEquals(false, user == null);
		assertEquals("Tommy Trojan", user.getName());
		assertEquals(true, user.getEmail() != null);
		assertEquals(true, user.getId() != null);
	}
	
	@Test
	public void testGetUserWithEmailFail() {
		User user = dbReader.getUserByEmail("notfound@usc.edu");
		assertEquals(true, user == null);
	}
	
	@Test
	public void testGetUserRankFirst() {
		long rank = dbReader.getUserRank(1000);
		assertEquals(1, rank);
	}
	
	@Test
	public void testGetUserRankSecond() {
		long rank = dbReader.getUserRank(9);
		assertEquals(2, rank);
	}
	
	@Test
	public void testGetPlaceWithId() {
		Place place = dbReader.getPlaceById("0");
		assertEquals("Los Angeles", place.getName());
	}
	
	@Test
	public void testGetPlaceInAreaSuccess() {
		List<Place> placesInArea = dbReader.getPlacesInArea(33.0, 35.0, -120.0, -116.0);
		assertEquals(1, placesInArea.size());
		assertEquals("Los Angeles", placesInArea.get(0).getName());
	}
	
	@Test
	public void testGetPlaceInAreaLatNotInRange() {
		List<Place> placesInArea = dbReader.getPlacesInArea(33.0, 33.5, -120.0, -116.0);
		assertEquals(0, placesInArea.size());
	}
	
	@Test
	public void testGetPlaceInAreaLonNotInRange() {
		List<Place> placesInArea = dbReader.getPlacesInArea(33.0, 35.0, -120.0, -119.0);
		assertEquals(0, placesInArea.size());
	}
	
	@Test
	public void testGetPlacesVisitedByUserWithIdSuccess() {
		List<Place> placesVisited = dbReader.getPlacesVisitedByUserWithId("0");
		assertEquals(1, placesVisited.size());
		assertEquals("Los Angeles", placesVisited.get(0).getName());
	}
	
	@Test
	public void testGetPlacesVisitedByUserWithIdNoMatch() {
		List<Place> placesVisited = dbReader.getPlacesVisitedByUserWithId("1");
		assertEquals(0, placesVisited.size());
	}
	
	@Test
	public void testGetPosts() {
		List<PostResponse> posts = dbReader.getPosts();
		assertEquals(1, posts.size());
	}
	
	@Test
	public void testGetPostsWithLimit() {
		List<PostResponse> posts = dbReader.getPosts(1);
		assertEquals(1, posts.size());
	}
	
	@Test
	public void testGetPostsWithEndTime() {
		List<PostResponse> posts = dbReader.getPosts(new Date(2021), 0);
		assertEquals(1, posts.size());
	}
	
	@Test
	public void testGetPostsByUser() {
		List<PostResponse> posts = dbReader.getPosts("0", 0);
		assertEquals(2, posts.size());
	}
}
