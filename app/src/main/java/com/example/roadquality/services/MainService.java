package com.example.roadquality.services;

import static java.time.Instant.now;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.IBinder;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.roadquality.models.Vector3D;
import com.example.roadquality.models.AccelerometerPoint;
import com.example.roadquality.models.DataPoint;
import com.example.roadquality.models.GPSPoint;
import com.example.roadquality.models.Journey;

import org.json.JSONException;

import java.io.IOException;

public class MainService extends Service {

    LocationManager locationManager;
    LocationService locationService;
    Journey journey;

    AccelerometerSensor accelerometerSensor;

    public MainService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        this.journey = new Journey();
        this.journey.setSuspension(intent.getBooleanExtra("suspension", false));
        this.journey.setSendRelativeTime(intent.getBooleanExtra("sendRelativeTime", false));
        this.journey.setMinutesToCut(intent.getIntExtra("minutesToCut", 99999));
        this.journey.setMetresToCut(intent.getIntExtra("metresToCut", 99999));
        this.journey.setSendInPartials(intent.getBooleanExtra("sendPartials", true));

        this.locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            return;
        }
        this.locationService = new LocationService(this, journey);
        this.locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                2000,
                1,
                this.locationService
        );

        accelerometerSensor.start();

    }

    @Override
    public void onCreate() {
        super.onCreate();

        accelerometerSensor = new AccelerometerSensor(this) {
            @Override
            public void onUpdate(Vector3D a, Vector3D g) {
                if (accelerometerSensor.significantMotionDetected()) {
                    journey.append(
                            new DataPoint(
                                    new AccelerometerPoint(Math.abs(a.project(g) - 1)),
                                    new GPSPoint(0, 0),
                                    (double) now().toEpochMilli() / 1000
                            )
                    );
                }
            }
        };

        String CHANNEL_ID = "road_quality";
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Road quality collector",
                NotificationManager.IMPORTANCE_DEFAULT
        );

        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("")
                .setContentText("").build();

        startForeground(1, notification);
    }


    @Override
    public void onDestroy() {
        this.locationManager.removeUpdates(this.locationService);
        accelerometerSensor.stop();

        try {
            this.journey.save();
            this.journey.send(true);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }

}