package databaseAccess;

import model.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.util.JSONParseException;

import redis.clients.jedis.Jedis;

// A database reader object
// It is an layer between the database and the data storage
// that provides support to all required database read operations
public class MongoDatabaseReader implements DatabaseReader {
	private static final String MONGO_CLIENT_URI = "mongodb://android_user:HaQKh4g2vAGGlOCA@cluster0-shard-00-00-s4zvl.mongodb.net:27017,cluster0-shard-00-01-s4zvl.mongodb.net:27017,cluster0-shard-00-02-s4zvl.mongodb.net:27017/test?ssl=true&replicaSet=Cluster0-shard-0&authSource=admin";
	private static final String DATABASE_NAME = "Application";
	private static final String USER_COLLECTION = "User";
	private static final String PLACE_COLLECTION = "Place";
	private static final String POST_COLLECTION = "Post";

	// database object
	private MongoDatabase database = null;

	// logger for the class
	private final Logger logger = LogManager.getLogger("DatabaseReader");

	// server cache
	private static final int TTL = 600;

	private Gson gson;

	// private constructor
	// prevent initialization elsewhere
	@SuppressWarnings("resource")
	public MongoDatabaseReader() {
		MongoClientURI uri = new MongoClientURI(MONGO_CLIENT_URI);
		MongoClient mongoClient = new MongoClient(uri);
		this.database = mongoClient.getDatabase(DATABASE_NAME);

		gson = new GsonBuilder()
				.registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (json, typeOfT, context) -> new Date(new Gson().fromJson(json, JsonObject.class).get("$date").getAsLong()))
				.create();

