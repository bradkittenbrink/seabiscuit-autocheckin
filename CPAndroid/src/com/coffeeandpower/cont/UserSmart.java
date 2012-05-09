package com.coffeeandpower.cont;

import java.io.Serializable;

@SuppressWarnings("serial")
public class UserSmart implements Serializable
	{

		private int checkInId;
		private int userId;
		private String nickName;
		private String statusText;
		private String photo; // ???
		private String majorJobCategory;
		private String minorJobCategory;
		private String headLine;
		private String fileName; // this is Profile Photo
		private double lat;
		private double lng;
		private int checkedIn;
		private String foursquareId;
		private String venueName;
		private int checkInCount;
		private String skills;
		private boolean met;
		private boolean isFirstInList;

		public boolean isFirstInList ()
			{
				return isFirstInList;
			}

		public void setFirstInList (boolean isFirstInList)
			{
				this.isFirstInList = isFirstInList;
			}

		public UserSmart (int checkInId, int userId, String nickName, String statusText, String photo, String majorJobCategory,
				String minorJobCategory, String headLine, String fileName, double lat, double lng, int checkedIn, String foursquareId,
				String venueName, int checkInCount, String skills, boolean met)
			{
				super ();
				this.checkInId = checkInId;
				this.userId = userId;
				this.nickName = nickName;
				this.statusText = statusText;
				this.photo = photo;
				this.majorJobCategory = majorJobCategory;
				this.minorJobCategory = minorJobCategory;
				this.headLine = headLine;
				this.fileName = fileName;
				this.lat = lat;
				this.lng = lng;
				this.checkedIn = checkedIn;
				this.foursquareId = foursquareId;
				this.venueName = venueName;
				this.checkInCount = checkInCount;
				this.skills = skills;
				this.met = met;
			}

		public int getCheckInId ()
			{
				return checkInId;
			}

		public void setCheckInId (int checkInId)
			{
				this.checkInId = checkInId;
			}

		public int getUserId ()
			{
				return userId;
			}

		public void setUserId (int userId)
			{
				this.userId = userId;
			}

		public String getNickName ()
			{
				return nickName;
			}

		public void setNickName (String nickName)
			{
				this.nickName = nickName;
			}

		public String getStatusText ()
			{
				return statusText;
			}

		public void setStatusText (String statusText)
			{
				this.statusText = statusText;
			}

		public String getPhoto ()
			{
				return photo;
			}

		public void setPhoto (String photo)
			{
				this.photo = photo;
			}

		public String getMajorJobCategory ()
			{
				return majorJobCategory;
			}

		public void setMajorJobCategory (String majorJobCategory)
			{
				this.majorJobCategory = majorJobCategory;
			}

		public String getMinorJobCategory ()
			{
				return minorJobCategory;
			}

		public void setMinorJobCategory (String minorJobCategory)
			{
				this.minorJobCategory = minorJobCategory;
			}

		public String getHeadLine ()
			{
				return headLine;
			}

		public void setHeadLine (String headLine)
			{
				this.headLine = headLine;
			}

		public String getFileName ()
			{
				return fileName;
			}

		public void setFileName (String fileName)
			{
				this.fileName = fileName;
			}

		public double getLat ()
			{
				return lat;
			}

		public void setLat (double lat)
			{
				this.lat = lat;
			}

		public double getLng ()
			{
				return lng;
			}

		public void setLng (double lng)
			{
				this.lng = lng;
			}

		public int getCheckedIn ()
			{
				return checkedIn;
			}

		public void setCheckedIn (int checkedIn)
			{
				this.checkedIn = checkedIn;
			}

		public String getFoursquareId ()
			{
				return foursquareId;
			}

		public void setFoursquareId (String foursquareId)
			{
				this.foursquareId = foursquareId;
			}

		public String getVenueName ()
			{
				return venueName;
			}

		public void setVenueName (String venueName)
			{
				this.venueName = venueName;
			}

		public int getCheckInCount ()
			{
				return checkInCount;
			}

		public void setCheckInCount (int checkInCount)
			{
				this.checkInCount = checkInCount;
			}

		public String getSkills ()
			{
				return skills;
			}

		public void setSkills (String skills)
			{
				this.skills = skills;
			}

		public boolean isMet ()
			{
				return met;
			}

		public void setMet (boolean met)
			{
				this.met = met;
			}

	}
