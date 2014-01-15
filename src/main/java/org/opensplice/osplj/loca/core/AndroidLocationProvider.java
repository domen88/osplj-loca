/**
 *                         OpenSplice For Java
 *
 *    This software and documentation are Copyright 2010 to 2013 PrismTech
 *    Limited and its licensees. All rights reserved. See file:
 *
 *                           docs/LICENSE.html
 *
 *    for full copyright notice and license terms.
 */

package org.opensplice.osplj.loca.core;

import android.app.Activity;
import android.content.Context;
import android.app.Application;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import org.opensplice.osplj.loca.core.LocationData;
import org.opensplice.osplj.loca.core.LocationProvider;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class AndroidLocationProvider extends LocationProvider implements LocationListener {

    private Location location;
    private LocationManager locationManager;
    private String provider;
    private LocationData locationData;
    private Context mcontext;

    private void getCurrentLocation() {


        try {
            final Class<?> activityThreadClass =
                    Class.forName("android.app.ActivityThread");
            final Method method = activityThreadClass.getMethod("currentApplication");
                 mcontext = (Application) method.invoke(null, (Object[]) null);
        } catch (final ClassNotFoundException e) {
            e.printStackTrace();
        } catch (final NoSuchMethodException e) {
            e.printStackTrace();
        } catch (final IllegalArgumentException e) {
            e.printStackTrace();
        } catch (final IllegalAccessException e) {
            e.printStackTrace();
        } catch (final InvocationTargetException e) {
            e.printStackTrace();
        }

        //Getting LocationManager object
        locationManager = (LocationManager) mcontext.getSystemService(Context.LOCATION_SERVICE);

        //Creating a Criteria object
        Criteria criteria = new Criteria();

        //Getting the name of provider that meets the Criteria
        provider = locationManager.getBestProvider(criteria, false);

        if ( provider != null && !provider.equals("") ) {

            //Get location from the given provider
            location = locationManager.getLastKnownLocation(provider);

            locationManager.requestLocationUpdates(provider, 0, 0, this);

            if (location!=null) {
                onLocationChanged(location);
            } else {
                //Do nothing
            }
        } else {
            //Do nothing
        }

    }

    @Override
    public void onLocationChanged (Location loc) {
        location = loc;
    }

    @Override
    public void onProviderDisabled (String provider) {
        //TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled (String provider) {
        //TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged (String provider, int status, Bundle extra) {
        //TODO Auto-generated method stub
    }

    @Override
    public LocationData getLocation() {

        this.getCurrentLocation();

        if (location == null) {

            return null;

        } else {

            locationData = new LocationData();
            locationData.setLatitude(location.getLatitude());
            locationData.setLongitude(location.getLongitude());

            return locationData;

        }

    }

}