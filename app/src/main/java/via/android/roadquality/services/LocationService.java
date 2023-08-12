package via.android.roadquality.services;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;

import androidx.annotation.NonNull;

import via.android.roadquality.models.AccelerometerPoint;
import via.android.roadquality.models.DataPoint;
import via.android.roadquality.models.GPSPoint;
import via.android.roadquality.models.Journey;
import via.android.roadquality.utils.LokiLogger;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;

import java.time.Instant;

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
    public void onProviderDisabled(@NonNull String provider) {
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

    public void onLocationResult(@NonNull LocationResult locationResult) {
        if (locationResult == null) {
            return;
        }

        for (Location l : locationResult.getLocations()) {
            this.onLocationChanged(l);
        }
    }
}