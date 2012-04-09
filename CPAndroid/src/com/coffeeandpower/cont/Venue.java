package com.coffeeandpower.cont;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Venue implements Serializable{

	private String id;
	private String name;
	private String address;
	private String crossStreet;

	private double lat;
	private double lng;

	private int distance;

	private String postalCode;
	private String city;
	private String state;
	private String country;

	private String categoryName;
	private String categoryPluralName;
	private String categoryShortName;
	private String phone;
	private String photoUrl;

	private int checkinsCount;
	private int usersCount;
	private int tipCount;
	private int hereNowCount;

	/**
	 * Create empty venue obj
	 */
	public Venue(){
		this("","","","",0,0,0,"","","","","","","",0,0,0,0,"","");
	}
	
	/**
	 * Venue constructor
	 * @param id
	 * @param name
	 * @param address
	 * @param crossStreet
	 * @param lat
	 * @param lng
	 * @param distance
	 * @param postalCode
	 * @param city
	 * @param state
	 * @param country
	 * @param categoryName
	 * @param categoryPluralName
	 * @param categoryShortName
	 * @param checkinsCount
	 * @param usersCount
	 * @param tipCount
	 * @param hereNowCount
	 */
	public Venue(String id, String name, String address, String crossStreet,
			double lat, double lng, int distance, String postalCode,
			String city, String state, String country, String categoryName,
			String categoryPluralName, String categoryShortName,
			int checkinsCount, int usersCount, int tipCount, int hereNowCount, String phone, String photoUrl) {

		this.id = id;
		this.name = name;
		this.address = address;
		this.crossStreet = crossStreet;
		this.lat = lat;
		this.lng = lng;
		this.distance = distance;
		this.postalCode = postalCode;
		this.city = city;
		this.state = state;
		this.country = country;
		this.categoryName = categoryName;
		this.categoryPluralName = categoryPluralName;
		this.categoryShortName = categoryShortName;
		this.checkinsCount = checkinsCount;
		this.usersCount = usersCount;
		this.tipCount = tipCount;
		this.hereNowCount = hereNowCount;
		this.phone = phone;
		this.photoUrl = photoUrl;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCrossStreet() {
		return crossStreet;
	}

	public void setCrossStreet(String crossStreet) {
		this.crossStreet = crossStreet;
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

	/**
	 * Returns distance in meters
	 * @return
	 */
	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getCategoryPluralName() {
		return categoryPluralName;
	}

	public void setCategoryPluralName(String categoryPluralName) {
		this.categoryPluralName = categoryPluralName;
	}

	public String getCategoryShortName() {
		return categoryShortName;
	}

	public void setCategoryShortName(String categoryShortName) {
		this.categoryShortName = categoryShortName;
	}

	public int getCheckinsCount() {
		return checkinsCount;
	}

	public void setCheckinsCount(int checkinsCount) {
		this.checkinsCount = checkinsCount;
	}

	public int getUsersCount() {
		return usersCount;
	}

	public void setUsersCount(int usersCount) {
		this.usersCount = usersCount;
	}

	public int getTipCount() {
		return tipCount;
	}

	public void setTipCount(int tipCount) {
		this.tipCount = tipCount;
	}

	public int getHereNowCount() {
		return hereNowCount;
	}

	public void setHereNowCount(int hereNowCount) {
		this.hereNowCount = hereNowCount;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	

}
