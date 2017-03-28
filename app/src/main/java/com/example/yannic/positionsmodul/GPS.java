package com.example.yannic.positionsmodul;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.renderscript.Double2;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;

import org.json.JSONObject;

import java.util.StringTokenizer;
import java.util.regex.Pattern;

/**
 * Created by Yannic on 14.11.2016.
 */

public class GPS {

    private static GPS self;
    private MainActivity activity;
    LocationManager manager;
    private Location location;
    private String nmea_string;

    private final String LOG_TAG = String.valueOf(this.getClass());

    public GPS(final MainActivity mainActivity, final Context context) {
        Log.v("GPS", "START GPS");
        self = this;
        this.activity = mainActivity;
        this.manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        this.startLocationTracking();
    }

    /**
     * creates LocationListener and request a update for Location.
     * plus permission check.
     */
    private void startLocationTracking() {
        if (manager == null)
            return;

        final LocationListener listener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
    }


    public static GPS getReference() {
        return self;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getNMEA() {
        return nmea_string;
    }

    /**
     * example:
     * $GPGGA,172814.0,3723.46587704,N,12202.26957864,W,2,6,1.2,18.893,M,-25.669,M,2.0,0031*4F
     *
     * @return
     */
    public void createNMEAString(final Location location) {
        String message_typ = "$GPGGA";
        String UTC = String.valueOf(location.getTime());
        String latitude = this.makeCord(location.getLatitude());
        String latitude_direction = "N";
        String longitude = this.makeCord(location.getLongitude());
        String longitude_direction = "W";
        String GPS_Quali = "2";
        String number_sat = String.valueOf(location.getExtras().get("satellites"));
        String hdop = "1.2";
        String height = String.valueOf(location.getAltitude());
        String height_metric = "M";
        String checksum = "*4F";

        String separator = ",";
        nmea_string = message_typ + separator + UTC + separator + latitude + separator +
                latitude_direction + separator + longitude + separator + longitude_direction + separator
                + GPS_Quali + separator + number_sat + separator + hdop + separator + height + separator + height_metric + separator + checksum;
    }

    /**
     * NMEA format coordinates.
     *
     * @param v
     * @return
     */
    private String makeCord(double v) {
        String s = String.valueOf(v);
        String[] parts = s.split(Pattern.quote("."));
        String firstPart = parts[0];
        String secondPart = parts[1];
        firstPart = firstPart + secondPart.substring(0, 2);
        secondPart = secondPart.substring(2);
        return firstPart + "." + secondPart;
    }

    class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            //Log.v("Location", location.toString());
            setLocation(location);
            createNMEAString(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }
}