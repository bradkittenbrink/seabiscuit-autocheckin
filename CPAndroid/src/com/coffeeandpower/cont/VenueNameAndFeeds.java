package com.coffeeandpower.cont;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;


public class VenueNameAndFeeds  implements Parcelable  {

    private int venueId;
    private String name;   
    private ArrayList<Feed> feedsArray;


    public VenueNameAndFeeds(int venueId, String name) {

        this.venueId = venueId;
        this.setName(name);

    }

    public VenueNameAndFeeds(int venueId, String name, ArrayList<Feed> feedsArray) {

        this.venueId = venueId;
        this.name = name;
        this.feedsArray = feedsArray;
    }

    public void setVenueId(int venueId) {
        this.venueId = venueId;
    }


    public int getVenueId() {
        return venueId;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Feed> getFeedsArray() {
        Log.d("VenueNameAndFeeds", this.name + " getFeedsArray length..." + this.feedsArray.size());
        return feedsArray;
    }

    public void setFeedsArray(ArrayList<Feed> feedsArray) {
        this.feedsArray = feedsArray;
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.venueId);
        out.writeString(this.name);
    }

    public static final Parcelable.Creator<VenueNameAndFeeds> CREATOR = new Parcelable.Creator<VenueNameAndFeeds>() {
            public VenueNameAndFeeds createFromParcel(Parcel in) {
                return new VenueNameAndFeeds(in);
            }

            public VenueNameAndFeeds[] newArray(int size) {
                return new VenueNameAndFeeds[size];
            }
    };

    private VenueNameAndFeeds(Parcel in) {
        this.venueId = in.readInt();
        this.name = in.readString();
    }

}
