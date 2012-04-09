package com.coffeeandpower.cont;

public class UserResume {

	private String nickName;
	private String statusText;
	private String urlPhoto;
	private String urlPhotoSmall;
	private String joined;
	private String bio;
	
	// Stats
	private int totalEarned;
	private int totalTipsEarned;
	private int totalMissionCountAsRecipient;
	private int distinctTipPayers;
	private int totalSpent;
	private int totalTipsSpent;
	private int totalMissionCountAsPayer;
	private int distinctTipRecipients;
	private double totalEarnedFromMe;
	private int totalMissionsFromMe;
	private int totalMissionsAsAgent;
	private int totalMissionsAsClient;
	private int totalMissions;
	private String totalFunded;
	
	private String skillSet;
	private String hourlyBillingRate;
	
	// Verified
	private String verifiedLinkedIn;
	private String linkedInProfileLink;
	private String verifiedFacebook;
	private String facebookProfileLink;
	private String verifiedMobile;
	
	private String trusted;
	private String jobTitle;
	
	// work
	// education
	
	// Check In Data
	private int checkInId;
	private int userId;
	private double lat;
	private double lng;
	private String checkInDate;
	private String checkIn;
	private String checkOutDate;
	private String checkOut;
	private String foursquare;
	private String foursquareId;
	private String venueName;
	private String venueAddress;
	private String city;
	private String state;
	private String zip;
	private String phone;
	private String icon;
	private String visible;
	private String photoUrlUnUsed; // Unused
	private String formattedPhone;
	private int usersHere;
	
	// Reviews
	private String reviewsPage;
	private String reviewsTotal;
	private String reviewsRecords;
	private String reviewsLoveReceived;
	
