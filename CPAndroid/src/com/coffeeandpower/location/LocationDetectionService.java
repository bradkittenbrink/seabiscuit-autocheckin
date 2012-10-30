package com.coffeeandpower.location;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.Constants;
import com.coffeeandpower.cache.CachedDataContainer;
import com.coffeeandpower.cache.CacheMgrService;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.cont.VenueSmart;
import com.coffeeandpower.utils.Executor;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LocationDetectionService extends Service {

    protected static String TAG = "LocationDetectionService";
    
    private static volatile LocationDetectionService instance = null;
    
    private ActiveLocationListener activeLocationListener;
    
    private TreeMap<Integer, PendingIntent> activeProxAlerts;
    private static HashSet<Integer> queuedVenues;
    private static VenueSmart initialManualCheckin = null;

    private static class ProximityReceiver extends BroadcastReceiver {
 
        private VenueSmart venue;

        public ProximityReceiver(VenueSmart v) {
            venue = v;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean entering = intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, false);

            Executor exe = new Executor(context);
            if (entering) {
                final int checkInTime = (int) (System.currentTimeMillis() / 1000);
                final int checkOutTime = checkInTime + 24 * 3600;
   
                exe.checkIn(venue, checkInTime, checkOutTime, "", false, true, context); 
                LocationDetectionStateMachine.myAutoCheckinObservable.notifyObservers(null);

                debugMessage("Entering fence for " + venue.getName());
            } else {
                debugMessage("Exiting fence for " + venue.getName());
                exe.checkOut();
                CacheMgrService.checkOutTrigger();
                LocationDetectionStateMachine.myAutoCheckinObservable.notifyObservers(null);
                // TODO - refresh lists of people checked into venue
            }
        }
    };

    private static class MyVenueCacheObserver implements Observer {
        @Override
        public void update(Observable apiCache, Object data) {
            Log.d(TAG, "notified new venues");
            DataHolder cachedData = ((CachedDataContainer) data).getData();

            @SuppressWarnings("unchecked")
            List<VenueSmart> venuesList = (List<VenueSmart>) ((Object[])cachedData.getObject())[0];

            for(VenueSmart v : venuesList) {
                if(queuedVenues.contains(v.getVenueId())) {
                    addVenueToAutoCheckinList(v);
                    queuedVenues.remove(v.getVenueId());
                }
            }
        }
    }

    //private static LocationDetectionStateMachine sm = new LocationDetectionStateMachine(this);
    
    
    //=====================================================
    // Service Lifecycle
    //=====================================================
    
    @Override
    public IBinder onBind(Intent arg0) {
        // This service is not intended to be bound so return null
        return null;
    }
    
    @Override
    public void onCreate() {
        Log.d(TAG,"onCreate()");
        
        instance = this;
        
        activeLocationListener = new ActiveLocationListener();
        activeLocationListener.init();

        LocationDetectionStateMachine.init(LocationDetectionService.this);

        activeProxAlerts = new TreeMap<Integer, PendingIntent>();
        
        if(queuedVenues != null) {
            CacheMgrService.startObservingAPICall("venuesWithCheckins", new MyVenueCacheObserver());
        }

        if (initialManualCheckin != null) {
            LocationDetectionStateMachine.manualCheckin(initialManualCheckin);
            initialManualCheckin = null;
        }
    }
    
    @Override
    public void onDestroy() {
        
        Log.d(TAG,"onDestroy()");
        
        LocationDetectionStateMachine.stop();

        for(Map.Entry<Integer, PendingIntent> i : activeProxAlerts.entrySet()) {
            PendingIntent proxIntent = i.getValue();
            getLocationManager().removeProximityAlert(proxIntent);
        }

        queuedVenues.clear();
        activeProxAlerts.clear();

        queuedVenues = null;
        activeProxAlerts = null;
        instance = null;
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"onStartCommand()");
        return START_STICKY;
    }

    public static void addVenueToAutoCheckinList(int venue_id) {
        Log.d(TAG, String.format("Adding venue %d to auto checkin list.", venue_id));
        VenueSmart venue = CacheMgrService.searchVenueInCache(venue_id);
        if(venue == null || venue.getVenueId() != venue_id) {
            Log.w(TAG, String.format("failed to look up venue id %d in cache!, aboring creation of proximity fence!", venue_id));
        }

        if(instance != null && venue != null) {
            instance.createProxAlert(venue);
        } else {
            Log.i(TAG, "creating fence when LocationDetectionService is not running or venue cache hasn't been loaded, queueing it for later.");
            if(queuedVenues == null) {
                queuedVenues = new HashSet<Integer>();
            }
            queuedVenues.add(venue_id);
        }
    }

    public static void addVenueToAutoCheckinList(VenueSmart checkinVenue) {
        Log.d(TAG, String.format("Adding venue %s to auto checkin list.", checkinVenue.getVenueId()));
        if(instance != null) {
            instance.createProxAlert(checkinVenue);
        } else {
            Log.i(TAG, "creating fence when LocationDetectionService is not running, queueing it for later.");
            if(queuedVenues == null) {
                queuedVenues = new HashSet<Integer>();
            }
            queuedVenues.add(checkinVenue.getVenueId());
        }
    }

    private void createProxAlert(VenueSmart currVenue) {
        final float PROX_ALERT_RADIUS = 100;
        final long PROX_ALERT_EXPIRY_MS = 2880000; // 2 days in ms

        String intentString = "proxIntent_" + currVenue.getVenueId();
        Intent intent = new Intent(intentString);

        // NOTE - this must be sent with no extras, otherwise the KEY_PROXIMITY_ENTERING data will not make it through.
        PendingIntent proxIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        activeProxAlerts.put(currVenue.getVenueId(), proxIntent);

        getLocationManager().addProximityAlert(currVenue.getLat(),
                                               currVenue.getLng(),
                                               PROX_ALERT_RADIUS,
                                               PROX_ALERT_EXPIRY_MS,
                                               proxIntent);
        
        registerReceiver(new ProximityReceiver(currVenue), new IntentFilter(intentString));

        debugMessage(String.format("Fence created for venue %s.", currVenue.getName()));
    }

    public static void removeVenueFromAutoCheckinList(VenueSmart venue) {
        if(instance != null) {
            PendingIntent proxIntent = instance.activeProxAlerts.get(venue.getVenueId());
            instance.getLocationManager().removeProximityAlert(proxIntent);
        } else {
            Log.w(TAG, "removing fence when LocationDetectionService is not running! ignored.");
        }
    }

    public static void manualCheckin(Context context, VenueSmart venue) {
        AppCAP.enableAutoCheckinForVenue(venue.getVenueId());
        AppCAP.enableAutoCheckin(context);

        addVenueToAutoCheckinList(venue);

        if (instance != null) {
            LocationDetectionStateMachine.manualCheckin(venue);
        } else {
            // service hasn't finished starting yet, so save it for onCreate
            initialManualCheckin = venue;
        }
    }
    
    public void startActiveListener() {
        activeLocationListener.startListener(this);
    }

    public void stopActiveListener() {
        getLocationManager().removeUpdates(activeLocationListener);
    }

    // package-private
    static void runOnMainThread(Runnable r) {
        new Handler(Looper.getMainLooper()).post(r);
    }

    private LocationManager getLocationManager() {
        return (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    private static void debugMessage(final String msg) {
        Log.d(TAG, msg);
        if(Constants.debugLocationToast) {
            runOnMainThread(new Runnable() {
                @Override public void run() {
                    Toast.makeText(instance, msg, Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
