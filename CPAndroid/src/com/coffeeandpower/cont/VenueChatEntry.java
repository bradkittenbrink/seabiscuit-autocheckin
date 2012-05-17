package com.coffeeandpower.cont;

public class VenueChatEntry {

	private String chatId;
	private String userId;
	private String entry;
	private String author;
	private String fileName;
	private String ip;
	private String date;
	private String systemType;

	private String systemData_userId;
	private String systemData_author;
	private String systemData_fineName;

	public VenueChatEntry(String chatId, String userId, String entry, String author, String fileName, String ip, String date, String systemType,
			String systemData_userId, String systemData_author, String systemData_fineName) {
		this.chatId = chatId;
		this.userId = userId;
		this.entry = entry;
		this.author = author;
		this.fileName = fileName;
		this.ip = ip;
		this.date = date;
		this.systemType = systemType;
		this.systemData_userId = systemData_userId;
		this.systemData_author = systemData_author;
		this.systemData_fineName = systemData_fineName;
	}

	public String getChatId() {
		return chatId;
	}

	public void setChatId(String chatId) {
		this.chatId = chatId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getEntry() {
		return entry;
	}

	public void setEntry(String entry) {
		this.entry = entry;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getSystemType() {
		return systemType;
	}

	public void setSystemType(String systemType) {
		this.systemType = systemType;
	}

	public String getSystemData_userId() {
		return systemData_userId;
	}

	public void setSystemData_userId(String systemData_userId) {
		this.systemData_userId = systemData_userId;
	}

	public String getSystemData_author() {
		return systemData_author;
	}

	public void setSystemData_author(String systemData_author) {
		this.systemData_author = systemData_author;
	}

	public String getSystemData_fineName() {
		return systemData_fineName;
	}

	public void setSystemData_fineName(String systemData_fineName) {
		this.systemData_fineName = systemData_fineName;
	}

}
