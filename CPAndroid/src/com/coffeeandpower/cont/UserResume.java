package com.coffeeandpower.cont;

import java.util.ArrayList;

public class UserResume {

    private String nickName;
    private String majorJob;
    private String minorJob;
    private String statusText;

    // Location
    private double locationLat;
    private double locationLng;

    private String urlPhoto;
    private String urlThumbnail;
    private String joined;
    private String joinedBrief;
    private String joinDate;
    private String joinSponsor;
    private String enteredInviteCode;
    private String bio;

    // Stats
    private int totalEarned;
    private int totalTipsEarned;
    private int totalHours;
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
    private String verifiedLinkedInProfileLink;
    private String verifiedFacebook;
    private String verifiedFacebookProfileLink;
    private String verifiedMobile;

    private String contactsOnlyChat;
    private boolean userIsContact;
    private String linkedInPublicProfileUrl;
    private String trusted;
    private String smartererName;
    private String jobTitle;

    // Work and Education
    private ArrayList<Education> education;
    private ArrayList<Work> work;

    private String userHasEducation;

    // Check In Data
    private int checkInData_id;
    private int checkInData_userId;
    private double checkInData_lat;
    private double checkInData_lng;
    private String checkInData_Date;
    private String checkInData_checkIn;
    private String checkInData_checkOutDate;
    private String checkInData_checkOut;
    private String checkInData_checkedIn;
    private String checkInData_venueId;
    private String checkInData_foursquareId;
    private String checkInData_Name;
    private String checkInData_Address;
    private String checkInData_city;
    private String checkInData_state;
    private String checkInData_zip;
    private String checkInData_phone;
    private String checkInData_icon;
    private String checkInData_visible;
    private String checkInData_photoUrl;
    private String checkInData_formattedPhone;
    private int checkInData_usersHere;
    private int checkInData_usersIncludingMe;
    private int checkInData_availableForHours;
    private int checkInData_availableForMinutes;

    private ArrayList<Venue> checkinhistoryArray;

    private String userHasFavoritePlaces;

    private ArrayList<Venue> favoritePlaces;

    // Reviews
    private String reviewsPage;
    private int reviewsTotal;
    private String reviewsRecords;
    private String reviewsLoveReceived;

    private ArrayList<Review> reviews;
    private ArrayList<Listing> agentListings;
    private ArrayList<Listing> clienListings;

    public UserResume() {
        this("", "", "", "", 0, 0, "", "", "", "", "", "", "", 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, "", "", "", "", "", "", "", "", "",
                false, "", "", "", "", new ArrayList<Education>(),
                new ArrayList<Work>(), "", 0, 0, 0, 0, "", "", "", "", "", "",
                "", "", "", "", "", "", "", "", "", "", "", 0, 0, 0, 0,
                new ArrayList<Venue>(), "", new ArrayList<Venue>(), "", 0, "",
                "", new ArrayList<Review>(), new ArrayList<Listing>(),
                new ArrayList<Listing>());
    }

