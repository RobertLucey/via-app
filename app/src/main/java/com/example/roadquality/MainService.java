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
import android.hardware.SensorManager;
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
    GeomagneticInterface geoMagInterface;

    float[] currentRotation;


    private float[] transpose(float[] matrix) {
        float temp;
        temp = matrix[1];
        matrix[1] = matrix[3];
        matrix[3] = temp;

        temp = matrix[2];
        matrix[2] = matrix[6];
        matrix[6] = temp;

        temp = matrix[5];
        matrix[5] = matrix[7];
        matrix[7] = temp;

        return matrix;
    }

    private float[] mult(float[] matrix1, float[] matrix2) {
        float[][] first = new float[3][3];
        int k = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                first[i][j] = matrix1[k];
                k++;
            }
        }

        float[] multiply = new float[3];
        float sum = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                sum = sum + (first[i][j] * matrix2[i]);
            }
            multiply[i] = sum;
            sum = 0;
        }
        return multiply;
    }


    public MainService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        System.out.println("SERVICE Service Startedd");

        String transportType = intent.getStringExtra("transportType");
        boolean suspension = intent.getBooleanExtra("suspension", false);
        boolean sendRelativeTime = intent.getBooleanExtra("sendRelativeTime", false);
        int minutesToCut = intent.getIntExtra("minutesToCut", 99999);
        int metresToCut = intent.getIntExtra("metresToCut", 99999);

        journey = new Journey(transportType, suspension, sendRelativeTime, minutesToCut, metresToCut);

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
                1000,
                1,
                this.locationService);

        geoMagInterface = new GeomagneticInterface(this);
        // create a listener for accelerometer
        geoMagInterface.setListener(new GeomagneticInterface.Listener() {

            @Override
            public void onTranslation(float[] geoArr) {
                currentRotation = geoArr;
            }
        });

        geoMagInterface.register();


        accelerometer = new AccelerometerInterface(this);
        // create a listener for accelerometer
        accelerometer.setListener(new AccelerometerInterface.Listener() {

            @Override
            public void onTranslation(float[] accelerationArr) {
                // set the color red if the device moves in positive x axis

                float[] rotationVectorOutput = currentRotation;
                float[] rotationMatrix = new float[16];  // 9 or 16?
                SensorManager.getRotationMatrixFromVector(rotationMatrix, rotationVectorOutput);
                float[] vertical = mult(transpose(rotationMatrix), accelerationArr);

                AccelerometerPoint accelerometerPoint = new AccelerometerPoint(vertical[0] + vertical[1] + vertical[2]);

                if (accelerometerPoint.isValid()) {
                    journey.append(
                            new DataPoint(
                                    accelerometerPoint,
                                    new GPSPoint(
                                            0.0,
                                            0.0
                                    ),
                                    (double) now().toEpochMilli() / 1000
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
        this.locationManager.removeUpdates(this.locationService);
        accelerometer.unregister();
        geoMagInterface.unregister();
        super.onDestroy();
    }

}