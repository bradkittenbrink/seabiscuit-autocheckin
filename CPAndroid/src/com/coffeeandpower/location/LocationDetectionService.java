package com.coffeeandpower.location;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.cont.VenueSmart;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class LocationDetectionService extends Service {

    protected static String TAG = "LocationDetectionService";
    
    private Context locationDetectionServiceContext;
    
    private ActiveLocationListener activeLocationListener;
    
    private Handler mainThreadTaskHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            
            super.handleMessage(msg);
            
            String messageType = msg.getData().getString("type");
            Log.d(TAG,"mainThreadTaskHandler.handleMessage: " + messageType);
            
            if (messageType.equalsIgnoreCase("startActiveListener")) {
                
                
                activeLocationListener.startListener();
                //Looper.myLooper().quit();
                
                /*
                LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                        0,
                        activeLocationListener);*/
            } else if (messageType.equalsIgnoreCase("stopActiveListener")) {
                LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
                locationManager.removeUpdates(activeLocationListener);
            }
        }
    };
    
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
        
        locationDetectionServiceContext = this;
        
        activeLocationListener = new ActiveLocationListener(this);
        activeLocationListener.init();
        
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                
                Looper.prepare();
                
                LocationDetectionStateMachine.init(locationDetectionServiceContext,mainThreadTaskHandler);
                
                Looper.loop();
                
            }
        },"LocationDetectionService.onCreate");
        thread.setDaemon(true);
        thread.start();     
    }
    
    @Override
    public void onDestroy() {
        
        Log.d(TAG,"onDestroy()");
        
        LocationDetectionStateMachine.stop();
        
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        
        Log.d(TAG,"onStartCommand()");
        
          
        
        
        
        
        return START_STICKY;
        
    }
    
    
    
    
    
    
    
    
    
    public static void addVenueToAutoCheckinList(VenueSmart checkinVenue)
        {
        
            // Create a prox alert if this is a new venue for this user
            if (AppCAP.enableAutoCheckinForVenue(checkinVenue.getVenueId())) {
                //createProxAlert(checkinVenue);
            }
        }
    
    
    
    

}
