package service;

import model.PostResponse;

public interface Broadcaster {
	public void broadcastPost(PostResponse postResponse);
}
