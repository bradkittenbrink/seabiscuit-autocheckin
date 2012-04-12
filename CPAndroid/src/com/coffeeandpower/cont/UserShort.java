package com.coffeeandpower.cont;

public class UserShort {

	private int id;
	
	private String nickName;
	private String statusText;
	private String about;
	private String joinDate;
	private String imageURL;
	private String hourlyBilingRate;
	
	
	public UserShort(int id, String nickName, String statusText, String about,
			String joinDate, String imageURL, String hourlyBilingRate) {
		
		this.id = id;
		this.nickName = nickName;
		this.statusText = statusText;
		this.about = about;
		this.joinDate = joinDate;
		this.imageURL = imageURL;
		this.hourlyBilingRate = hourlyBilingRate;
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getNickName() {
		return nickName;
	}


	public void setNickName(String nickName) {
		this.nickName = nickName;
	}


	public String getStatusText() {
		return statusText;
	}


	public void setStatusText(String statusText) {
		this.statusText = statusText;
	}


	public String getAbout() {
		return about;
	}


	public void setAbout(String about) {
		this.about = about;
	}


	public String getJoinDate() {
		return joinDate;
	}


	public void setJoinDate(String joinDate) {
		this.joinDate = joinDate;
	}


	public String getImageURL() {
		return imageURL;
	}


	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}


	public String getHourlyBilingRate() {
		return hourlyBilingRate;
	}


	public void setHourlyBilingRate(String hourlyBilingRate) {
		this.hourlyBilingRate = hourlyBilingRate;
	}
	
	
	
}
