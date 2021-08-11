package com.example.roadquality.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AccelerometerPoint {

    public double x;
    public double y;
    public double z;
    public double verticalAcceleration;

    public AccelerometerPoint(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public AccelerometerPoint(double verticalAcceleration) {
        this.verticalAcceleration = verticalAcceleration;
    }

    public static AccelerometerPoint parse(Object acc) throws JSONException {
        if (acc instanceof JSONArray) {
            if (((JSONArray) acc).length() == 0) {
                return new AccelerometerPoint(0, 0, 0);
            }
            return new AccelerometerPoint(
                    ((JSONArray) acc).getDouble(0),
                    ((JSONArray) acc).getDouble(1),
                    ((JSONArray) acc).getDouble(2)
            );
        } else if (acc instanceof JSONObject) {
            return new AccelerometerPoint(
                    ((JSONObject) acc).getDouble("x"),
                    ((JSONObject) acc).getDouble("y"),
                    ((JSONObject) acc).getDouble("z")
            );
        } else if (acc instanceof Double) {
            return new AccelerometerPoint(
                    (Double) acc
            );
        } else {
            throw new AssertionError("Can not parse AccelerometerPoint");
        }
    }

    public boolean isValid() {
        if (this.verticalAcceleration != 0) {
            return Math.abs(this.verticalAcceleration) > 0.5;
        } else {
            return this.x > 1 || this.y > 1 || this.z > 1;
        }
    }

    public Object getJSON(boolean simplify) throws JSONException {
        if (this.verticalAcceleration != 0) {
            return this.verticalAcceleration;
        } else {
            if (simplify) {
                JSONArray data = new JSONArray();
                data.put(this.x);
                data.put(this.y);
                data.put(this.z);
                return data;
            } else {
                JSONObject data = new JSONObject();
                data.put("x", this.x);
                data.put("y", this.y);
                data.put("z", this.z);
                return data;
            }
        }


    }
}
