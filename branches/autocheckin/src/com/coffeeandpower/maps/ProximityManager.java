package com.coffeeandpower.maps;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.cont.VenueSmart;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

public class ProximityManager {





        public static void venueCheckin(VenueSmart checkinVenue)
        {
        	AppCAP.didCheckIntoVenue(checkinVenue.getVenueId());        	
        	
        }

}
