package com.example.roadquality.models;

public class AccelerometerPoint {

    public double verticalAcceleration;

    public AccelerometerPoint(double verticalAcceleration) {
        this.verticalAcceleration = verticalAcceleration;
    }

    public static AccelerometerPoint parse(Object acc) {
        if (acc instanceof Number) {
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
