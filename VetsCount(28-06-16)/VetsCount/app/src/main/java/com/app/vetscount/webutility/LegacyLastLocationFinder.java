package com.app.vetscount.webutility;

import java.util.List;
import java.util.TimerTask;



import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

public class LegacyLastLocationFinder implements ILastLocationFinder {

	protected static String TAG = "PreGingerbreadLastLocationFinder";
	Location bestResult = null;
	protected LocationListener locationListener;
	protected LocationManager locationManager;
	protected Criteria criteria;
	protected Context context;
	GotLocationCallback callback;

	/**
	 * Construct a new Legacy Last Location Finder.
	 * @param context Context
	 */
	public LegacyLastLocationFinder(Context context, GotLocationCallback callback1) {
		this.callback = callback1;
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		locationListener = new LocationListener() {

			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onLocationChanged(Location location) {
				// TODO Auto-generated method stub
				callback.gotLocation(location.getLatitude(), location.getLongitude());
			}
		};

		criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_HIGH);
		this.context = context;
	}


	/**
	 * Returns the most accurate and timely previously detected location.
	 * Where the last result is beyond the specified maximum distance or
	 * latency a one-off location update is returned via the {@link LocationListener}

	 * @param minDistance Minimum distance before we require a location update.
	 * @param minTime Minimum time required between location updates.
	 * @return The most accurate and / or timely previously detected location.
	 */
	public Location getLastBestLocation(int minDistance, long minTime) {

		float bestAccuracy = Float.MAX_VALUE;
		long bestTime = Long.MAX_VALUE;

		// Iterate through all the providers on the system, keeping
		// note of the most accurate result within the acceptable time limit.
		// If no result is found within maxTime, return the newest Location.
		List<String> matchingProviders = locationManager.getAllProviders();
		for (String provider : matchingProviders) {
			if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				// TODO: Consider calling
				//    ActivityCompat#requestPermissions
				// here to request the missing permissions, and then overriding
				//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
				//                                          int[] grantResults)
				// to handle the case where the user grants the permission. See the documentation
				// for ActivityCompat#requestPermissions for more details.
				return bestResult;
			}
			Location location = locationManager.getLastKnownLocation(provider);
			if (location != null) {
				float accuracy = location.getAccuracy();
				long time = location.getTime();

				if ((time < minTime && accuracy < bestAccuracy)) {
					bestResult = location;
					bestAccuracy = accuracy;
					bestTime = time;
				} else if (time > minTime && bestAccuracy == Float.MAX_VALUE && time < bestTime) {
					bestResult = location;
					bestTime = time;
				}
			}
		}

		// If the best result is beyond the allowed time limit, or the accuracy of the
		// best result is wider than the acceptable maximum distance, request a single update.
		// This check simply implements the same conditions we set when requesting regular
		// location updates every [minTime] and [minDistance].
		// Prior to Gingerbread "one-shot" updates weren't available, so we need to implement
		// this manually.
		if (locationListener != null && (bestTime > minTime || bestAccuracy > minDistance)) {
			String provider = locationManager.getBestProvider(criteria, true);
			if (provider != null) {
				bestResult = null;
				locationManager.requestLocationUpdates(provider, 0, 0, singeUpdateListener, context.getMainLooper());
			}
		}


		return bestResult;

	}

	/**
	 * This one-off {@link LocationListener} simply listens for a single location
	 * update before unregistering itself.
	 * The one-off location update is returned via the {@link LocationListener}

	 */
	protected LocationListener singeUpdateListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			Log.d(TAG, "Single Location Update Received: " + location.getLatitude() + "," + location.getLongitude());
			if (locationListener != null && location != null)
				locationListener.onLocationChanged(location);

			if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				// TODO: Consider calling
				//    ActivityCompat#requestPermissions
				// here to request the missing permissions, and then overriding
				//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
				//                                          int[] grantResults)
				// to handle the case where the user grants the permission. See the documentation
				// for ActivityCompat#requestPermissions for more details.
				return;
			}
			locationManager.removeUpdates(singeUpdateListener);
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onProviderDisabled(String provider) {
		}
	};

	/**
	 * {@inheritDoc}
	 */
	public void setChangedLocationListener(LocationListener l) {
		locationListener = l;
	}

	/**
	 * {@inheritDoc}
	 */
	public void cancel() {
		if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return;
		}
		locationManager.removeUpdates(singeUpdateListener);
	  }
	  
	  
	  
	}
