package com.coffeeandpower.cont;

import java.io.Serializable;

@SuppressWarnings("serial")
public class User implements Serializable {

	private int userId;
	private int favoriteEnabled;
	private int favoriteCount;
	private int myFavoriteCount;
	private int moneyReceived;
	private int offersPaid;
	private int balance;

	private String nickName;
	private String userName;
	private String statusText;
	private String status;
	private String active;
	private String photo;
	private String photoLarge;

	private double lat;
	private double lng;

	public User(int userId, int favoriteEnabled, int favoriteCount, int myFavoriteCount, int moneyReceived, int offersPaid, int balance,
			String nickName, String userName, String statusText, String status, String active, String photo, String photoLarge,
			double lat, double lng) {
		super();
		this.userId = userId;
		this.favoriteEnabled = favoriteEnabled;
		this.favoriteCount = favoriteCount;
		this.myFavoriteCount = myFavoriteCount;
		this.moneyReceived = moneyReceived;
		this.offersPaid = offersPaid;
		this.balance = balance;
		this.nickName = nickName;
		this.userName = userName;
		this.statusText = statusText;
		this.status = status;
		this.active = active;
		this.photo = photo;
		this.photoLarge = photoLarge;
		this.lat = lat;
		this.lng = lng;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getFavoriteEnabled() {
		return favoriteEnabled;
	}

	public void setFavoriteEnabled(int favoriteEnabled) {
		this.favoriteEnabled = favoriteEnabled;
	}

	public int getFavoriteCount() {
		return favoriteCount;
	}

	public void setFavoriteCount(int favoriteCount) {
		this.favoriteCount = favoriteCount;
	}

	public int getMyFavoriteCount() {
		return myFavoriteCount;
	}

	public void setMyFavoriteCount(int myFavoriteCount) {
		this.myFavoriteCount = myFavoriteCount;
	}

	public int getMoneyReceived() {
		return moneyReceived;
	}

	public void setMoneyReceived(int moneyReceived) {
		this.moneyReceived = moneyReceived;
	}

	public int getOffersPaid() {
		return offersPaid;
	}

	public void setOffersPaid(int offersPaid) {
		this.offersPaid = offersPaid;
	}

	public int getBalance() {
		return balance;
	}

	public void setBalance(int balance) {
		this.balance = balance;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getStatusText() {
		return statusText;
	}

	public void setStatusText(String statusText) {
		this.statusText = statusText;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getPhotoLarge() {
		return photoLarge;
	}

	public void setPhotoLarge(String photoLarge) {
		this.photoLarge = photoLarge;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

}
