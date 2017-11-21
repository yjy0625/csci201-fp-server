package service;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

import databaseAccess.DatabaseReader;
import databaseAccess.DatabaseWriter;
import model.Place;
import model.Post;
import model.PostResponse;
import model.User;

@Path("/post")
public class AddPostService {

	private Gson gson;
	private DatabaseReader dbReader;
	private DatabaseWriter dbWriter;
	private Broadcaster broadcaster;

	public AddPostService(
			Gson gson,
			DatabaseReader dbReader,
			DatabaseWriter dbWriter,
			Broadcaster broadcaster)
	{
		this.gson = gson;
		this.dbReader = dbReader;
		this.dbWriter = dbWriter;
		this.broadcaster = broadcaster;
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response addPost(String json) {
		Post post = gson.fromJson(json, Post.class);
		boolean success = dbWriter.addPost(post);

		if(!success) {
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}

		User user = dbReader.getUserById(post.getUserId());
		Place place = dbReader.getPlaceById(post.getPlaceId());
		
		success = success && dbWriter.incScoreToUser(user.getId(), place.getPoints());
		success = success && dbWriter.incPlaceNumberOfVisit(place.getId());

		if(!success) {
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}

		user.incScore(place.getPoints());
		dbReader.updateUserToCache(user);

		place.incNumOfVisit();
		dbReader.updatePlaceToCache(place);
		
		PostResponse postResponse = new PostResponse(post.getId(), post.getTimestamp(), post.getUserId(), user.getName(), post.getPlaceId(), place.getName(), post.getPostContent(), post.getNumImages(), post.isPublic());
		
		if(post.isPublic()) {
			broadcaster.broadcastPost(postResponse);
		}

		return Response.status(200).entity("Post added successfully.").build();
	}

}
