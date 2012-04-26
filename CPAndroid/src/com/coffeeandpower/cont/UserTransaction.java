package com.coffeeandpower.cont;

import java.util.ArrayList;

public class UserTransaction {

	private int userId;
	private String nickName;
	private String userName;
	private String statusText;
	private String userStatus;
	private String active;
	private String photo;
	private String photoLarge;
	
	private double lat;
	private double lng;
	
	private int favoriteEnabled;
	private int favoriteCount;
	private int myFavoriteCount;
	private int moneyReceived;
	private int offersPaid;
	private int balance;
	
	private ArrayList<Transaction> transactions;

	
	public UserTransaction(int userId, String nickName, String userName,
			String statusText, String userStatus, String active, String photo,
			String photoLarge, double lat, double lng, int favoriteEnabled,
			int favoriteCount, int myFavoriteCount, int moneyReceived,
			int offersPaid, int balance, ArrayList<Transaction> transactions) {
		
		this.userId = userId;
		this.nickName = nickName;
		this.userName = userName;
		this.statusText = statusText;
		this.userStatus = userStatus;
		this.active = active;
		this.photo = photo;
		this.photoLarge = photoLarge;
		this.lat = lat;
		this.lng = lng;
		this.favoriteEnabled = favoriteEnabled;
		this.favoriteCount = favoriteCount;
		this.myFavoriteCount = myFavoriteCount;
		this.moneyReceived = moneyReceived;
		this.offersPaid = offersPaid;
		this.balance = balance;
		this.transactions = transactions;
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

	public String getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
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

	public ArrayList<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(ArrayList<Transaction> transactions) {
		this.transactions = transactions;
	}
	
	
}
