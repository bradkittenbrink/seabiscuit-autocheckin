package com.coffeeandpower.datatiming;

import java.util.Observable;

import android.os.Handler;
import android.util.Log;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.cont.DataHolder;

public class Counter extends Observable {
	
	private Integer tick = 0;
	private Integer trigger = 0; // trigger every N steps
	private DataHolder response;
	
	private boolean isRunning = false;
	private boolean delayHttp = false;
	
	// Scheduler
	protected Handler taskHandler = new Handler();

	//CounterWorkerThread workerThread = new CounterWorkerThread();
	public void getLastResponseReset() {
		if(response != null)
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
        	    
        	    Log.d("Timer","Calling function with coordinate: " + AppCAP.getUserCoordinates());
                    
        	    if(delayHttp)
        	    {
        		    delayHttp = false;    
        	    }
        	    else
        	    {
        		    response = AppCAP.getConnection().getNearestVenuesWithCheckinsToCoordinate(AppCAP.getUserCoordinates());
        		    Log.d("Timer","Received Response: " + response.toString());
        	    }
                    
                    // Now post a notification with response.object	
                    setChanged();
			
                    Log.d("Timer","Sending notifyObservers...");
                    notifyObservers(new CounterData(CounterData.triggertype, response));
        	    
                    Log.d("Counter","Posting runnable delayed for 10 seconds...");
        	    taskHandler.postDelayed(runTimer, tick * 1000);
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
