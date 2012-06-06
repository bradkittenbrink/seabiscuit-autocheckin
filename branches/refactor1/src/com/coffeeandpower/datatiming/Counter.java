package com.coffeeandpower.datatiming;

import java.util.Observable;

import android.os.Handler;
import android.util.Log;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.cont.DataHolder;
import com.google.android.maps.GeoPoint;

public class Counter extends Observable {
	
	private Integer tick = 0;
	private Integer trigger = 0; // trigger every N steps
	private DataHolder venuesWithCheckinsResponse;
	private DataHolder nearbyVenuesResponse;
	
	private boolean isRunning = false;
	private boolean delayHttp = false;
	private boolean isFirstRun = true;
	
	// Scheduler
	protected Handler taskHandler = new Handler();

	//CounterWorkerThread workerThread = new CounterWorkerThread();
	public void getLastResponseReset() {
		if(isFirstRun)
		{
                    delayHttp = true;
		}
		//Restart the timer
		stop();
		start();
		
		
	}
	
	public Counter(Integer tick, Integer trigger) {
		super();
		this.tick = tick;
		this.trigger = trigger;
		this.isFirstRun = true;
	}

	public void stop() {
		
		if (isRunning == true) {
			Log.d("Counter","Removing callbacks...");
			this.isRunning = false;
			taskHandler.removeCallbacks(runTimer);
			
		}
		else
		{
			Log.d("Counter","Warning: Tried to stop Counter but it was not started...");
		}
	}

	public void start() {
		
		if (isRunning == false) {
			Log.d("Counter","Posting runnable...");
			this.isRunning = true;
			taskHandler.removeCallbacks(runTimer);
			taskHandler.post(runTimer);
			
		}
		else
		{
			Log.d("Counter","Warning: Tried to start Counter when it was already running...");
		}
		
	}
	
	public void manualTrigger() {
		stop();
		start();
	}

	private Runnable runTimer = new Runnable()
        {
            @Override
            public void run()
            {
        	    // We are now on the main thread, so kick off the API call in a worker thread
        	    new Thread(new Runnable() {
        		    public void run() {
        			    
        			    
        			    
        			    if (AppCAP.getUserLatLon()[0] == 0 && AppCAP.getUserLatLon()[1] == 0) {
        				    Log.d("Counter","User position is currently 0-0, skipping API calls until a position is received.");
        			    } else {
                                	    Log.d("Timer","Calling functions with coordinates: " + AppCAP.getUserLatLon()[0] + ", " + AppCAP.getUserLatLon()[1]);
                                            
                                	    if(delayHttp)
                                	    {
                                		    delayHttp = false;
                                                    Log.d("Timer","Sending notifyObservers...");
                                                    
                                                    // Send notify for nearby venues
                                                    setChanged();
                                                    notifyObservers(new CounterData(venuesWithCheckinsResponse,nearbyVenuesResponse));
                                	    }
                                	    else
                                	    {
                                		    if (isFirstRun)
                        				    isFirstRun = false;
                                		    
                                		    venuesWithCheckinsResponse = AppCAP.getConnection().getNearestVenuesWithCheckinsToCoordinate(AppCAP.getUserLatLon());
                                		    Log.d("Timer","Received VenuesWithCheckins: " + venuesWithCheckinsResponse.toString());
                                		    
                                		    //If we aren't logged in we don't want to run this http request
                                		    //if (AppCAP.isLoggedIn()==false)
                                		    final GeoPoint gp = new GeoPoint((int)(AppCAP.getUserLatLon()[0]*1E6), (int)(AppCAP.getUserLatLon()[1]*1E6));
                                		    nearbyVenuesResponse = AppCAP.getConnection().getVenuesCloseToLocation(gp,20);
                                		    Log.d("Timer","Received VenuesWithCheckins: " + venuesWithCheckinsResponse.toString());
                                		    
                                		    Log.d("Counter","Received Response: " + venuesWithCheckinsResponse.getResponseMessage());
                                        	    Log.d("Counter","Received Response: " + nearbyVenuesResponse.getResponseMessage());
                                        	    
                                        	    if (!venuesWithCheckinsResponse.getResponseMessage().equals("HTTP 200 OK")) {
                                        		    Log.d("Counter","Skipping notifyObservers.");
                                        	    } else {
                                        		 
                                                            Log.d("Timer","Sending notifyObservers...");
                                                            
                                                            // Send notify for nearby venues
                                                            setChanged();
                                                            notifyObservers(new CounterData(venuesWithCheckinsResponse,nearbyVenuesResponse));
                                                	    
                                        	    }                                		    
                                	    }                                	    
        			    }
                        	    
                        	    Log.d("Counter","Posting runnable delayed for 10 seconds...");
                        	    taskHandler.postDelayed(runTimer, tick * 1000);
        		    }
        	    }).start();
            }
        };
	
        
        /*
	private class CounterWorkerThread implements Runnable {
		Thread thread = null;
		private boolean run = false;

		CounterWorkerThread() {
			thread = new Thread(this);
		}

		public void start() {
			Log.d("Counter","Counter.start(): " + this.run);
			if (this.run == false) {
        			this.run = true;
        			if ( thread == null) {
        				thread = new Thread(this);
        			}
        			thread.start();
        			Log.d("timer","Counter is started.");
			}
		}

		public void stop() {
			Log.d("Counter","Counter.stop(): " + this.run);
			if (this.run == true) {
				Log.d("timer","Stopping counter...");
        			this.run = false;
        			thread.interrupt();
        			thread = null;
        			Log.d("timer","Counter is stopped.");
			}
		}
		public void manualTrigger() {
			this.stop();
			this.start();
		}

		public void run() {
			Log.d("Counter","Counter.run()");
			try {
				while (run == true) {
					count++;
					
		                        Log.d("Timer","Calling function with coordinate: " + AppCAP.getUserCoordinates());
		                        
		                        response = AppCAP.getConnection().getNearestVenuesWithCheckinsToCoordinate(AppCAP.getUserCoordinates());
		        		//AppCAP.setUserCoordinates(getSWAndNECoordinatesBounds(mapView));
					//Object[] obj = (Object[]) response.getObject();

		                        
		                        Log.d("Timer","Received Response: " + response.toString());
		                        
		                        // Now post a notification with response.object
					
					setChanged();
					
					Log.d("Timer","Sending notifyObservers...");
					notifyObservers(new CounterData(CounterData.triggertype, response));
					
					Thread.sleep(tick * 1000);
				}
			} catch (InterruptedException interruptEx) {
				//do nothing here
			} catch (Exception ex) {
			
				ex.printStackTrace();
			}
		}
	}
	
	*/
}
