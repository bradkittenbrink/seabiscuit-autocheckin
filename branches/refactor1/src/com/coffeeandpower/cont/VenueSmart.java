package com.coffeeandpower.cont;

import java.util.ArrayList;

import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class VenueSmart implements Parcelable{

	private int venueId;
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
	
	public static class CheckinData implements Parcelable {
		int userId;
		int checkinCount;
		int checkedIn;

		public CheckinData(int userId, int checkinCount, int checkedIn) {
			this.userId = userId;
			this.checkinCount = checkinCount;
			this.checkedIn = checkedIn;
		}

		public int getUserId() {
			return userId;
		}

		public int getCheckinCount() {
			return checkinCount;
		}

		public int getCheckedIn() {
			return checkedIn;
		}
		
		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel out, int flags) {
			out.writeInt(userId);
			out.writeInt(checkinCount);
			out.writeInt(checkedIn);
		}
		public static final Parcelable.Creator<CheckinData> CREATOR = new Parcelable.Creator<CheckinData>() {
		            public CheckinData createFromParcel(Parcel in) {
		                return new CheckinData(in);
		            }
		        
		            public CheckinData[] newArray(int size) {
		                return new CheckinData[size];
		            }
			};

		        private CheckinData(Parcel in) {
		        	this.userId = in.readInt();
		        	this.checkinCount = in.readInt();
		        	this.checkedIn = in.readInt();		    
		        }

			public static Creator<CheckinData> CREATOR() {
				// TODO Auto-generated method stub
				return null;
			}
	}
	public VenueSmart(JSONObject objVenue, ArrayList<CheckinData> arrayCheckins)
	{
		this.venueId = objVenue.optInt("venue_id");
		this.name = objVenue.optString("name");
		this.address = objVenue.optString("address");
		this.city = objVenue.optString("city");
		this.state = objVenue.optString("state");
		this.distance = objVenue.optString("distance");
		this.foursquareId = objVenue.optString("foursquare_id");
		this.checkins = objVenue.optInt("checkins");
		this.checkinsForWeek = objVenue.optInt("checkins_for_week");
		this.checkinsForInterval = objVenue.optInt("checkins_for_interval");
		this.photoURL = objVenue.optString("photo_url");
		this.phone = objVenue.optString("phone");
		this.formattedPhone = objVenue.optString("formatted_phone");
		this.lat = objVenue.optDouble("lat");
		this.lng = objVenue.optDouble("lng");
		this.arrayCheckins = arrayCheckins;
	}
	
	public static VenueSmart createVenueFromJSON(JSONObject obj) {
		return new VenueSmart(obj,null);
	}
	
	/**
	 * Create empty venue obj
	 */
	public VenueSmart() {
		this(0, "", "", "", "", "", "", 0, 0, 0, "", "", "", 0, 0, new ArrayList<CheckinData>());
	}
	

	public VenueSmart(int venueId, String name, String address, String city, String state, String distance, String foursquareId, int checkins,
			int checkinsForWeek, int checkinsForInterval, String photoURL, String phone, String formattedPhone, double lat, double lng,
			ArrayList<CheckinData> arrayCheckins) {
		this.venueId = venueId;
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
	
	
	public static VenueSmart createVenuePlaceholder(String fourSquareId,String name) {
		return new VenueSmart(0,name,"","","","",fourSquareId,0,0,0,"","","",0,0,null);		
	}

	public int getVenueId() {
		return venueId;
	}

	public void setVenueId(int venueId) {
		this.venueId = venueId;
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

	public String getDistance() {
		return distance;
	}
	
	public float getDistanceFloat() {
		if (distance.equals(""))
			return 0;
		else
			return new Float(distance).floatValue();
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public String getFoursquareId() {
		return foursquareId;
	}

	public void setFoursquareId(String foursquareId) {
		this.foursquareId = foursquareId;
	}

	public int getCheckins() {
		return checkins;
	}

	public void setCheckins(int checkins) {
		this.checkins = checkins;
	}

	public int getCheckinsForWeek() {
		return checkinsForWeek;
	}

	public void setCheckinsForWeek(int checkinsForWeek) {
		this.checkinsForWeek = checkinsForWeek;
	}

	public int getCheckinsForInterval() {
		return checkinsForInterval;
	}

	public void setCheckinsForInterval(int checkinsForInterval) {
		this.checkinsForInterval = checkinsForInterval;
	}

	public String getPhotoURL() {
		return photoURL;
	}

	public void setPhotoURL(String photoURL) {
		this.photoURL = photoURL;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getFormattedPhone() {
		return formattedPhone;
	}

	public void setFormattedPhone(String formattedPhone) {
		this.formattedPhone = formattedPhone;
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

	public ArrayList<CheckinData> getArrayCheckins() {
		return arrayCheckins;
	}

	public void setArrayCheckins(ArrayList<CheckinData> arrayCheckins) {
		this.arrayCheckins = arrayCheckins;
	}
	
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		// TODO Auto-generated method stub
		
		
		out.writeInt(this.venueId);
		out.writeString(this.name);
		out.writeString(this.address);
		out.writeString(this.city);
		out.writeString(this.state);
		out.writeString(this.distance);
		out.writeString(this.foursquareId);
		
		out.writeInt(this.checkins);
		out.writeInt(this.checkinsForWeek);
		out.writeInt(this.checkinsForInterval);
		
		out.writeString(this.photoURL);
		out.writeString(this.phone);
		out.writeString(this.formattedPhone);
		
		out.writeDouble(this.lat);
		out.writeDouble(this.lng);
		
		//This should be a list of users, need to get that resolved at some point
		out.writeTypedList(this.arrayCheckins);
		
	}
	public static final Parcelable.Creator<VenueSmart> CREATOR = new Parcelable.Creator<VenueSmart>() {
	            public VenueSmart createFromParcel(Parcel in) {
	                return new VenueSmart(in);
	            }
	        
	            public VenueSmart[] newArray(int size) {
	                return new VenueSmart[size];
	            }
		};

	        private VenueSmart(Parcel in) {
	            this.venueId = in.readInt();
	            this.name = in.readString();
	            this.address = in.readString();
	            this.city = in.readString();
	            this.state = in.readString();
	            this.distance = in.readString();
	            this.foursquareId = in.readString();
	            
	            this.checkins = in.readInt();
	            this.checkinsForWeek = in.readInt();
	            this.checkinsForInterval = in.readInt();
	            
	            this.photoURL = in.readString();
	            this.phone = in.readString();
	            this.formattedPhone = in.readString();
	            
	            this.lat = in.readDouble();
	            this.lng = in.readDouble();
	            
	            this.arrayCheckins =  new ArrayList<CheckinData>();
	            in.readTypedList(this.arrayCheckins,CheckinData.CREATOR);
	        }

}
