package databaseAccess;

import model.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

// A database writer object
// It is an layer between the database and the data storage
// that provides support to all required database write operations
public class MongoDatabaseWriter implements DatabaseWriter {
	private static final String MONGO_CLIENT_URI = "mongodb://android_user:HaQKh4g2vAGGlOCA@cluster0-shard-00-00-s4zvl.mongodb.net:27017,cluster0-shard-00-01-s4zvl.mongodb.net:27017,cluster0-shard-00-02-s4zvl.mongodb.net:27017/test?ssl=true&replicaSet=Cluster0-shard-0&authSource=admin";
	private static final String DATABASE_NAME = "Application";
	private static final String USER_COLLECTION = "User";
	private static final String PLACE_COLLECTION = "Place";
	private static final String POST_COLLECTION = "Post";
	
	// database object
	private MongoDatabase database;
	
	// logger for the class
	private final Logger logger = LogManager.getLogger("DatabaseWriter");
	
	@SuppressWarnings("resource")
	public MongoDatabaseWriter() {
		MongoClientURI uri = new MongoClientURI(MONGO_CLIENT_URI);
		MongoClient mongoClient = new MongoClient(uri);
		this.database = mongoClient.getDatabase(DATABASE_NAME);
		logger.debug("Created DatabaseWriter instance.");
	}
	
	public boolean addUser(User user) {
		MongoCollection<Document> userCollection = database.getCollection(USER_COLLECTION); 
		if(userCollection.find(Filters.eq("id", user.getId())).first() != null) {
			logger.debug("Add user with email {} and name {} failed: user already exists.", user.getEmail(), user.getName());
			return false;
		}
		if(userCollection.find(Filters.eq("email", user.getEmail())).first() != null) {
			logger.debug("Add user with email {} and name {} failed: user already exists.", user.getEmail(), user.getName());
			return false;
		}
		Document newUserDocument = new Document()
										.append("id", user.getId())
										.append("email", user.getEmail())
										.append("name", user.getName())
										.append("password", user.getPassword())
										.append("score", user.getScore());
		userCollection.insertOne(newUserDocument);
		logger.debug("Added a user with email {} and name {}.", user.getEmail(), user.getName());
		return true;
	}
	
	public boolean editUserName(String id, String editedName) {
		MongoCollection<Document> userCollection = database.getCollection(USER_COLLECTION);
		Document updatedUserDocument = new Document()
										.append("name", editedName);
		userCollection.updateOne(Filters.eq("id", id), new Document("$set", updatedUserDocument));
		logger.debug("Edited a user with id {}: new name {}.", id, editedName);
		return true;
	}
	
	public boolean editUserPassword(String id, String editedPassword) {
		MongoCollection<Document> userCollection = database.getCollection(USER_COLLECTION);
		Document updatedUserDocument = new Document()
										.append("password", editedPassword);
		userCollection.updateOne(Filters.eq("id", id), new Document("$set", updatedUserDocument));
		logger.debug("Edited a user with id {}: new password {}.", id, editedPassword);
		return true;
	}
	
	public boolean addPost(Post post) {
		MongoCollection<Document> postCollection = database.getCollection(POST_COLLECTION);
		if(postCollection.find(Filters.eq("id", post.getId())).first() != null) {
			logger.error("Add post by user {} at place {} failed: duplicate id.", post.getUserId(), post.getPlaceId());
			return false;
		}
		Document newPostDocument = new Document()
										.append("id", post.getId())
										.append("timestamp", post.getTimestamp())
										.append("userId", post.getUserId())
										.append("placeId", post.getPlaceId())
										.append("postContent", post.getPostContent())
										.append("numImages", post.getNumImages())
										.append("isPublic", post.isPublic());
		postCollection.insertOne(newPostDocument);
		
		logger.debug("Added a post by user {} at place {}.", post.getUserId(), post.getPlaceId());
		return true;
	}
	
	public boolean incScoreToUser(String id, int score) {
		MongoCollection<Document> userCollection = database.getCollection(USER_COLLECTION);
		if(userCollection.find(Filters.eq("id", id)).first() == null) {
			logger.error("Increase score of user failed: user with id {} not found.", id);
			return false;
		}
		userCollection.updateOne(Filters.eq("id", id), Updates.inc("score", score));
		
		logger.debug("Increased score of user with id {} by {}", id, score);
		return true;
	}
	
	public boolean editPostVisibility(String id, boolean isPublic) {
		MongoCollection<Document> postCollection = database.getCollection(POST_COLLECTION);
		Document updatedPostDocument = new Document()
										.append("isPublic", isPublic);
		postCollection.updateOne(Filters.eq("id", id), new Document("$set", updatedPostDocument));
		logger.debug("Edited a post with id {}: changed visibility to {}.", id, (isPublic? "public": "private"));
		return true;
	}
	
	public boolean incPlaceNumberOfVisit(String id) {
		MongoCollection<Document> placeCollection = database.getCollection(PLACE_COLLECTION);
		placeCollection.updateOne(Filters.eq("id", id), Updates.inc("numVisits", 1));
		logger.debug("Increased visit count of place with id {}.", id);
		return true;
	}
}
