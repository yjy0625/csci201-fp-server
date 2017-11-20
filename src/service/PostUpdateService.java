package service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import com.google.gson.Gson;

import model.PostResponse;

public class PostUpdateService extends Thread implements Broadcaster {
	
	private Gson gson;
	
	private SocketIOServer server;
	
	// logger for the class
	private final Logger logger = LogManager.getLogger("Socket");
	
	public PostUpdateService(Gson gson) {
		this.gson = gson;
		
		Configuration config = new Configuration();
        config.setHostname("localhost");
        config.setPort(8081);

        server = new SocketIOServer(config);
        
        server.addConnectListener( (client) -> {
        		System.out.println("A new client has Connected!");
        });
        
        this.start();
	}
	
	public void run() {
		try {
			server.start();
	        
	        System.out.println("Socket started");
	        
	        Thread.sleep(Integer.MAX_VALUE);
	
	        server.stop();
		}
		catch(InterruptedException ie) {
			logger.error("Got interrupted exception: " + ie.getMessage());
		}
	}
	
	public void broadcastPost(PostResponse postResponse) {
		System.out.println("Broadcasting to " + server.getAllClients().size() + " users");
		server.getBroadcastOperations().sendEvent("update", gson.toJson(postResponse));
		logger.info("Broadcast new post {} to {} users", postResponse.getId(), server.getAllClients().size());
	}
	
}
