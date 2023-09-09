package via.android.roadquality.services;

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
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import via.android.roadquality.models.Vector3D;
import via.android.roadquality.models.AccelerometerPoint;
import via.android.roadquality.models.DataPoint;
import via.android.roadquality.models.GPSPoint;
import via.android.roadquality.models.Journey;
import via.android.roadquality.utils.LokiLogger;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;

import java.io.IOException;

public class MainService extends Service {
    private LokiLogger logger;

    LocationManager locationManager;
    LocationService locationService;
    Journey journey;

    AccelerometerSensor accelerometerSensor;

    public MainService() {
        this.logger = new LokiLogger(this, "MainService.java");
    }

    @Override
    public IBinder onBind(Intent intent) {
        logger.log("onBind called and it's not implemented! Hard fail.");
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    public void onStart(Intent intent, int startId) {
        // This is to ensure the logger has a valid Context.
        this.logger = new LokiLogger(this, "MainService.java");

        logger.log("onStart fired...");
        this.journey = new Journey(this);
        logger.log("new Journey() created...");
        this.journey.setTransportType(intent.getStringExtra("transportType"));
        this.journey.setSuspension(intent.getBooleanExtra("suspension", false));
        this.journey.setSendRelativeTime(intent.getBooleanExtra("sendRelativeTime", false));
        this.journey.setMinutesToCut(intent.getIntExtra("minutesToCut", 0));
        this.journey.setMetresToCut(intent.getIntExtra("metresToCut", 0));
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

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        FusedLocationProviderClient fusedClient = LocationServices.getFusedLocationProviderClient(this);

        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        this.locationService = new LocationService(this, journey);

        fusedClient.requestLocationUpdates(
                locationRequest,
                this.locationService,
                Looper.getMainLooper()
        );

        accelerometerSensor.start();
        logger.log("onStart finished!");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // This is to ensure the logger has a valid Context.
        this.logger = new LokiLogger(this, "MainService.java");

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
        LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(this.locationService);
        accelerometerSensor.stop();

        try {
            logger.log("saving journey...");
            this.journey.save();
            logger.log("Journey saved");
            this.journey.send(true);
            logger.log("Journey sent!");
        } catch (IOException | JSONException e) {
            logger.log("journey save/send error hit :(" + e + " " + e.getMessage());
            e.printStackTrace();
        }

        super.onDestroy();
    }
}