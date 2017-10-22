package service;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

import databaseAccess.DatabaseWriter;
import model.User;

@Path("/signup")
public class SignUpService {
	
	private Gson gson;
	private DatabaseWriter dbWriter;
	
	public SignUpService(Gson gson, DatabaseWriter dbWriter) {
		this.gson = gson;
		this.dbWriter = dbWriter;
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response signup(String json) {
		User user = gson.fromJson(json, User.class);
		boolean success = dbWriter.addUser(user);
		
		if(!success) {
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}
		
		return Response.ok(json, MediaType.APPLICATION_JSON).build();
	}
	
}
