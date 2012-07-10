package com.coffeeandpower.location;

import android.net.wifi.ScanResult;
import android.os.Parcel;
import android.os.Parcelable;

public class MyScanResult implements Parcelable{
	public String BSSID;
	public String SSID;
	public int frequency;
	public int level;
	
	public MyScanResult(ScanResult scanResult){
		this.BSSID = scanResult.BSSID;
		this.SSID = scanResult.SSID;
		this.frequency = scanResult.frequency;
		this.level = scanResult.level;
	}
	
	//FIXME this is a temporary for tests.
	public MyScanResult(String BSSID){
		this.BSSID = BSSID;
	}
	
	@Override
	public boolean equals(Object obj) {
	        if (obj instanceof MyScanResult)
	            return BSSID.equalsIgnoreCase(((MyScanResult) obj).BSSID); 
	        else
	            return false;
	    }
	
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public String toString() {
		
		if (SSID != null) {
			return "MyScanResult [" + SSID + "] " + BSSID;
		}
		else
			return "MyScanResult [] " + BSSID;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(this.BSSID);
		out.writeString(this.SSID);
		out.writeInt(this.frequency);
		out.writeInt(this.level);
		
	}
	
	public static final Parcelable.Creator<MyScanResult> CREATOR = new Parcelable.Creator<MyScanResult>() {
            public MyScanResult createFromParcel(Parcel in) {
                return new MyScanResult(in);
            }
        
            public MyScanResult[] newArray(int size) {
                return new MyScanResult[size];
            }
	};

        private MyScanResult(Parcel in) {
            this.BSSID = in.readString();
            this.SSID = in.readString();
            this.frequency = in.readInt();
            this.level = in.readInt();
        		    
        }

}