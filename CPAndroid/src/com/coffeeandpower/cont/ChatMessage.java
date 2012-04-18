package com.coffeeandpower.cont;

public class ChatMessage {

	
	private int id;
	private int userId;
	private String entryText;
	private String nickName;
	private String date;
	private String photoUrl;
	private int receivingUserId;
	private int offerId;
	
	public ChatMessage(int id, int userId, String entryText, String nickName,
			String date, String photoUrl, int receivingUserId, int offerId) {

		this.id = id;
		this.userId = userId;
		this.entryText = entryText;
		this.nickName = nickName;
		this.date = date;
		this.photoUrl = photoUrl;
		this.receivingUserId = receivingUserId;
		this.offerId = offerId;
	}
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getEntryText() {
		return entryText;
	}
	public void setEntryText(String entryText) {
		this.entryText = entryText;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getPhotoUrl() {
		return photoUrl;
	}
	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}
	public int getReceivingUserId() {
		return receivingUserId;
	}
	public void setReceivingUserId(int receivingUserId) {
		this.receivingUserId = receivingUserId;
	}
	public int getOfferId() {
		return offerId;
	}
	public void setOfferId(int offerId) {
		this.offerId = offerId;
	}
	
	
}
