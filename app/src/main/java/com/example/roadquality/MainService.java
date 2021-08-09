package com.example.roadquality;

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

import com.example.roadquality.models.AccelerometerPoint;
import com.example.roadquality.models.DataPoint;
import com.example.roadquality.models.GPSPoint;
import com.example.roadquality.models.Journey;

import org.json.JSONException;

import java.io.IOException;

public class MainService extends Service {

    AccelerometerInterface accelerometer;
    LocationManager locationManager;
    LocationService locationService;
    Journey journey;


    public MainService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        System.out.println("SERVICE Service Started");

        String transportType = intent.getStringExtra("transportType");
        boolean suspension = intent.getBooleanExtra("suspension", false);
        boolean sendRelativeTime = intent.getBooleanExtra("sendRelativeTime", false);
        int minutesToCut = intent.getIntExtra("minutesToCut", 99999);
        int metresToCut = intent.getIntExtra("metresToCut", 99999);

        journey = new Journey(transportType, suspension, sendRelativeTime, minutesToCut, metresToCut);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationService = new LocationService(this, journey);
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000,
                1,
                locationService);

        System.out.println("STARTED");

        accelerometer = new AccelerometerInterface(this);

        // create a listener for accelerometer
        accelerometer.setListener(new AccelerometerInterface.Listener() {

            @Override
            public void onTranslation(float tx, float ty, float ts) {
                // set the color red if the device moves in positive x axis
                AccelerometerPoint ap = new AccelerometerPoint(tx, ty, ts);
                if (ap.isValid()) {
                    journey.append(
                            new DataPoint(
                                    ap,
                                    new GPSPoint(
                                            0.0,
                                            0.0
                                    ),
                                    now().toEpochMilli() / 1000L
                            )
                    );
                }
            }
        });

        accelerometer.register();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        String CHANNEL_ID = "my_channel_01";
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Channel human readable title",
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
        try {
            this.journey.save();
            this.journey.send();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        locationManager.removeUpdates(locationService);
        accelerometer.unregister();
        super.onDestroy();
    }

}