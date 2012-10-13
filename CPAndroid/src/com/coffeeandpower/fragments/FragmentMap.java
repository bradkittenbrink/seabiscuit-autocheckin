package com.coffeeandpower.fragments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.Constants;
import com.coffeeandpower.app.R;
import com.coffeeandpower.RootActivity;
import com.coffeeandpower.activity.ActivityPlaceDetails;
import com.coffeeandpower.activity.ActivityUserDetails;
import com.coffeeandpower.adapters.MyPlacesAdapter;
import com.coffeeandpower.adapters.MyUsersAdapter;
import com.coffeeandpower.cache.CacheMgrService;
import com.coffeeandpower.cache.CachedDataContainer;
import com.coffeeandpower.cont.DataHolder;
import com.coffeeandpower.cont.UserSmart;
import com.coffeeandpower.cont.VenueSmart;
import com.coffeeandpower.maps.BalloonItemizedOverlay;
import com.coffeeandpower.maps.MyItemizedOverlay;
import com.coffeeandpower.maps.MyOverlayItem;
import com.coffeeandpower.maps.PinDrawable;
import com.coffeeandpower.utils.Executor;
import com.coffeeandpower.utils.Executor.ExecutorInterface;

import com.coffeeandpower.views.CustomFontView;
import com.coffeeandpower.views.HorizontalPagerModified;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

public class FragmentMap extends Fragment {
    
    private final String TAG = "FragmentMap";
    
    private static final int SCREEN_SETTINGS = 0;
    private static final int SCREEN_MAP = 1;

    private static final int ACTIVITY_ACCOUNT_SETTINGS = 1888;
    public static final int ACCOUNT_CHANGED = 1900;
    // Views
    private HorizontalPagerModified pager;
    private ImageView imageRefresh;
    
    private double[] pinScales = { 0.326 , // for 1 person
            0.57, 0.74, 0.855, 0.932, 0.976, 
            1.0 // for 7 or more people
    };

    // Map items
    private MapView mapView;
    private MapController mapController;
    private MyLocationOverlay myLocationOverlay;
    private MyItemizedOverlay itemizedoverlay;


    private Executor exe;
    
    float firstX = 0;
    float firstY = 0;

    private MyCachedDataObserver myCachedDataObserver = new MyCachedDataObserver();
    
    
    private MyUsersAdapter adapterUsers;

    private ListView listView;
    private ProgressDialog progress;

    private ArrayList<UserSmart> arrayUsers;

    private boolean initialLoad = true;
    private ImageView blankSlateImg;


    private Bundle intentExtras;

    public FragmentMap(Bundle intentExtras) {
        this.intentExtras = intentExtras;
    }