	private double locationLat;
	private double locationLng;
	
	
	public UserResume(String nickName, String statusText, String urlPhoto,
			String urlPhotoSmall, String joined, String bio, int totalEarned,
			int totalTipsEarned, int totalMissionCountAsRecipient,
			int distinctTipPayers, int totalSpent, int totalTipsSpent,
			int totalMissionCountAsPayer, int distinctTipRecipients,
			double totalEarnedFromMe, int totalMissionsFromMe,
			int totalMissionsAsAgent, int totalMissionsAsClient,
			int totalMissions, String totalFunded, String skillSet,
			String hourlyBillingRate, String verifiedLinkedIn,
			String linkedInProfileLink, String verifiedFacebook,
			String facebookProfileLink, String verifiedMobile, String trusted,
			String jobTitle, int checkInId, int userId, double lat, double lng,
			String checkInDate, String checkIn, String checkOutDate,
			String checkOut, String foursquare, String foursquareId,
			String venueName, String venueAddress, String city, String state,
			String zip, String phone, String icon, String visible,
			String photoUrlUnUsed, String formattedPhone, int usersHere,
			String reviewsPage, String reviewsTotal, String reviewsRecords,
			String reviewsLoveReceived, double locationLat, double locationLng) {

		this.nickName = nickName;
		this.statusText = statusText;
		this.urlPhoto = urlPhoto;
		this.urlPhotoSmall = urlPhotoSmall;
		this.joined = joined;
		this.bio = bio;
		this.totalEarned = totalEarned;
		this.totalTipsEarned = totalTipsEarned;
		this.totalMissionCountAsRecipient = totalMissionCountAsRecipient;
		this.distinctTipPayers = distinctTipPayers;
		this.totalSpent = totalSpent;
		this.totalTipsSpent = totalTipsSpent;
		this.totalMissionCountAsPayer = totalMissionCountAsPayer;
		this.distinctTipRecipients = distinctTipRecipients;
		this.totalEarnedFromMe = totalEarnedFromMe;
		this.totalMissionsFromMe = totalMissionsFromMe;
		this.totalMissionsAsAgent = totalMissionsAsAgent;
		this.totalMissionsAsClient = totalMissionsAsClient;
		this.totalMissions = totalMissions;
		this.totalFunded = totalFunded;
		this.skillSet = skillSet;
		this.hourlyBillingRate = hourlyBillingRate;
		this.verifiedLinkedIn = verifiedLinkedIn;
		this.linkedInProfileLink = linkedInProfileLink;
		this.verifiedFacebook = verifiedFacebook;
		this.facebookProfileLink = facebookProfileLink;
		this.verifiedMobile = verifiedMobile;
		this.trusted = trusted;
		this.jobTitle = jobTitle;
		this.checkInId = checkInId;
		this.userId = userId;
		this.lat = lat;
		this.lng = lng;
		this.checkInDate = checkInDate;
		this.checkIn = checkIn;
		this.checkOutDate = checkOutDate;
		this.checkOut = checkOut;
		this.foursquare = foursquare;
		this.foursquareId = foursquareId;
		this.venueName = venueName;
		this.venueAddress = venueAddress;
		this.city = city;
		this.state = state;
		this.zip = zip;
		this.phone = phone;
		this.icon = icon;
		this.visible = visible;
		this.photoUrlUnUsed = photoUrlUnUsed;
		this.formattedPhone = formattedPhone;
		this.usersHere = usersHere;
		this.reviewsPage = reviewsPage;
		this.reviewsTotal = reviewsTotal;
		this.reviewsRecords = reviewsRecords;
		this.reviewsLoveReceived = reviewsLoveReceived;
		this.locationLat = locationLat;
		this.locationLng = locationLng;
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


	public String getUrlPhoto() {
		return urlPhoto;
	}


	public void setUrlPhoto(String urlPhoto) {
		this.urlPhoto = urlPhoto;
	}


	public String getUrlPhotoSmall() {
		return urlPhotoSmall;
	}


	public void setUrlPhotoSmall(String urlPhotoSmall) {
		this.urlPhotoSmall = urlPhotoSmall;
	}


	public String getJoined() {
		return joined;
	}


	public void setJoined(String joined) {
		this.joined = joined;
	}


	public String getBio() {
		return bio;
	}


	public void setBio(String bio) {
		this.bio = bio;
	}


	public int getTotalEarned() {
		return totalEarned;
	}


	public void setTotalEarned(int totalEarned) {
		this.totalEarned = totalEarned;
	}


	public int getTotalTipsEarned() {
		return totalTipsEarned;
	}


	public void setTotalTipsEarned(int totalTipsEarned) {
		this.totalTipsEarned = totalTipsEarned;
	}


	public int getTotalMissionCountAsRecipient() {
		return totalMissionCountAsRecipient;
	}


	public void setTotalMissionCountAsRecipient(int totalMissionCountAsRecipient) {
		this.totalMissionCountAsRecipient = totalMissionCountAsRecipient;
	}


	public int getDistinctTipPayers() {
		return distinctTipPayers;
	}


	public void setDistinctTipPayers(int distinctTipPayers) {
		this.distinctTipPayers = distinctTipPayers;
	}


	public int getTotalSpent() {
		return totalSpent;
	}


	public void setTotalSpent(int totalSpent) {
		this.totalSpent = totalSpent;
	}


	public int getTotalTipsSpent() {
		return totalTipsSpent;
	}


	public void setTotalTipsSpent(int totalTipsSpent) {
		this.totalTipsSpent = totalTipsSpent;
	}


	public int getTotalMissionCountAsPayer() {
		return totalMissionCountAsPayer;
	}


	public void setTotalMissionCountAsPayer(int totalMissionCountAsPayer) {
		this.totalMissionCountAsPayer = totalMissionCountAsPayer;
	}


	public int getDistinctTipRecipients() {
		return distinctTipRecipients;
	}


	public void setDistinctTipRecipients(int distinctTipRecipients) {
		this.distinctTipRecipients = distinctTipRecipients;
	}


	public double getTotalEarnedFromMe() {
		return totalEarnedFromMe;
	}


	public void setTotalEarnedFromMe(double totalEarnedFromMe) {
		this.totalEarnedFromMe = totalEarnedFromMe;
	}


	public int getTotalMissionsFromMe() {
		return totalMissionsFromMe;
	}


	public void setTotalMissionsFromMe(int totalMissionsFromMe) {
		this.totalMissionsFromMe = totalMissionsFromMe;
	}


	public int getTotalMissionsAsAgent() {
		return totalMissionsAsAgent;
	}


	public void setTotalMissionsAsAgent(int totalMissionsAsAgent) {
		this.totalMissionsAsAgent = totalMissionsAsAgent;
	}


	public int getTotalMissionsAsClient() {
		return totalMissionsAsClient;
	}


	public void setTotalMissionsAsClient(int totalMissionsAsClient) {
		this.totalMissionsAsClient = totalMissionsAsClient;
	}


	public int getTotalMissions() {
		return totalMissions;
	}


	public void setTotalMissions(int totalMissions) {
		this.totalMissions = totalMissions;
	}


	public String getTotalFunded() {
		return totalFunded;
	}


	public void setTotalFunded(String totalFunded) {
		this.totalFunded = totalFunded;
	}


	public String getSkillSet() {
		return skillSet;
	}


	public void setSkillSet(String skillSet) {
		this.skillSet = skillSet;
	}


	public String getHourlyBillingRate() {
		return hourlyBillingRate;
	}


	public void setHourlyBillingRate(String hourlyBillingRate) {
		this.hourlyBillingRate = hourlyBillingRate;
	}


	public String getVerifiedLinkedIn() {
		return verifiedLinkedIn;
	}


	public void setVerifiedLinkedIn(String verifiedLinkedIn) {
		this.verifiedLinkedIn = verifiedLinkedIn;
	}


	public String getLinkedInProfileLink() {
		return linkedInProfileLink;
	}


	public void setLinkedInProfileLink(String linkedInProfileLink) {
		this.linkedInProfileLink = linkedInProfileLink;
	}


	public String getVerifiedFacebook() {
		return verifiedFacebook;
	}


	public void setVerifiedFacebook(String verifiedFacebook) {
		this.verifiedFacebook = verifiedFacebook;
	}


	public String getFacebookProfileLink() {
		return facebookProfileLink;
	}


	public void setFacebookProfileLink(String facebookProfileLink) {
		this.facebookProfileLink = facebookProfileLink;
	}


	public String getVerifiedMobile() {
		return verifiedMobile;
	}


	public void setVerifiedMobile(String verifiedMobile) {
		this.verifiedMobile = verifiedMobile;
	}


	public String getTrusted() {
		return trusted;
	}


	public void setTrusted(String trusted) {
		this.trusted = trusted;
	}


	public String getJobTitle() {
		return jobTitle;
	}


	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}


