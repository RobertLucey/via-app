package com.example.roadquality.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AccelerometerPoint {

    public double x;
    public double y;
    public double z;
    public double verticalAcceleration;

    public AccelerometerPoint(double verticalAcceleration) {
        this.verticalAcceleration = verticalAcceleration;
    }

    public static AccelerometerPoint parse(Object acc) throws JSONException {
        System.out.println(acc);
        if (acc instanceof Double || acc instanceof Integer) {
            return new AccelerometerPoint(
                    (Double) acc
            );
        } else {
            throw new AssertionError("Can not parse AccelerometerPoint");
        }
    }

    public boolean isValid() {
        return Math.abs(this.verticalAcceleration) > 0.5;
    }

    public Object getJSON() {
        return this.verticalAcceleration;
    }
}
