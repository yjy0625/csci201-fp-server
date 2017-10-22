package model;

import java.io.Serializable;

public class User implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String id;
	private String email;
	private String name;
	private String password;
	private String avatarUrl;
	private int score;
	
	public String getId() {
		return id;
	}
	
	public String getEmail() {
		return email;
	}

	public String getName() {
		return name;
	}
	
	public String getPassword() {
		return password;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public int getScore() {
		return score;
	}
	
	public void setName(String newName) {
		name = newName;
	}
	
	public void setPassword(String newPassword) {
		password = newPassword;
	}
	
	public void incScore(int score) {
		this.score += score;
	}
}
