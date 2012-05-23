package com.coffeeandpower.datatiming;

import java.util.Observable;

import android.util.Log;

import com.coffeeandpower.AppCAP;
import com.coffeeandpower.cont.DataHolder;

public class Counter extends Observable {
	private Integer count = 0;
	private Integer tick = 0;
	private Integer trigger = 0; // trigger every N steps
	private DataHolder response;

	CounterWorkerThread workerThread = new CounterWorkerThread();
	
	public Counter(Integer tick, Integer trigger) {
		super();
		this.count = 0;
		this.tick = tick;
		this.trigger = trigger;
	}

	public void stop() {
		workerThread.stop();
	}

	public void start() {
		workerThread.start();
	}
	public void manualTrigger() {
		workerThread.manualTrigger();
	}

	private class CounterWorkerThread implements Runnable {
		Thread thread = null;
		boolean run = false;;

		CounterWorkerThread() {
			thread = new Thread(this);
		}

		public void start() {
			run = true;
			if ( thread == null) {
				thread = new Thread(this);
			}
			thread.start();
		}

		public void stop() {
			run = false;
			thread.interrupt();
			thread = null;
		}
		public void manualTrigger() {
			this.stop();
			this.start();
		}

		public void run() {
			try {
				while (run == true) {
					count++;
					
		                        Log.d("Timer","Calling function with coordinate: " + AppCAP.getUserCoordinates());
		                        
		                        response = AppCAP.getConnection().getNearestVenuesWithCheckinsToCoordinate(AppCAP.getUserCoordinates());
		                        
		                        Log.d("Timer","Received Response: " + response.toString());
		                        
		                        // Now post a notification with response.object
					
					setChanged();
					if ((count % trigger) == 0) {
						notifyObservers(new CounterData(CounterData.triggertype, response));
					} else {
						notifyObservers(new CounterData(CounterData.counttype,response));
					}
					Thread.sleep(tick * 1000);
				}
			} catch (InterruptedException interruptEx) {
				//do nothing here
			} catch (Exception ex) {
			
				ex.printStackTrace();
			}
		}
	}
}
