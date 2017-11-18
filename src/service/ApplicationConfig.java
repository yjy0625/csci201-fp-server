package service;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.glassfish.jersey.media.multipart.MultiPartFeature;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;

import databaseAccess.DatabaseReader;
import databaseAccess.DatabaseWriter;
import databaseAccess.MongoDatabaseReader;
import databaseAccess.MongoDatabaseWriter;

@ApplicationPath("rest")
public class ApplicationConfig extends Application {
	private Set<Object> singletons = new HashSet<Object>();
	private Set<Class<?>> classes = new HashSet<Class<?>>();
	
	// initialization code here
	public ApplicationConfig() {
		System.out.println("test");
		classes.add(MultiPartFeature.class);
		
		Gson gson = new GsonBuilder()
				.registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (json, typeOfT, context) -> new Date(new Gson().fromJson(json, JsonObject.class).get("$date").getAsLong()))
				.create();
		
		DatabaseReader dbReader = new MongoDatabaseReader();
		DatabaseWriter dbWriter = new MongoDatabaseWriter();
		Broadcaster broadcaster = new PostUpdateService(gson);
		
		singletons.add(new SignUpService(gson, dbWriter));
		singletons.add(new SignInService(gson, dbReader));
		singletons.add(new UserInfoService(gson, dbReader, dbWriter));
		singletons.add(new MapInfoService(gson, dbReader));
		singletons.add(new AddPostService(gson, dbReader, dbWriter, broadcaster));
		singletons.add(new TimelineService(gson, dbReader));
		singletons.add(new FileService());
		
		System.out.println("Service started.");
	}
	
	@Override
	public Set<Class<?>> getClasses() {
		return classes;
	}
	
	@Override
	public Set<Object> getSingletons() {
		return singletons;
	}
}

