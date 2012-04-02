package com.coffeeandpower.cont;

import java.io.Serializable;

public class User implements Serializable{

	private int userId;
	
	private String nickName;

	public User(int userId, String nickName) {

		this.userId = userId;
		this.nickName = nickName;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	
	
}
