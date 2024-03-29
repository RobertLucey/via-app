package via.android.roadquality.models;

import via.android.roadquality.utils.Haversine;

import org.json.JSONException;
import org.json.JSONObject;

public class DataPoint {

    public AccelerometerPoint accelerometerPoint;
    public GPSPoint gpsPoint;
    public double time;

    public DataPoint(AccelerometerPoint accelerometerPoint, GPSPoint gpsPoint, double time) {
        this.accelerometerPoint = accelerometerPoint;
        this.gpsPoint = gpsPoint;
        this.time = time;
    }

    public double distanceFrom(GPSPoint cmp) {
        return Haversine.distance(
                this.gpsPoint.lat,
                this.gpsPoint.lng,
                cmp.lat,
                cmp.lng
        ) * 1000;
    }

    public JSONObject getJSON(boolean simplify, boolean sendRelativeTime, double startTime) throws JSONException {

        JSONObject data = new JSONObject();
        data.put("acc", this.accelerometerPoint.getJSON());
        data.put("gps", this.gpsPoint.getJSON(simplify));
        if (sendRelativeTime) {
            data.put("time", this.time - startTime);
        }

        return data;
    }
}
