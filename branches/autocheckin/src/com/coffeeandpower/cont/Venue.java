package com.coffeeandpower.cont;

import java.io.Serializable;

import org.json.JSONArray;
import org.json.JSONObject;

@SuppressWarnings("serial")
public class Venue implements Serializable {

    private String foursquareId;
    private int venueId;
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
    private String icon;
    private String photoUrl;
    private int checkinTime;

    private int checkinsCount;
    private int usersCount;
    private int tipCount;
    private int hereNowCount;

    /**
     * Create empty venue obj
     */
    public Venue() {
        this("", 0, "", "", "", 0, 0, 0, "", "", "", "", "", "", "", 0, 0, 0,
                0, "", "", "", 0);
    }

    public Venue(JSONObject venue) {

        this.venueId = venue.optInt("id");
        this.name = venue.optString("name");
        this.foursquareId = venue.optString("foursquare_id");

        // Location
        // Object
        JSONObject locationObj = venue.optJSONObject("location");
        if (locationObj != null) {

            this.address = locationObj.optString("address");
            this.crossStreet = locationObj.optString("crossStreet");
            this.lat = locationObj.optDouble("lat");
            this.lng = locationObj.optDouble("lng");
            this.distance = locationObj.optInt("distance");
            this.postalCode = locationObj.optString("postalCode");
            this.city = locationObj.optString("city");
            this.state = locationObj.optString("state");
            this.country = locationObj.optString("country");

        }

        // Categories
        // Array
        JSONArray categoriesArray = venue.optJSONArray("categories");
        if (categoriesArray != null) {

            if (categoriesArray.length() > 0) {
                // TODO: This only gets the first category
                JSONObject cat = categoriesArray.optJSONObject(0);
                if (cat != null) {

                    this.categoryName = cat.optString("name");
                    this.categoryPluralName = cat.optString("pluralName");
                    this.categoryShortName = cat.optString("shortName");
                }
            }
        }

        // Stats
        // Object
        JSONObject statsObj = venue.optJSONObject("stats");
        if (statsObj != null) {

            this.checkinsCount = statsObj.optInt("checkinsCount");
            this.usersCount = statsObj.optInt("usersCount");
            this.tipCount = statsObj.optInt("tipCount");
        }

        // HereNow
        // Object
        JSONObject hereNowObj = venue.optJSONObject("hereNow");
        if (hereNowObj != null) {

            this.hereNowCount = hereNowObj.optInt("count");
        }

    }

    public Venue(String foursquareId, int venueId, String name, String address,
            String crossStreet, double lat, double lng, int distance,
            String postalCode, String city, String state, String country,
            String categoryName, String categoryPluralName,
            String categoryShortName, int checkinsCount, int usersCount,
            int tipCount, int hereNowCount, String phone, String icon,
            String photoUrl, int checkinTime) {

        this.foursquareId = foursquareId;
        this.venueId = venueId;
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
        this.icon = icon;
        this.photoUrl = photoUrl;
        this.checkinTime = checkinTime;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getCheckinTime() {
        return checkinTime;
    }

    public void setCheckinTime(int checkinTime) {
        this.checkinTime = checkinTime / 3600;
    }

    public int getVenueId() {
        return venueId;
    }

    public void setVenueId(int venueId) {
        this.venueId = venueId;
    }

    public String getFoursquareId() {
        return foursquareId;
    }

    public void setFoursquareId(String id) {
        this.foursquareId = id;
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
     * 
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
