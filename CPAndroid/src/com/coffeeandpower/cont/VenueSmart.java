package com.coffeeandpower.cont;

import java.util.ArrayList;

public class VenueSmart
	{

		private String name;
		private String address;
		private String city;
		private String state;
		private String distance;
		private String foursquareId;

		private int checkins;
		private int checkinsForWeek;
		private int checkinsForInterval;

		private String photoURL;
		private String phone;
		private String formattedPhone;

		private double lat;
		private double lng;

		private ArrayList<CheckinData> arrayCheckins;

		public static class CheckinData
			{
				int userId;
				int checkinCount;
				int checkedIn;

				public CheckinData (int userId, int checkinCount, int checkedIn)
					{
						this.userId = userId;
						this.checkinCount = checkinCount;
						this.checkedIn = checkedIn;
					}

				public int getUserId ()
					{
						return userId;
					}

				public int getCheckinCount ()
					{
						return checkinCount;
					}

				public int getCheckedIn ()
					{
						return checkedIn;
					}
			}

		public VenueSmart (String name, String address, String city, String state, String distance, String foursquareId, int checkins,
				int checkinsForWeek, int checkinsForInterval, String photoURL, String phone, String formattedPhone, double lat, double lng,
				ArrayList<CheckinData> arrayCheckins)
			{
				this.name = name;
				this.address = address;
				this.city = city;
				this.state = state;
				this.distance = distance;
				this.foursquareId = foursquareId;
				this.checkins = checkins;
				this.checkinsForWeek = checkinsForWeek;
				this.checkinsForInterval = checkinsForInterval;
				this.photoURL = photoURL;
				this.phone = phone;
				this.formattedPhone = formattedPhone;
				this.lat = lat;
				this.lng = lng;
				this.arrayCheckins = arrayCheckins;
			}

		public String getName ()
			{
				return name;
			}

		public void setName (String name)
			{
				this.name = name;
			}

		public String getAddress ()
			{
				return address;
			}

		public void setAddress (String address)
			{
				this.address = address;
			}

		public String getCity ()
			{
				return city;
			}

		public void setCity (String city)
			{
				this.city = city;
			}

		public String getState ()
			{
				return state;
			}

		public void setState (String state)
			{
				this.state = state;
			}

		public String getDistance ()
			{
				return distance;
			}

		public void setDistance (String distance)
			{
				this.distance = distance;
			}

		public String getFoursquareId ()
			{
				return foursquareId;
			}

		public void setFoursquareId (String foursquareId)
			{
				this.foursquareId = foursquareId;
			}

		public int getCheckins ()
			{
				return checkins;
			}

		public void setCheckins (int checkins)
			{
				this.checkins = checkins;
			}

		public int getCheckinsForWeek ()
			{
				return checkinsForWeek;
			}

		public void setCheckinsForWeek (int checkinsForWeek)
			{
				this.checkinsForWeek = checkinsForWeek;
			}

		public int getCheckinsForInterval ()
			{
				return checkinsForInterval;
			}

		public void setCheckinsForInterval (int checkinsForInterval)
			{
				this.checkinsForInterval = checkinsForInterval;
			}

		public String getPhotoURL ()
			{
				return photoURL;
			}

		public void setPhotoURL (String photoURL)
			{
				this.photoURL = photoURL;
			}

		public String getPhone ()
			{
				return phone;
			}

		public void setPhone (String phone)
			{
				this.phone = phone;
			}

		public String getFormattedPhone ()
			{
				return formattedPhone;
			}

		public void setFormattedPhone (String formattedPhone)
			{
				this.formattedPhone = formattedPhone;
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

		public ArrayList<CheckinData> getArrayCheckins ()
			{
				return arrayCheckins;
			}

		public void setArrayCheckins (ArrayList<CheckinData> arrayCheckins)
			{
				this.arrayCheckins = arrayCheckins;
			}

	}
