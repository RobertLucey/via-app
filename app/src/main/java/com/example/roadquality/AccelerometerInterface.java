package com.example.roadquality;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class AccelerometerInterface {

    // create an interface with one method
    public interface Listener {
        // create method with all 3
        // axis translation as argument
        void onTranslation(float[] accelerometerData);
    }

    // create an instance
    private Listener listener;

    // method to set the instance
    public void setListener(Listener l) {
        listener = l;
    }

    final private SensorManager sensorManager;
    final private Sensor sensor;
    final private SensorEventListener sensorEventListener;

    // create constructor with
    // context as argument
    public AccelerometerInterface(Context context) {

        // create instance of sensor manager
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        // create instance of sensor
        // with type linear acceleration
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

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
                    try {
                        listener.onTranslation(
                                sensorEvent.values
                        );
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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