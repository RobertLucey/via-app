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
import com.example.roadquality.utils.LokiLogger;

import org.json.JSONException;

import java.io.IOException;

public class MainService extends Service {
    private LokiLogger logger = new LokiLogger("MainService.java");

    LocationManager locationManager;
    LocationService locationService;
    Journey journey;

    AccelerometerSensor accelerometerSensor;

    public MainService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        logger.log("onBind called and it's not implemented! Hard fail.");
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        logger.log("onStart fired...");
        this.journey = new Journey();
        logger.log("new Journey() created...");
        this.journey.setTransportType(intent.getStringExtra("transportType"));
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
            logger.log("Thinks we don't have permission. Killing :(");
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
        logger.log("onStart finished!");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        logger.log("onCreate started...");
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
        logger.log("Got accelerometerSensor");

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
        logger.log("onDestroy started...");
        this.locationManager.removeUpdates(this.locationService);
        accelerometerSensor.stop();

        try {
            logger.log("saving journey...");
            this.journey.save();
            logger.log("Journey saved");
            this.journey.send(true);
            logger.log("Journey sent!");
        } catch (IOException | JSONException e) {
            logger.log("journey save/send error hit :(" + e.toString() + " " + e.getMessage());
            e.printStackTrace();
        }

        super.onDestroy();
    }
}