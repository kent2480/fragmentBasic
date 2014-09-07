package com.example.fragmentbasic;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

public class MyServiceTest extends Service {
	private static final String TAG = "kent_service";
	private LocationManager mLocationManager;
	private MyLocationListener mlistener;
	private int requestTime, counter;
	private Handler mHandler;
	private WakeLock wakeLock;
	private AlarmManager mAlarmManager;
	private static final int MSG_START_GPS = 0;
	private static final int MSG_STOP_GPS = 1;
	private static final int MSG_SUSPEND_GPS = 2;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate");
		mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		mlistener = new MyLocationListener();
		
		IntentFilter mfilter = new IntentFilter();
		mfilter.addAction("project.action.start.gps");
		mfilter.addAction("project.action.stop.gps");
		registerReceiver(new TcxoReceiver(), mfilter);
		
		mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
            	Log.d(TAG, "----- Handler Message: msg = " + msg.what + "-----");
                switch (msg.what) {
                    case MSG_START_GPS :
                    	startTcxo();
                        break;
                    case MSG_STOP_GPS :
                    	stopTcxo();
                    	break;
                    case MSG_SUSPEND_GPS :
                    	suspendTcxo();
                    	break;

                }
                super.handleMessage(msg);
            }
    	};
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStartCommand");
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mLocationManager.removeUpdates(mlistener);
		unregisterReceiver(new TcxoReceiver());
	}
	
	public void startTcxo(){
		Log.d(TAG, "startTcxo");
		acquireWakeLock();
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlistener);
		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mlistener);
	}
	
	public void stopTcxo(){
		Log.d(TAG, "stopTcxo");
		releaseWakeLock();
		mLocationManager.removeUpdates(mlistener);
		//mHandler.
	}
	
	public void suspendTcxo() {
		Log.d(TAG, "suspendTcxo");
		releaseWakeLock();
		mLocationManager.removeUpdates(mlistener);		
	}
	
	public void acquireWakeLock() {
		Log.d(TAG, "acquireWakeLock");
	    if (wakeLock == null) {
	        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
	        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, 
	        		                  this.getClass().getCanonicalName());
	        wakeLock.acquire();
	    }
	}
	
	private void releaseWakeLock() {
		Log.d(TAG, "releaseWakeLock");
	    if (wakeLock != null && wakeLock.isHeld()) {
	        wakeLock.release();
	        wakeLock = null;
	    }
	}
	
	class TcxoReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "onReceive: " + intent.getAction());
			String action = intent.getAction();
			if(action.equals("project.action.start.gps")) {
				requestTime = intent.getIntExtra("Time", 0);
				Message msgStart = new Message();
				msgStart.what = MSG_START_GPS;
				mHandler.sendMessage(msgStart);

				Message msgSuspend = new Message();
				msgSuspend.what = MSG_SUSPEND_GPS;
				mHandler.sendMessageDelayed(msgSuspend, requestTime * 60 * 1000);

				counter++;
				Log.d(TAG, "Counter = " + counter);
				if (counter <= 20) {
					intent.setAction("project.action.start.gps");
					PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
					mAlarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + requestTime * 2 * 60 * 1000, sender);
				}

			} else if(action.equals("project.action.stop.gps")) {
				Message msgStop = new Message();
				msgStop.what = MSG_STOP_GPS;
				mHandler.sendMessage(msgStop);
			}
		}
	}

	private class MyLocationListener implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {
			Log.d(TAG, "onLocationChanged");
			Intent intentBack = new Intent();
			intentBack.setAction("project.action.fix.location");
			Bundle bundle = new Bundle();
			bundle.putInt("Counter", counter);
			bundle.putDouble("Lat", location.getLatitude());
			bundle.putDouble("Long", location.getLongitude());
			bundle.putDouble("Accuracy", location.getAccuracy());
			bundle.putDouble("Bearing", location.getBearing());
			bundle.putDouble("Speed", location.getSpeed());
			bundle.putDouble("Time", location.getTime());
			intentBack.putExtras(bundle);
			sendBroadcast(intentBack);
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}