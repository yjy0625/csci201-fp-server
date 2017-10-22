package service;

import java.io.IOException;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import model.Post;

@ServerEndpoint(value = "/ws")
public class PostUpdateService implements Broadcaster {
	
	private Gson gson;
	
	private static Vector<Session> sessions = new Vector<>();
	
	private final Logger logger = LogManager.getLogger("WebSocket");
	
	public PostUpdateService(Gson gson) {
		this.gson = gson;
	}
	
	@OnOpen
	public void open(Session session) {
		logger.info("A connection is made.");
	}
	
	@OnMessage
	public void onMessage(String message, Session session) {
		logger.debug("Received a message from client but will not process it.");
	}
	
	@OnClose
	public void close(Session session) {
		logger.info("A client disconnected.");
		sessions.remove(session);
	}
	
	@OnError
	public void error(Throwable e) {
		logger.error("Error: " + e.getMessage());
	}
	
	public void broadcastPost(Post post) {
		AtomicInteger count = new AtomicInteger(0);
		
		try {
			for(Session s: sessions) {
				s.getBasicRemote().sendText(gson.toJson(post));
				count.incrementAndGet();
			}
		}
		catch(IOException ioe) {
			logger.error("An error occurred when broadcasting post {} to users: {}", post.getId(), ioe.getMessage());
		}
		
		logger.debug("Broadcast post {} to {} users.", post.getId(), count);
	}
	
}
