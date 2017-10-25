package service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("/file")
public class FileService {
	
	private static final int MAX_SIZE_IN_MB = 2;
	
	private static final java.nio.file.Path USER_IMAGE_DIR = Paths.get(System.getProperty("user.home"), "FileServer", "User");
	private static final java.nio.file.Path POST_IMAGE_DIR = Paths.get(System.getProperty("user.home"), "FileServer", "Post");
	
	public FileService() {
		
	}
	
	@POST
	@Path("image/upload/user/{id}")
	@Consumes({"image/jpeg", "image/png"})
	public Response uploadUserAvatarImage(
			@PathParam("id") String id, 
			InputStream is, 
			@HeaderParam("Content-Type") String fileType, 
			@HeaderParam("Content-Length") long fileSize) {
		
		// throw error if there is no image input
		if(is == null) {
			return Response.status(400)
					.entity("Invalid data")
					.build();
		}
		
		// throw error if image size too large
		if (fileSize > 1024 * 1024 * MAX_SIZE_IN_MB) {
            throw new WebApplicationException(Response.status(Status.BAD_REQUEST).entity("Image is larger than " + MAX_SIZE_IN_MB + "MB").build());
        }
		
		// construct image file name 
		String filename = id;
		
		if (fileType.equals("image/jpeg")) {
			filename += ".jpg";
        } else {
        		filename += ".png";
        }
		
		// save file
		try {
			Files.copy(is, USER_IMAGE_DIR.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
		}
		catch(IOException ioe) {
			return Response.status(500).
					entity("Cannot save file")
					.build();
		}
		
		return Response.status(200).entity("File saved successfully.").build();
		
	}
	
	@GET
	@Path("image/download/user/{id}")
	@Produces({"image/jpeg", "image/png"})
	public Response downloadUserAvatarImage(@PathParam("id") String id) {
		
		// guess file type
		String fileType = "jpeg";
		
		java.nio.file.Path dest = USER_IMAGE_DIR.resolve(id + ".jpg");
		if(!Files.exists(dest)) {
			dest = USER_IMAGE_DIR.resolve(id + ".png");
			fileType = "png";
		}
		
		// throw error if neither JPG nor PNG file is found
		if(!Files.exists(dest)) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		
		// construct response
		Response response = null;
		
		try {
			response = Response.ok(Files.newInputStream(dest), "image/" + fileType)
					.build();
		}
		catch(IOException ioe) {
			response = Response.status(500).
					entity("Cannot read file")
					.build();
			return response;
		}
		
		return response;
	}
	
	@POST
	@Path("image/upload/post/{id}/index/{index}")
	@Consumes({"image/jpeg", "image/png"})
	public Response uploadPostImage(
			@PathParam("id") String id, 
			@PathParam("index") int index,
			InputStream is, 
			@HeaderParam("Content-Type") String fileType, 
			@HeaderParam("Content-Length") long fileSize) {
		
		// throw error if there is no image input
		if(is == null) {
			return Response.status(400)
					.entity("Invalid data")
					.build();
		}
		
		// throw error if image size too large
		if (fileSize > 1024 * 1024 * MAX_SIZE_IN_MB) {
            throw new WebApplicationException(Response.status(Status.BAD_REQUEST).entity("Image is larger than " + MAX_SIZE_IN_MB + "MB").build());
        }
		
		// construct image file name 
		String filename = id + "-" + index;
		
		if (fileType.equals("image/jpeg")) {
			filename += ".jpg";
        } else {
        		filename += ".png";
        }
		
		// save file
		try {
			Files.copy(is, POST_IMAGE_DIR.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
		}
		catch(IOException ioe) {
			return Response.status(500).
					entity("Cannot save file")
					.build();
		}
		
		return Response.status(200).entity("File saved successfully.").build();
		
	}
	
	@GET
	@Path("image/download/post/{id}/index/{index}")
	@Produces({"image/jpeg", "image/png"})
	public Response downloadPostImage(@PathParam("id") String id, @PathParam("index") int index) {
			
		// guess file type
		String fileType = "jpeg";
		
		java.nio.file.Path dest = POST_IMAGE_DIR.resolve(id + "-" + index + ".jpg");
		if(!Files.exists(dest)) {
			dest = POST_IMAGE_DIR.resolve(id + "-" + index + ".png");
			fileType = "png";
		}
		
		// throw error if neither JPG nor PNG file is found
		if(!Files.exists(dest)) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		
		// construct response
		Response response = null;
		
		try {
			response = Response.ok(Files.newInputStream(dest), "image/" + fileType)
					.build();
		}
		catch(IOException ioe) {
			response = Response.status(500).
					entity("Cannot read file")
					.build();
			return response;
		}
		
		return response;
			
	}
	
}