	public int getCheckInId() {
		return checkInId;
	}


	public void setCheckInId(int checkInId) {
		this.checkInId = checkInId;
	}


	public int getUserId() {
		return userId;
	}


	public void setUserId(int userId) {
		this.userId = userId;
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


	public String getCheckInDate() {
		return checkInDate;
	}


	public void setCheckInDate(String checkInDate) {
		this.checkInDate = checkInDate;
	}


	public String getCheckIn() {
		return checkIn;
	}


	public void setCheckIn(String checkIn) {
		this.checkIn = checkIn;
	}


	public String getCheckOutDate() {
		return checkOutDate;
	}


	public void setCheckOutDate(String checkOutDate) {
		this.checkOutDate = checkOutDate;
	}


	public String getCheckOut() {
		return checkOut;
	}


	public void setCheckOut(String checkOut) {
		this.checkOut = checkOut;
	}


	public String getFoursquare() {
		return foursquare;
	}


	public void setFoursquare(String foursquare) {
		this.foursquare = foursquare;
	}


	public String getFoursquareId() {
		return foursquareId;
	}


	public void setFoursquareId(String foursquareId) {
		this.foursquareId = foursquareId;
	}


	public String getVenueName() {
		return venueName;
	}


	public void setVenueName(String venueName) {
		this.venueName = venueName;
	}


	public String getVenueAddress() {
		return venueAddress;
	}


	public void setVenueAddress(String venueAddress) {
		this.venueAddress = venueAddress;
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


	public String getZip() {
		return zip;
	}


	public void setZip(String zip) {
		this.zip = zip;
	}


	public String getPhone() {
		return phone;
	}


	public void setPhone(String phone) {
		this.phone = phone;
	}


	public String getIcon() {
		return icon;
	}


	public void setIcon(String icon) {
		this.icon = icon;
	}


	public String getVisible() {
		return visible;
	}


	public void setVisible(String visible) {
		this.visible = visible;
	}


	public String getPhotoUrlUnUsed() {
		return photoUrlUnUsed;
	}


	public void setPhotoUrlUnUsed(String photoUrlUnUsed) {
		this.photoUrlUnUsed = photoUrlUnUsed;
	}


	public String getFormattedPhone() {
		return formattedPhone;
	}


	public void setFormattedPhone(String formattedPhone) {
		this.formattedPhone = formattedPhone;
	}


	public int getUsersHere() {
		return usersHere;
	}


	public void setUsersHere(int usersHere) {
		this.usersHere = usersHere;
	}


	public String getReviewsPage() {
		return reviewsPage;
	}


	public void setReviewsPage(String reviewsPage) {
		this.reviewsPage = reviewsPage;
	}


	public String getReviewsTotal() {
		return reviewsTotal;
	}


	public void setReviewsTotal(String reviewsTotal) {
		this.reviewsTotal = reviewsTotal;
	}


	public String getReviewsRecords() {
		return reviewsRecords;
	}


	public void setReviewsRecords(String reviewsRecords) {
		this.reviewsRecords = reviewsRecords;
	}


	public String getReviewsLoveReceived() {
		return reviewsLoveReceived;
	}


	public void setReviewsLoveReceived(String reviewsLoveReceived) {
		this.reviewsLoveReceived = reviewsLoveReceived;
	}


	public double getLocationLat() {
		return locationLat;
	}


	public void setLocationLat(double locationLat) {
		this.locationLat = locationLat;
	}


	public double getLocationLng() {
		return locationLng;
	}


	public void setLocationLng(double locationLng) {
		this.locationLng = locationLng;
	}
	
	
	
	
}