    public UserResume(String nickName, String majorJob, String minorJob,
            String statusText, double locationLat, double locationLng,
            String urlPhoto, String urlThumbnail, String joined,
            String joinedBrief, String joinDate, String enteredInviteCode,
            String bio, int totalEarned, int totalTipsEarned, int totalHours,
            int totalMissionCountAsRecipient, int distinctTipPayers,
            int totalSpent, int totalTipsSpent, int totalMissionCountAsPayer,
            int distinctTipRecipients, double totalEarnedFromMe,
            int totalMissionsFromMe, int totalMissionsAsAgent,
            int totalMissionsAsClient, int totalMissions, String totalFunded,
            String skillSet, String hourlyBillingRate, String verifiedLinkedIn,
            String verifiedLinkedInProfileLink, String verifiedFacebook,
            String verifiedFacebookProfileLink, String verifiedMobile,
            String contactsOnlyChat, boolean userIsContact,
            String linkedInPublicProfileUrl, String trusted,
            String smartererName, String jobTitle,
            ArrayList<Education> education, ArrayList<Work> work,
            String userHasEducation, int checkInData_id,
            int checkInData_userId, double checkInData_lat,
            double checkInData_lng, String checkInData_Date,
            String checkInData_checkIn, String checkInData_checkOutDate,
            String checkInData_checkOut, String checkInData_checkedIn,
            String checkInData_venueId, String checkInData_foursquareId,
            String checkInData_Name, String checkInData_Address,
            String checkInData_city, String checkInData_state,
            String checkInData_zip, String checkInData_phone,
            String checkInData_icon, String checkInData_visible,
            String checkInData_photoUrl, String checkInData_formattedPhone,
            int checkInData_usersHere, int checkInData_usersIncludingMe,
            int checkInData_availableForHours,
            int checkInData_availableForMinutes,
            ArrayList<Venue> checkinhistoryArray, String userHasFavoritePlaces,
            ArrayList<Venue> favoritePlaces, String reviewsPage,
            int reviewsTotal, String reviewsRecords,
            String reviewsLoveReceived, ArrayList<Review> reviews,
            ArrayList<Listing> agentListings, ArrayList<Listing> clienListings) {

        this.nickName = nickName;
        this.majorJob = majorJob;
        this.minorJob = minorJob;
        this.statusText = statusText;
        this.locationLat = locationLat;
        this.locationLng = locationLng;
        this.urlPhoto = urlPhoto;
        this.urlThumbnail = urlThumbnail;
        this.joined = joined;
        this.joinedBrief = joinedBrief;
        this.joinDate = joinDate;
        this.enteredInviteCode = enteredInviteCode;
        this.bio = bio;
        this.totalEarned = totalEarned;
        this.totalTipsEarned = totalTipsEarned;
        this.totalHours = totalHours;
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
        this.verifiedLinkedInProfileLink = verifiedLinkedInProfileLink;
        this.verifiedFacebook = verifiedFacebook;
        this.verifiedFacebookProfileLink = verifiedFacebookProfileLink;
        this.verifiedMobile = verifiedMobile;
        this.contactsOnlyChat = contactsOnlyChat;
        this.userIsContact = userIsContact;
        this.linkedInPublicProfileUrl = linkedInPublicProfileUrl;
        this.trusted = trusted;
        this.smartererName = smartererName;
        this.jobTitle = jobTitle;
        this.education = education;
        this.work = work;
        this.userHasEducation = userHasEducation;
        this.checkInData_id = checkInData_id;
        this.checkInData_userId = checkInData_userId;
        this.checkInData_lat = checkInData_lat;
        this.checkInData_lng = checkInData_lng;
        this.checkInData_Date = checkInData_Date;
        this.checkInData_checkIn = checkInData_checkIn;
        this.checkInData_checkOutDate = checkInData_checkOutDate;
        this.checkInData_checkOut = checkInData_checkOut;
        this.checkInData_checkedIn = checkInData_checkedIn;
        this.checkInData_venueId = checkInData_venueId;
        this.checkInData_foursquareId = checkInData_foursquareId;
        this.checkInData_Name = checkInData_Name;
        this.checkInData_Address = checkInData_Address;
        this.checkInData_city = checkInData_city;
        this.checkInData_state = checkInData_state;
        this.checkInData_zip = checkInData_zip;
        this.checkInData_phone = checkInData_phone;
        this.checkInData_icon = checkInData_icon;
        this.checkInData_visible = checkInData_visible;
        this.checkInData_photoUrl = checkInData_photoUrl;
        this.checkInData_formattedPhone = checkInData_formattedPhone;
        this.checkInData_usersHere = checkInData_usersHere;
        this.checkInData_usersIncludingMe = checkInData_usersIncludingMe;
        this.checkInData_availableForHours = checkInData_availableForHours;
        this.checkInData_availableForMinutes = checkInData_availableForMinutes;
        this.checkinhistoryArray = checkinhistoryArray;
        this.userHasFavoritePlaces = userHasFavoritePlaces;
        this.favoritePlaces = favoritePlaces;
        this.reviewsPage = reviewsPage;
        this.reviewsTotal = reviewsTotal;
        this.reviewsRecords = reviewsRecords;
        this.reviewsLoveReceived = reviewsLoveReceived;
        this.reviews = reviews;
        this.agentListings = agentListings;
        this.clienListings = clienListings;
    }

    public ArrayList<Venue> getFavoritePlaces() {
        return favoritePlaces;
    }

    public void setFavoritePlaces(ArrayList<Venue> favoritePlaces) {
        this.favoritePlaces = favoritePlaces;
    }

    public ArrayList<Venue> getCheckinhistoryArray() {
        return checkinhistoryArray;
    }

