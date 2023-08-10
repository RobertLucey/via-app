package via.android.roadquality.models;

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

    public Object getJSON() {
        return Math.round(this.verticalAcceleration * 10000.0) / 10000.0;
    }
}
