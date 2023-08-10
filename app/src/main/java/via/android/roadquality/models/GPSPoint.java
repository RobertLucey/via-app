package via.android.roadquality.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GPSPoint {

    public double lat;
    public double lng;

    public GPSPoint(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public boolean isPopulated() {
        return this.lat != 0 && this.lng != 0;
    }

    public static GPSPoint parse(Object gps) throws JSONException {
        if (gps instanceof JSONArray) {
            if (((JSONArray) gps).length() == 0) {
                return new GPSPoint(0, 0);
            }
            return new GPSPoint(
                    ((JSONArray) gps).getDouble(0),
                    ((JSONArray) gps).getDouble(1)
            );
        } else if (gps instanceof JSONObject) {
            return new GPSPoint(
                    ((JSONObject) gps).getDouble("lat"),
                    ((JSONObject) gps).getDouble("lng")
            );
        } else {
            throw new AssertionError("Can not parse GPSPoint");
        }
    }

    public Object getJSON(boolean simplify) throws JSONException {
        if (simplify) {
            JSONArray data = new JSONArray();
            data.put(this.lat);
            data.put(this.lng);
            return data;
        } else {
            JSONObject data = new JSONObject();
            data.put("lat", this.lat);
            data.put("lng", this.lng);
            return data;
        }
    }
}
