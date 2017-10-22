package service;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/file")
public class FileService {
	
	private static final String UPLOAD_FOLDER = "/user/yjy/FileServer/"; 
	private static final String USER_IMAGE_SUBFOLDER = "User/";
	
	public FileService() {
		
	}
	
	@POST
	@Path("image/upload/user/{id}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadUserAvatarImage(
			@PathParam("id") String id, 
			@FormDataParam("file") InputStream is,
			@FormDataParam("file") FormDataContentDisposition fileDetail) {
		final String folderPath = UPLOAD_FOLDER + USER_IMAGE_SUBFOLDER;
		
		if(is == null) {
			return Response.status(400)
					.entity("Invalid data")
					.build();
		}
		
		try {
			createFolderIfNotExists(folderPath);
		}
		catch(SecurityException se) {
			return Response.status(500)
					.entity("Can not create destination folder on server")
					.build();
		}
		
		String uploadLocation = folderPath + id + "-" + fileDetail.getFileName();
		try {
			saveToFile(is, uploadLocation);
		}
		catch(IOException ioe) {
			return Response.status(500).
					entity("Can not save file")
					.build();
		}
		
		return Response.status(200)
				.entity("File saved to " + uploadLocation)
				.build();
		
	}
	
	@GET
	@Path("image/download/user/{id}/filename/{filename}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response downloadUserAvatarImage(
			@PathParam("id") String id, 
			@PathParam("filename") String filename) 
	{
		final String filePath = UPLOAD_FOLDER + USER_IMAGE_SUBFOLDER + id + "-" + filename;
		
		File file = new File(filePath);
		
		return Response.ok((Object) file).header(
				"Content-Disposition", 
				"attachment; filename=" + filename
				).build();
		
	}
	
	private void saveToFile(InputStream is, String target) throws IOException {
		OutputStream os = null;
		int read = 0;
		byte[] bytes = new byte[1024];
		os = new FileOutputStream(new File(target));
		while ((read = is.read(bytes)) != -1) {
			os.write(bytes, 0, read);
		}
		os.flush();
		os.close();
	}
	
	private void createFolderIfNotExists(String directoryName) throws SecurityException {
		File directory = new File(directoryName);
		if (!directory.exists()) {
			directory.mkdir();
		}
	}
	
}
