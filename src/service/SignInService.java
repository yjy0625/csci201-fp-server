package service;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

import databaseAccess.DatabaseReader;
import model.User;

@Path("/signin")
public class SignInService {
	
	private Gson gson;
	private DatabaseReader dbReader;
	
	public SignInService(Gson gson, DatabaseReader dbReader) {
		this.gson = gson;
		this.dbReader = dbReader;
	}
	
	@POST
	@Path("email/{email: .*}/password/{password: .*}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response signin(@PathParam("email") String email, @PathParam("password") String password) {
		User user = dbReader.getUserByEmail(email);
		
		if(user == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		
		boolean success = user.getPassword().equals(password);
		
		if(!success) {
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}
		
		return Response.ok(gson.toJson(user), MediaType.APPLICATION_JSON).build();
	}
	
}