		logger.debug("Created DatabaseReader instance.");
	}

	public boolean signIn(String email, String password) {
		User user = getUserByEmail(email);

		// handle user not found error
		if(user == null) {
			logger.debug("Sign in failed: user with email {} doesn't exist.", email);
			return false;
		}

		logger.debug("Sign in succeeded: user with email {}.", email);
		return password.equals(user.getPassword());
	}

	public User getUserById(String id) {
		String userKeyId = "user/id/" + id;
		Jedis cache = new Jedis();
		String cachedUser = cache.get(userKeyId);
		cache.close();
		User user = null;

		// user not in cache
		if(cachedUser == null) {
			MongoCollection<Document> userCollection = database.getCollection(USER_COLLECTION);
			Document userDocument = userCollection.find(Filters.eq("id", id)).first();

			if(userDocument == null) {
				logger.debug("Get user information failed: user with id {} doesn't exist.", id);
				return null;
			}

			try {
				String jsonString = userDocument.toJson();
				Gson gson = new Gson();
				user = gson.fromJson(jsonString, User.class);
			}
			catch(JSONParseException jpe) {
				logger.error("Get user information failed: parsing user with id {} results in error {}.", id, jpe.getMessage());
				return null;
			}

			// add user information in cache
			cache = new Jedis();
			cache.set(userKeyId, gson.toJson(user));
			String userKeyEmail = "user/email/" + user.getEmail();
			cache.set(userKeyEmail, gson.toJson(user));
			cache.expire(userKeyEmail, TTL);
			cache.close();
		}
		else {
			user = gson.fromJson(cachedUser, User.class);
		}

		// update expiration time
		cache = new Jedis();
		cache.expire(userKeyId, TTL);
		cache.close();

		return user;
	}

	public User getUserByEmail(String email) {
		String userKeyEmail = "user/email/" + email;
		Jedis cache = new Jedis();
		String cachedUser = cache.get(userKeyEmail);
		cache.close();
		User user = null;

		// user not in cache
		if(cachedUser == null) {
			MongoCollection<Document> userCollection = database.getCollection(USER_COLLECTION);
			Document userDocument = userCollection.find(Filters.eq("email", email)).first();

			if(userDocument == null) {
				logger.debug("Get user information failed: user with email {} doesn't exist.", email);
				return null;
			}

			try {
				String jsonString = userDocument.toJson();
				Gson gson = new Gson();
				user = gson.fromJson(jsonString, User.class);
			}
			catch(JSONParseException jpe) {
				logger.error("Get user information failed: parsing user with email {} results in error {}.", email, jpe.getMessage());
				return null;
			}

			// add user information in cache
			cache = new Jedis();
			cache.set(userKeyEmail, gson.toJson(user));
			String userKeyId = "user/id/" + user.getId();
			cache.set(userKeyId, gson.toJson(user));
			cache.expire(userKeyId, TTL);
			cache.close();
		}
		else {
			user = gson.fromJson(cachedUser, User.class);
		}

		// update expiration time
		cache = new Jedis();
		cache.expire(userKeyEmail, TTL);
		cache.close();

		return user;
	}

	public long getUserRank(int score) {
		MongoCollection<Document> userCollection = database.getCollection(USER_COLLECTION);

		// count the number of users with score higher than the given score
		Document query = new Document("score", new Document("$gt", score));

		// add that value by one (e.g. if query results in 0 then rank is 1st place)
		long rank = userCollection.count(query) + 1;

		logger.debug("Got rank of user with score {}: {}.", score, rank);
		return rank;
	}

	public Place getPlaceById(String id) {
		String placeKey = "place/id/" + id;
		Jedis cache = new Jedis();
		String cachedPlace = cache.get(placeKey);
		cache.close();
		Place place = null;

		// user not in cache
		if(cachedPlace == null) {
			MongoCollection<Document> placeCollection = database.getCollection(PLACE_COLLECTION);
			Document placeDocument = placeCollection.find(Filters.eq("id", id)).first();

			if(placeDocument == null) {
				logger.debug("Get place information failed: place with id {} doesn't exist.", id);
				return null;
			}

			try {
				String jsonString = placeDocument.toJson();
				Gson gson = new Gson();
				place = gson.fromJson(jsonString, Place.class);
			}
			catch(JSONParseException jpe) {
				logger.error("Get place information failed: parsing place with id {} results in error {}.", id, jpe.getMessage());
				return null;
			}

			// add place information in cache
			cache = new Jedis();
			cache.set(placeKey, gson.toJson(place));
			cache.close();
		}
		else {
			place = gson.fromJson(cachedPlace, Place.class);
		}

		// update expiration time
		cache = new Jedis();
		cache.expire(placeKey, TTL);
		cache.close();
		
		return place;
	}

	public List<Place> getPlacesInArea(double minLat, double maxLat, double minLon, double maxLon) {
		MongoCollection<Document> placeCollection = database.getCollection(PLACE_COLLECTION);

		// define query result accumulation block
		List<Place> queryResult = new ArrayList<Place>();
		Block<Document> addSingleResult = new Block<Document>() {
			@Override
			public void apply(final Document placeDocument) {
				Place place = null;
				try {
					String jsonString = placeDocument.toJson();
					Gson gson = new Gson();
					place = gson.fromJson(jsonString, Place.class);
				}
				catch(JSONParseException jpe) {
					logger.error("Get places in area failed: parsing place with json string {} results in error {}.", placeDocument.toJson(), jpe.getMessage());
				}

				queryResult.add(place);
			}
		};

		// execute query and record results to list
		placeCollection.find(Filters.and(Filters.gt("lat", minLat), Filters.lt("lat", maxLat), Filters.gt("lon", minLon), Filters.lt("lon", maxLon)))
				.forEach(addSingleResult);

		logger.debug("Get places in area (lat between {} and {}; lon between {} and {}) returns {} results.", minLat, maxLat, minLon, maxLon, queryResult.size());
		return queryResult;
	}

	public List<Place> getPlacesVisitedByUserWithId(String id) {
		MongoCollection<Document> placeCollection = database.getCollection(PLACE_COLLECTION);
		List<Post> postsByUser = getPostsInternal(id, null, Integer.MAX_VALUE);
		List<String> places = postsByUser.stream().map(item -> item.getPlaceId()).collect(Collectors.toList());
		System.out.println("debug");
		for(String place: places) {
			System.out.println(place);
		}

		// define query result accumulation block
		List<Place> queryResult = new ArrayList<Place>();
		Block<Document> addSingleResult = new Block<Document>() {
			@Override
			public void apply(final Document placeDocument) {
				Place place = null;
				try {
					String jsonString = placeDocument.toJson();
					Gson gson = new Gson();
					place = gson.fromJson(jsonString, Place.class);
				}
				catch(JSONParseException jpe) {
					logger.error("Get places visited by user with id {} failed: parsing place with json string {} results in error {}.", id, placeDocument.toJson(), jpe.getMessage());
				}

				queryResult.add(place);
			}
		};

		// execute query and record results to list
		placeCollection.find(Filters.in("id", places))
				.forEach(addSingleResult);

		logger.debug("Get places visited by user with id {} returns {} results.", id, queryResult.size());
		return queryResult;
	}

	public List<PostResponse> getPosts() {
		return getPosts(null, null, Integer.MAX_VALUE);
	}

	public List<PostResponse> getPosts(int maxLength) {
		return getPosts(null, null, maxLength);
	}

	public List<PostResponse> getPosts(String id, int maxLength) {
		return getPosts(id, null, maxLength);
	}

	public List<PostResponse> getPosts(Date endTime, int maxLength) {
		return getPosts(null, endTime, maxLength);
	}
	
	public List<PostResponse> getPosts(String id, Date endTime, int maxLength) {
		return getPostResponseListFromPostList(getPostsInternal(id, endTime, maxLength));
	}

	private List<Post> getPostsInternal(String id, Date endTime, int maxLength) {
		MongoCollection<Document> postCollection = database.getCollection(POST_COLLECTION);

		// define query result accumulation block
		List<Post> queryResult = new ArrayList<Post>();
		Block<Document> addSingleResult = new Block<Document>() {
			@Override
			public void apply(final Document postDocument) {
				Post post = null;
				try {
					String jsonString = postDocument.toJson();
					Gson gson = new GsonBuilder()
							.registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (json, typeOfT, context) -> new Date(new Gson().fromJson(json, JsonObject.class).get("$date").getAsLong()))
							.create();
					post = gson.fromJson(jsonString, Post.class);
				}
				catch(JSONParseException jpe) {
					logger.error("Get posts in user failed: parsing post with json string {} results in error {}.", postDocument.toJson(), jpe.getMessage());
				}

				queryResult.add(post);
			}
		};

		// execute query and record results to list
		Bson filter;
		if(id == null && endTime == null) {
			filter = Filters.eq("isPublic", true);
		}
		else if(id == null) {
			filter = Filters.and(Filters.eq("isPublic", true), Filters.lt("timestamp", endTime));
		}
		else if(endTime == null) {
			filter = Filters.eq("userId", id);
		}
		else {
			filter = Filters.and(Filters.eq("userId", id), Filters.lt("timestamp", endTime));
		}

		postCollection.find(filter)
				.sort(Sorts.descending("timestamp"))
				.limit(maxLength)
				.forEach(addSingleResult);

		// logging
		if(id == null && endTime == null) {
			logger.debug("Get all public posts of all time with length limit {} returns {} results.", maxLength, queryResult.size());
		}
		else if(id == null) {
			logger.debug("Get all public posts before {} with length limit {} returns {} results.", endTime, maxLength, queryResult.size());
		}
		else if(endTime == null) {
			logger.debug("Get posts by user {} of all time with length limit {} returns {} results.", id, maxLength, queryResult.size());
		}
		else {
			logger.debug("Get posts by user {} before {} with length limit {} returns {} results.", id, endTime, maxLength, queryResult.size());
		}

		return queryResult;
	}
	
	private PostResponse getPostResponseFromPost(Post post) {
		String userName = getUserById(post.getUserId()).getName();
		String placeName = getPlaceById(post.getPlaceId()).getName();
		return new PostResponse(post.getId(), post.getTimestamp(), post.getUserId(), userName, post.getPlaceId(), placeName, post.getPostContent(), post.getNumImages(), post.isPublic());
	}
	
	private List<PostResponse> getPostResponseListFromPostList(List<Post> posts) {
		List<PostResponse> list = new ArrayList<PostResponse>();
		for(Post post: posts) {
			list.add(getPostResponseFromPost(post));
		}
		return list;
	}

	public void updateUserToCache(User user) {
		Jedis cache = new Jedis();
		String userKeyId = "user/id/" + user.getId();
		cache.set(userKeyId, gson.toJson(user));
		cache.expire(userKeyId, TTL);
		String userKeyEmail = "user/email/" + user.getEmail();
		cache.set(userKeyEmail, gson.toJson(user));
		cache.expire(userKeyEmail, TTL);
		cache.close();
	}
	
	public void updatePlaceToCache(Place place) {
		Jedis cache = new Jedis();
		String placeId = "place/id/" + place.getId();
		cache.set(placeId, gson.toJson(place));
		cache.expire(placeId, TTL);
		cache.close();
	}

}
