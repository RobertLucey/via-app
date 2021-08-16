package com.example.roadquality.services;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;

import com.example.roadquality.models.AccelerometerPoint;
import com.example.roadquality.models.DataPoint;
import com.example.roadquality.models.GPSPoint;
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
    public void onLocationChanged(Location location) {
        GPSPoint dp = new GPSPoint(location.getLatitude(), location.getLongitude());
        journey.append(
                new DataPoint(
                        new AccelerometerPoint(
                                0.0
                        ),
                        dp,
                        Instant.now().toEpochMilli() / 1000L
                )
        );
    }
}