    public void setCheckinhistoryArray(ArrayList<Venue> checkinhistoryArray) {
        this.checkinhistoryArray = checkinhistoryArray;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getMajorJob() {
        return majorJob;
    }

    public void setMajorJob(String majorJob) {
        this.majorJob = majorJob;
    }

    public String getMinorJob() {
        return minorJob;
    }

    public void setMinorJob(String minorJob) {
        this.minorJob = minorJob;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
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

    public String getUrlPhoto() {
        return urlPhoto;
    }

    public void setUrlPhoto(String urlPhoto) {
        this.urlPhoto = urlPhoto;
    }

    public String getUrlThumbnail() {
        return urlThumbnail;
    }

    public void setUrlThumbnail(String urlThumbnail) {
        this.urlThumbnail = urlThumbnail;
    }

    public String getJoined() {
        return joined;
    }

    public void setJoined(String joined) {
        this.joined = joined;
    }

    public String getJoinedBrief() {
        return joinedBrief;
    }

    public void setJoinedBrief(String joinedBrief) {
        this.joinedBrief = joinedBrief;
    }

    public String getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(String joinDate) {
        this.joinDate = joinDate;
    }

    public String getJoinSponsor() {
        return joinSponsor;
    }

    public void setJoinSponsor(String joinSponsor) {
        this.joinSponsor = joinSponsor;
    }

    public String getEnteredInviteCode() {
        return enteredInviteCode;
    }

    public void setEnteredInviteCode(String enteredInviteCode) {
        this.enteredInviteCode = enteredInviteCode;
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

    public int getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(int totalHours) {
        this.totalHours = totalHours;
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

    public String getVerifiedLinkedInProfileLink() {
        return verifiedLinkedInProfileLink;
    }

    public void setVerifiedLinkedInProfileLink(
            String verifiedLinkedInProfileLink) {
        this.verifiedLinkedInProfileLink = verifiedLinkedInProfileLink;
    }

    public String getVerifiedFacebook() {
        return verifiedFacebook;
    }

    public void setVerifiedFacebook(String verifiedFacebook) {
        this.verifiedFacebook = verifiedFacebook;
    }

    public String getVerifiedFacebookProfileLink() {
        return verifiedFacebookProfileLink;
    }

    public void setVerifiedFacebookProfileLink(
            String verifiedFacebookProfileLink) {
        this.verifiedFacebookProfileLink = verifiedFacebookProfileLink;
    }

    public String getVerifiedMobile() {
        return verifiedMobile;
    }

    public void setVerifiedMobile(String verifiedMobile) {
        this.verifiedMobile = verifiedMobile;
    }

    public String getContactsOnlyChat() {
        return contactsOnlyChat;
    }

    public void setContactsOnlyChat(String contactsOnlyChat) {
        this.contactsOnlyChat = contactsOnlyChat;
    }

    public boolean isUserIsContact() {
        return userIsContact;
    }

    public void setUserIsContact(boolean userIsContact) {
        this.userIsContact = userIsContact;
    }

    public String getLinkedInPublicProfileUrl() {
        return linkedInPublicProfileUrl;
    }

    public void setLinkedInPublicProfileUrl(String linkedInPublicProfileUrl) {
        this.linkedInPublicProfileUrl = linkedInPublicProfileUrl;
    }

    public String getTrusted() {
        return trusted;
    }

    public void setTrusted(String trusted) {
        this.trusted = trusted;
    }

    public String getSmartererName() {
        return smartererName;
    }

    public void setSmartererName(String smartererName) {
        this.smartererName = smartererName;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public ArrayList<Education> getEducation() {
        return education;
    }

    public void setEducation(ArrayList<Education> education) {
        this.education = education;
    }

    public ArrayList<Work> getWork() {
        return work;
    }

    public void setWork(ArrayList<Work> work) {
        this.work = work;
    }

    public String getUserHasEducation() {
        return userHasEducation;
    }

    public void setUserHasEducation(String userHasEducation) {
        this.userHasEducation = userHasEducation;
    }

    public int getCheckInData_id() {
        return checkInData_id;
    }

    public void setCheckInData_id(int checkInData_id) {
        this.checkInData_id = checkInData_id;
    }

    public int getCheckInData_userId() {
        return checkInData_userId;
    }

    public void setCheckInData_userId(int checkInData_userId) {
        this.checkInData_userId = checkInData_userId;
    }

    public double getCheckInData_lat() {
        return checkInData_lat;
    }

    public void setCheckInData_lat(double checkInData_lat) {
        this.checkInData_lat = checkInData_lat;
    }

    public double getCheckInData_lng() {
        return checkInData_lng;
    }

    public void setCheckInData_lng(double checkInData_lng) {
        this.checkInData_lng = checkInData_lng;
    }

    public String getCheckInData_Date() {
        return checkInData_Date;
    }

    public void setCheckInData_Date(String checkInData_Date) {
        this.checkInData_Date = checkInData_Date;
    }

    public String getCheckInData_checkIn() {
        return checkInData_checkIn;
    }

    public void setCheckInData_checkIn(String checkInData_checkIn) {
        this.checkInData_checkIn = checkInData_checkIn;
    }

    public String getCheckInData_checkOutDate() {
        return checkInData_checkOutDate;
    }

    public void setCheckInData_checkOutDate(String checkInData_checkOutDate) {
        this.checkInData_checkOutDate = checkInData_checkOutDate;
    }

    public String getCheckInData_checkOut() {
        return checkInData_checkOut;
    }

    public void setCheckInData_checkOut(String checkInData_checkOut) {
        this.checkInData_checkOut = checkInData_checkOut;
    }

    public String getCheckInData_checkedIn() {
        return checkInData_checkedIn;
    }

    public void setCheckInData_checkedIn(String checkInData_checkedIn) {
        this.checkInData_checkedIn = checkInData_checkedIn;
    }

    public String getCheckInData_venueId() {
        return checkInData_venueId;
    }

    public void setCheckInData_venueId(String checkInData_venueId) {
        this.checkInData_venueId = checkInData_venueId;
    }

    public String getCheckInData_foursquareId() {
        return checkInData_foursquareId;
    }

    public void setCheckInData_foursquareId(String checkInData_foursquareId) {
        this.checkInData_foursquareId = checkInData_foursquareId;
    }

    public String getCheckInData_Name() {
        return checkInData_Name;
    }

    public void setCheckInData_Name(String checkInData_Name) {
        this.checkInData_Name = checkInData_Name;
    }

    public String getCheckInData_Address() {
        return checkInData_Address;
    }

    public void setCheckInData_Address(String checkInData_Address) {
        this.checkInData_Address = checkInData_Address;
    }

    public String getCheckInData_city() {
        return checkInData_city;
    }

    public void setCheckInData_city(String checkInData_city) {
        this.checkInData_city = checkInData_city;
    }

    public String getCheckInData_state() {
        return checkInData_state;
    }

    public void setCheckInData_state(String checkInData_state) {
        this.checkInData_state = checkInData_state;
    }

    public String getCheckInData_zip() {
        return checkInData_zip;
    }

    public void setCheckInData_zip(String checkInData_zip) {
        this.checkInData_zip = checkInData_zip;
    }

    public String getCheckInData_phone() {
        return checkInData_phone;
    }

    public void setCheckInData_phone(String checkInData_phone) {
        this.checkInData_phone = checkInData_phone;
    }

    public String getCheckInData_icon() {
        return checkInData_icon;
    }

    public void setCheckInData_icon(String checkInData_icon) {
        this.checkInData_icon = checkInData_icon;
    }

    public String getCheckInData_visible() {
        return checkInData_visible;
    }

    public void setCheckInData_visible(String checkInData_visible) {
        this.checkInData_visible = checkInData_visible;
    }

    public String getCheckInData_photoUrl() {
        return checkInData_photoUrl;
    }

    public void setCheckInData_photoUrl(String checkInData_photoUrl) {
        this.checkInData_photoUrl = checkInData_photoUrl;
    }

    public String getCheckInData_formattedPhone() {
        return checkInData_formattedPhone;
    }

    public void setCheckInData_formattedPhone(String checkInData_formattedPhone) {
        this.checkInData_formattedPhone = checkInData_formattedPhone;
    }

    public int getCheckInData_usersHere() {
        return checkInData_usersHere;
    }

    public void setCheckInData_usersHere(int checkInData_usersHere) {
        this.checkInData_usersHere = checkInData_usersHere;
    }

    public int getCheckInData_usersIncludingMe() {
        return checkInData_usersIncludingMe;
    }

    public void setCheckInData_usersIncludingMe(int checkInData_usersIncludingMe) {
        this.checkInData_usersIncludingMe = checkInData_usersIncludingMe;
    }

    public int getCheckInData_availableForHours() {
        return checkInData_availableForHours;
    }

    public void setCheckInData_availableForHours(
            int checkInData_availableForHours) {
        this.checkInData_availableForHours = checkInData_availableForHours;
    }

    public int getCheckInData_availableForMinutes() {
        return checkInData_availableForMinutes;
    }

    public void setCheckInData_availableForMinutes(
            int checkInData_availableForMinutes) {
        this.checkInData_availableForMinutes = checkInData_availableForMinutes;
    }

    public String getUserHasFavoritePlaces() {
        return userHasFavoritePlaces;
    }

    public void setUserHasFavoritePlaces(String userHasFavoritePlaces) {
        this.userHasFavoritePlaces = userHasFavoritePlaces;
    }

    public String getReviewsPage() {
        return reviewsPage;
    }

    public void setReviewsPage(String reviewsPage) {
        this.reviewsPage = reviewsPage;
    }

    public int getReviewsTotal() {
        return reviewsTotal;
    }

    public void setReviewsTotal(int reviewsTotal) {
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

    public ArrayList<Review> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }

    public ArrayList<Listing> getAgentListings() {
        return agentListings;
    }

    public void setAgentListings(ArrayList<Listing> agentListings) {
        this.agentListings = agentListings;
    }

    public ArrayList<Listing> getClienListings() {
        return clienListings;
    }

    public void setClienListings(ArrayList<Listing> clienListings) {
        this.clienListings = clienListings;
    }

}
