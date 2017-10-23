package service;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

import databaseAccess.DatabaseReader;
import databaseAccess.DatabaseWriter;
import model.Place;
import model.Post;
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
	public Response addPost(String json) {
		Post post = gson.fromJson(json, Post.class);
		boolean success = dbWriter.addPost(post);

		if(!success) {
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}

		User user = dbReader.getUserById(post.getUserId());
		Place place = dbReader.getPlaceById(post.getPlaceId());
		success = dbWriter.incScoreToUser(user.getId(), place.getPoints());

		if(!success) {
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}

		user.incScore(place.getPoints());
		dbReader.updateUserToCache(user);

		place.incNumOfVisit();
		dbReader.updatePlaceToCache(place);

		broadcaster.broadcastPost(post);

		return Response.ok().build();
	}

}
