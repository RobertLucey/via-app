package com.example.roadquality;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class GeomagneticInterface {


    // create an interface with one method
    public interface Listener {
        // create method with all 3
        // axis translation as argument
        void onTranslation(float[] t1);
    }

    // create an instance
    private GeomagneticInterface.Listener listener;

    // method to set the instance
    public void setListener(GeomagneticInterface.Listener l) {
        listener = l;
    }

    final private SensorManager sensorManager;
    final private Sensor sensor;
    final private SensorEventListener sensorEventListener;

    // create constructor with
    // context as argument
    public GeomagneticInterface(Context context) {

        // create instance of sensor manager
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        // create the sensor listener
        sensorEventListener = new SensorEventListener() {
            // this method is called when the
            // device's position changes
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                // check if listener is
                // different from null
                if (listener != null) {
                    // pass the three floats in listener on translation of axis
                    listener.onTranslation(
                            sensorEvent.values
                    );
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
    }

    // create register method
    // for sensor notifications
    public void register() {
        // call sensor manger's register listener
        // and pass the required arguments
        sensorManager.registerListener(
                sensorEventListener,
                sensor,
                SensorManager.SENSOR_DELAY_NORMAL
        );
    }

    // create method to unregister
    // from sensor notifications
    public void unregister() {
        // call sensor manger's unregister listener
        // and pass the required arguments
        sensorManager.unregisterListener(sensorEventListener);
    }
}
