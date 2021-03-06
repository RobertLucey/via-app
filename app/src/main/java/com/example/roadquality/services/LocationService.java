package com.example.roadquality.services;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;

import com.example.roadquality.models.AccelerometerPoint;
import com.example.roadquality.models.DataPoint;
import com.example.roadquality.models.GPSPoint;
import com.example.roadquality.models.Journey;
import com.example.roadquality.utils.LokiLogger;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;

import java.time.Instant;
import java.util.List;

public class LocationService extends LocationCallback implements LocationListener {
    LokiLogger logger;
    Context context;
    Journey journey;

    LocationService(Context context, Journey journey) {
        this.logger = new LokiLogger(context, "LocationService.java");
        this.context = context;
        this.journey = journey;
    }

    @Override
    public void onProviderDisabled(String provider) {
        logger.log("Provider " + provider + " disabled");
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

    public void onLocationResult(LocationResult locationResult) {
        if (locationResult == null) {
            return;
        }

        for (Location l : locationResult.getLocations()) {
            this.onLocationChanged(l);
        }
    }
}