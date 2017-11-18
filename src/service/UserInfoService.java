package service;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

import databaseAccess.DatabaseReader;
import databaseAccess.DatabaseWriter;
import model.User;

@Path("/user")
public class UserInfoService {
	
	private Gson gson;
	private DatabaseReader dbReader;
	private DatabaseWriter dbWriter;
	
	public UserInfoService(Gson gson, DatabaseReader dbReader, DatabaseWriter dbWriter) {
		this.gson = gson;
		this.dbReader = dbReader;
		this.dbWriter = dbWriter;
	}
	
	@GET
	@Path("id/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserInfoById(@PathParam("id") String id) {
		User user = dbReader.getUserById(id);
		
		if(user == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		
		return Response.ok(gson.toJson(user), MediaType.APPLICATION_JSON).build();
	}
	
	@GET
	@Path("email/{email: .*}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserInfoByEmail(@PathParam("email") String email) {
		User user = dbReader.getUserByEmail(email);
		
		if(user == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		
		return Response.ok(gson.toJson(user), MediaType.APPLICATION_JSON).build();
	}
	
	@GET
	@Path("rank/id/{id}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getUserRankById(@PathParam("id") String id) {
		User user = dbReader.getUserById(id);
		
		if(user == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		
		long rank = dbReader.getUserRank(user.getScore());
		
		return Response.ok("" + rank, MediaType.TEXT_PLAIN).build();
	}
	
	@GET
	@Path("rank/email/{email: .*}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getUserRankByEmail(@PathParam("email") String email) {
		User user = dbReader.getUserByEmail(email);
		
		if(user == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		
		long rank = dbReader.getUserRank(user.getScore());
		
		return Response.ok("" + rank, MediaType.TEXT_PLAIN).build();
	}
	
	@POST
	@Path("update/id/{id}/username/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateUserName(
			@PathParam("username") String editedName,
			@PathParam("id") String id) 
	{
		User user = dbReader.getUserById(id);
		
		if(user == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
				
		boolean success = dbWriter.editUserName(id, editedName);
		
		if(!success) {
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}
		
		user.setName(editedName);
		dbReader.updateUserToCache(user);
		
		return Response.ok(gson.toJson(user), MediaType.APPLICATION_JSON).build();
	}
	
	@POST
	@Path("update/id/{id}/password/{password: .*}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateUserPassword(
			@PathParam("password") String editedPassword,
			@PathParam("id") String id) 
	{
		User user = dbReader.getUserById(id);
		
		if(user == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
				
		boolean success = dbWriter.editUserPassword(id, editedPassword);
		
		if(!success) {
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}
		
		user.setPassword(editedPassword);
		dbReader.updateUserToCache(user);
		
		return Response.ok(gson.toJson(user), MediaType.APPLICATION_JSON).build();
	}
	
}
