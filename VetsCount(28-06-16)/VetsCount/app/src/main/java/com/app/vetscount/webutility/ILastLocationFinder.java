package com.app.vetscount.webutility;

import android.location.Location;
import android.location.LocationListener;

public interface ILastLocationFinder {
	 public Location getLastBestLocation(int minDistance, long minTime);
	  
	  /**
	   * Set the {@link LocationListener} that may receive a one-shot current location update.
	   * @param l LocationListener
	   */
	  public void setChangedLocationListener(LocationListener l);
	  
	  /**
	   * Cancel the one-shot current location update.
	   */
	  public void cancel();
	}