    // Scheduler - create a custom message handler for use in passing venue data
    // from background API call to main thread
    protected Handler mainThreadTaskHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            if (mapView != null && mapView.isShown() == true) {
                // Determine which message type is being sent
                String type = msg.getData().getString("type");
                
                if (!type.equalsIgnoreCase("AutoCheckinTrigger")) {
                 // if the message isn't an autocheckin trigger, assume its a cached data update
                        // pass message data along to venue update method
                ArrayList<VenueSmart> venueArray = msg.getData()
                        .getParcelableArrayList("venues");
                ArrayList<UserSmart> userArray = msg.getData()
                        .getParcelableArrayList("users");
                        updateVenuesAndCheckinsFromApiResult(venueArray, userArray);
            
                        progress.dismiss();
                }
            }
            super.handleMessage(msg);
        }
    };



    private DataHolder result;

    private UserSmart loggedUser;    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View mainView = null;
        mainView = inflater.inflate(R.layout.tab_fragment_map, null);
         // start services
        progress = new ProgressDialog(this.getActivity());
        progress.setMessage("Loading...");
        progress.show();

        // Executor
        exe = new Executor(getActivity());
        // We need this to get the user Id
        exe.setExecutorListener(new ExecutorInterface() {
            @Override
            public void onErrorReceived() {
                //errorReceived();
            }

            @Override
            public void onActionFinished(int action) {
                actionFinished(action);
            }
        });

        return mainView;

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        mapView = (MapView) getView().findViewById(R.id.mapview);
        imageRefresh = (ImageView) getView().findViewById(R.id.imagebutton_map_refresh_progress);
        myLocationOverlay = new MyLocationOverlay(getActivity(), mapView);
        Drawable drawable = this.getResources().getDrawable(
                R.drawable.pin_checkedout);
        itemizedoverlay = new MyItemizedOverlay(drawable, mapView);

        // Set others
        mapView.getOverlays().add(myLocationOverlay);
        myLocationOverlay.runOnFirstFix(new Runnable() {
            public void run() {
                RootActivity.log("FragmentMap First Fix Hit");
                mapView.getController().animateTo(myLocationOverlay.getMyLocation());
                AppCAP.setUserCoordinates(getSWAndNECoordinatesBounds(mapView));
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mapController.setZoom(17);
                        refreshMapDataSet();
                    }
                });
            }
        });

        mapController = mapView.getController();
        mapController.setZoom(12);
        // Hardcoded to US until we get a fix
        mapController.zoomToSpan(100448195, 94921874);

        // User is logged in, get user data
        if (AppCAP.isLoggedIn()) {
            exe.getUserData();
        }

        // Listener for autorefresh map
        mapView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    firstX = event.getX();
                    firstY = event.getY();
                    break;

                case MotionEvent.ACTION_CANCEL:
                    if (event.getX() > firstX + 10
                            || event.getX() < firstX - 10
                            || event.getY() > firstY + 10
                            || event.getY() < firstY - 10) {
                        refreshMapDataSet();
                        firstX = event.getX();
                        firstY = event.getY();
                    }

                    break;

                case MotionEvent.ACTION_UP:
                    if (event.getX() > firstX + 10
                            || event.getX() < firstX - 10
                            || event.getY() > firstY + 10
                            || event.getY() < firstY - 10) {
                        refreshMapDataSet();
                        firstX = event.getX();
                        firstY = event.getY();
                    }
                    hideBaloons();
                    break;
                }
                return false;
            }
        });
    }

    @Override
    public void onStart() {
        if (Constants.debugLog)
            Log.d("Contacts", "FragmentMap.onStart()");
        super.onStart();
        CacheMgrService.startObservingAPICall("venuesWithCheckins",myCachedDataObserver);

    }

    @Override
    public void onStop() {
        if (Constants.debugLog)
            Log.d("venueFeeds", "FragmentMap.onStop()");
        super.onStop();
        CacheMgrService.stopObservingAPICall("venuesWithCheckins",myCachedDataObserver);
            myLocationOverlay.disableMyLocation();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!AppCAP.shouldFinishActivities()) {
            if (!AppCAP.isLoggedIn()) {
                progress.dismiss();
            }
            myLocationOverlay.enableMyLocation();

            // Refresh Data
            refreshMapDataSet();
        }
    }

    public void refresh() {

        // Restart the activity so user lists load correctly
        CacheMgrService.resetVenueFeedsData(true);
    }

    
    //====================================================================
    // Map View Management
    //====================================================================


    /**
     * Create point on Map with data from MapUserdata
     * 
     * @param point
     * @param foursquareIdKey
     * @param checkinsSum
     * @param venueName
     * @param isList
     */
    private void createMarker(GeoPoint point, VenueSmart currVenueSmart,
            int checkinsSum, String venueName, boolean isPin) {
        Drawable drawable;
        if (currVenueSmart != null) {
            String checkStr = "";
            if (!isPin) {
                checkStr = checkinsSum == 1 ? " checkin in the last week"
                        : " checkins in the last week";
            } else {
                checkStr = checkinsSum == 1 ? " person here now"
                        : " persons here now";
            }
            venueName = AppCAP.cleanResponseString(venueName);

            MyOverlayItem overlayitem = new MyOverlayItem(point, venueName,
                    checkinsSum + checkStr);
            overlayitem.setVenueSmartData(currVenueSmart);

            if (myLocationOverlay.getMyLocation() != null) {
                overlayitem.setMyLocationCoords(myLocationOverlay
                        .getMyLocation().getLatitudeE6(), myLocationOverlay
                        .getMyLocation().getLongitudeE6());
            }

            // Pin or marker
            if (isPin) {
                overlayitem.setPin(true);
                overlayitem.setMarker(getPinDrawable(checkinsSum, point));
            } else {
                if (currVenueSmart.getSpecialVenueType().compareTo("solar") == 0) {
                    drawable = this.getResources().getDrawable(R.drawable.pin_solar);
                } else {
                    drawable = this.getResources().getDrawable(R.drawable.pin_checkedout);
                }
                Bitmap d = ((BitmapDrawable)drawable).getBitmap();
                double scaleFactor = getScaleFactor(checkinsSum);
                Bitmap bitmapOrig = Bitmap.createScaledBitmap(d, (int) (scaleFactor * drawable.getIntrinsicWidth()), 
                        (int) (scaleFactor * drawable.getIntrinsicHeight()), false);
                BitmapDrawable newDrawable = new BitmapDrawable(this.getResources(), bitmapOrig);
                newDrawable.setBounds(-newDrawable.getIntrinsicWidth() / 2, -newDrawable.getIntrinsicHeight() , 
                        newDrawable.getIntrinsicWidth() / 2, 0 );
                overlayitem.setMarker(newDrawable);
            }

            itemizedoverlay.addOverlay(overlayitem);
        }
    }

    private Drawable getPinDrawable(int checkinsNum, GeoPoint gp) {
        PinDrawable icon = new PinDrawable(getActivity(), checkinsNum);
        icon.setBounds(0, -icon.getIntrinsicHeight(), icon.getIntrinsicWidth(),
                0);
        return icon;
    }
    
        
    private double getScaleFactor(int number) {       
        if (number <= 0) {
            return pinScales[0];
        } else if (number >= pinScales.length) {
            return pinScales[pinScales.length - 1];
        } else {
            return pinScales[number - 1];
        }
    }

    public void onClickLocateMe(View v) {
        if (myLocationOverlay != null) {
            if (myLocationOverlay.getMyLocation() != null) {
                mapController.animateTo(myLocationOverlay.getMyLocation());
                mapController.setZoom(17);
            }
        }
    }

    public void onClickRefresh(View v) {
        refreshMapDataSet();
    }

    public void hideBaloons() {
        List<Overlay> mapOverlays = mapView.getOverlays();
        for (Overlay overlay : mapOverlays) {
            if (overlay instanceof BalloonItemizedOverlay<?>) {
                ((BalloonItemizedOverlay<?>) overlay).hideBalloon();
            }
        }
    }

    private void refreshMapDataSet() {

        Animation anim = AnimationUtils
                .loadAnimation(getActivity(), R.anim.refresh_anim);
        imageRefresh.setAnimation(anim);

        hideBaloons();

        // For every refresh save Map coordinates
        AppCAP.setUserCoordinates(getSWAndNECoordinatesBounds(mapView));
        MapView map = (MapView) getView().findViewById(R.id.mapview);
        GeoPoint pointCenterMap = map.getMapCenter();
        int lngSpan = pointCenterMap.getLongitudeE6();
        int latSpan = pointCenterMap.getLatitudeE6();
        AppCAP.setMapCenterCoordinates(lngSpan, latSpan);
    }

    /**
     * [0]sw_lat; [1]sw_lng; [2]ne_lat; [3]ne_lng;
     * 
     * @param map
     * @return
     */
    private double[] getSWAndNECoordinatesBounds(MapView map) {
        double[] data = new double[6];

        GeoPoint pointCenterMap = map.getMapCenter();
        int lngSpan = map.getLongitudeSpan();
        int latSpan = map.getLatitudeSpan();

        GeoPoint sw = new GeoPoint(
                pointCenterMap.getLatitudeE6() - latSpan / 2,
                pointCenterMap.getLongitudeE6() - lngSpan / 2);
        GeoPoint ne = new GeoPoint(
                pointCenterMap.getLatitudeE6() + latSpan / 2,
                pointCenterMap.getLongitudeE6() + lngSpan / 2);

        data[0] = sw.getLatitudeE6() / 1E6; // sw_lat
        data[1] = sw.getLongitudeE6() / 1E6; // sw_lng
        data[2] = ne.getLatitudeE6() / 1E6; // ne_lat
        data[3] = ne.getLongitudeE6() / 1E6; // ne_lng
        data[4] = 0;
        data[5] = 0;

        if (myLocationOverlay.getMyLocation() != null) {
            data[4] = myLocationOverlay.getMyLocation().getLatitudeE6() / 1E6;
            data[5] = myLocationOverlay.getMyLocation().getLongitudeE6() / 1E6;
        }
        return data;
    }


    private void actionFinished(int action) {
        result = exe.getResult();

        switch (action) {
        case Executor.HANDLE_GET_USER_DATA:
            if (result.getObject() != null) {
                if (result.getObject() instanceof UserSmart) {
                    loggedUser = (UserSmart) result.getObject();
                    
                    AppCAP.setLoggedInUserId(loggedUser.getUserId());
                    AppCAP.setLoggedInUserNickname(loggedUser.getNickName());
                }
            }
        }
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

        case ACTIVITY_ACCOUNT_SETTINGS:
            if (resultCode == ACCOUNT_CHANGED) {
                exe.getUserData();
            }
            break;
        }
    }
    
    //====================================================================
    // Cached Data Management
    //====================================================================
    
    private class MyCachedDataObserver implements Observer {
        
        @Override
        public void update(Observable observable, Object data) {

            
            if (data instanceof CachedDataContainer) {
                CachedDataContainer counterdata = (CachedDataContainer) data;
                DataHolder venuesWithCheckins = counterdata.getData();

                Object[] obj = (Object[]) venuesWithCheckins.getObject();
                @SuppressWarnings("unchecked")
                List<VenueSmart> arrayVenues = (List<VenueSmart>) obj[0];
                @SuppressWarnings("unchecked")
                List<UserSmart> arrayUsers = (List<UserSmart>) obj[1];

                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putCharSequence("type", counterdata.type);
                bundle.putParcelableArrayList("venues", new ArrayList<VenueSmart>(arrayVenues));
                bundle.putParcelableArrayList("users", new ArrayList<UserSmart>(arrayUsers));
                message.setData(bundle);

                if (Constants.debugLog)
                    Log.d(TAG,"FragmentMap: Received cached data, processing...");

                
                mainThreadTaskHandler.sendMessage(message);
                
                
            }
        }
    }
    
    private void updateVenuesAndCheckinsFromApiResult(ArrayList<VenueSmart> venueArray, ArrayList<UserSmart> arrayUsers) {
        
        if (Constants.debugLog)
            Log.d(TAG,"updateVenuesAndCheckinsFromApiResult()");
        itemizedoverlay.clear();

        for (VenueSmart venue : venueArray) {
            GeoPoint gp = new GeoPoint((int) (venue.getLat() * 1E6),
                    (int) (venue.getLng() * 1E6));

            if (venue.getCheckins() > 0) {
                createMarker(gp, venue, venue.getCheckins(), venue.getName(),
                        true);
            } else if (venue.getCheckinsForWeek() > 0) {
                createMarker(gp, venue, venue.getCheckinsForWeek(),
                        venue.getName(), false); 
            }
        }

        for (UserSmart user : arrayUsers) {
            if (user.getUserId() == AppCAP.getLoggedInUserId()) {
                if (user.getCheckedIn() == 1) {
                    AppCAP.setUserCheckedIn(true);
                } else {
                    AppCAP.setUserCheckedIn(false);
                }
            }
        }

        if (itemizedoverlay.size() > 0) {
            mapView.getOverlays().add(itemizedoverlay);
        }
        mapView.invalidate();

    }

}
