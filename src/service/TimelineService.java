package service;

import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

import databaseAccess.DatabaseReader;
import model.PostResponse;

@Path("/timeline")
public class TimelineService {
	
	private Gson gson;
	private DatabaseReader dbReader;
	
	public TimelineService(Gson gson, DatabaseReader dbReader) {
		this.gson = gson;
		this.dbReader = dbReader;
	}
	
	@GET
	@Path("everything")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTimelineWithAllPublicPosts() {
		List<PostResponse> posts = dbReader.getPosts();
		return Response.ok(gson.toJson(posts), MediaType.APPLICATION_JSON).build();
	}
	
	@GET
	@Path("maxLength/{maxLength}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response geteTimelineWithPublicPostsWithinLengthLimit(
			@PathParam("maxLength") int maxLength)
	{
		List<PostResponse> posts = dbReader.getPosts(maxLength);
		return Response.ok(gson.toJson(posts), MediaType.APPLICATION_JSON).build();
	}
	
	@GET
	@Path("by/{endTime}/maxLength/{maxLength}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTimelineWithPublicPostsWithinTimeAndLengthLimit(
			@PathParam("endTime") long endTimeLong,
			@PathParam("maxLength") int maxLength) 
	{
		Date endTime = new Date(endTimeLong);
		List<PostResponse> posts = dbReader.getPosts(endTime, maxLength);
		return Response.ok(gson.toJson(posts), MediaType.APPLICATION_JSON).build();
	}
	
	@GET
	@Path("user/{id}/maxLength/{maxLength}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTimelineForUserWithinLengthLimit(
			@PathParam("id") String id,
			@PathParam("maxLength") int maxLength) 
	{
		List<PostResponse> posts = dbReader.getPosts(id, maxLength);
		return Response.ok(gson.toJson(posts), MediaType.APPLICATION_JSON).build();
	}
	
	@GET
	@Path("user/{id}/by/{endTime}/maxLength/{maxLength}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTimelineForUserWithinTimeAndLengthLimit(
			@PathParam("id") String id,
			@PathParam("endTime") long endTimeLong,
			@PathParam("maxLength") int maxLength) 
	{
		Date endTime = new Date(endTimeLong);
		List<PostResponse> posts = dbReader.getPosts(id, endTime, maxLength);
		return Response.ok(gson.toJson(posts), MediaType.APPLICATION_JSON).build();
	}
	
}
