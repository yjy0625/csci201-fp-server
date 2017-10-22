package service;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

import databaseAccess.DatabaseReader;
import model.Place;

@Path("/map")
public class MapInfoService {
	
	private Gson gson;
	private DatabaseReader dbReader;
	
	public MapInfoService(Gson gson, DatabaseReader dbReader) {
		this.gson = gson;
		this.dbReader = dbReader;
	}
	
	@GET
	@Path("inArea/latBetween/{minLat}/and/{maxLat}/lonBetween/{minLon}/and/{maxLon}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMapInfoByArea(
			@PathParam("minLat") double minLat, 
			@PathParam("maxLat") double maxLat, 
			@PathParam("minLon") double minLon, 
			@PathParam("maxLon") double maxLon) 
	{
		List<Place> places = dbReader.getPlacesInArea(minLat, maxLat, minLon, maxLon);
		
		return Response.ok(gson.toJson(places), MediaType.APPLICATION_JSON).build();
	}
	
	@GET
	@Path("placesVisitedByUser/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMapInfoByUser(@PathParam("id") String id) {
		 List<Place> places = dbReader.getPlacesVisitedByUserWithId(id);
		 
		 return Response.ok(gson.toJson(places), MediaType.APPLICATION_JSON).build();
	}
	
	@GET
	@Path("id/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMapInfoById(@PathParam("id") String id) {
		Place place = dbReader.getPlaceById(id);
		
		if(place == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		
		return Response.ok(gson.toJson(place), MediaType.APPLICATION_JSON).build();
	}
	
}
