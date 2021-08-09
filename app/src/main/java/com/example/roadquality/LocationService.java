package com.example.roadquality;

import static java.time.Instant.now;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.Toast;

import com.example.roadquality.models.AccelerometerPoint;
import com.example.roadquality.models.DataPoint;
import com.example.roadquality.models.GPSPoint;
import com.example.roadquality.models.Journey;
import com.example.roadquality.models.Journey;

import java.time.Instant;
import java.util.List;

public class LocationService implements LocationListener {
    Context context;
    Journey journey;

    LocationService(Context context, Journey journey) {
        this.context = context;
        this.journey = journey;
    }

    @Override
    public void onLocationChanged( List<Location> locations) {
    }

    @Override
    public void onLocationChanged(Location location) {
        GPSPoint dp = new GPSPoint(location.getLatitude(), location.getLongitude());
        journey.append(
                new DataPoint(new AccelerometerPoint(
                        0.0,
                        0.0,
                        0.0
                ), dp, now().toEpochMilli() / 1000L)
        );
        Toast.makeText(
                context,
                (location.getLongitude()) + ", " + (location.getLatitude()),
                Toast.LENGTH_SHORT
        ).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Toast.makeText(context, "Provider status changed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(context, "Gps turned off . you cannot follow your location", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(context, "Gps turned on . you can follow ur location", Toast.LENGTH_LONG).show();
    }

}