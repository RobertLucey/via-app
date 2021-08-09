package com.example.roadquality;

import static org.junit.Assert.assertEquals;
import com.example.roadquality.models.AccelerometerPoint;
import com.example.roadquality.models.DataPoint;
import com.example.roadquality.models.GPSPoint;

import org.json.JSONException;
import org.junit.Test;

public class DataPointTest {
    @Test
    public void getJson() throws JSONException {
        DataPoint point = new DataPoint(new AccelerometerPoint(1, 2, 3), new GPSPoint(1, 2), 10);

        assertEquals(
                point.getJSON(false, true, 5).toString(),
                "{\"acc\":{\"x\":1,\"y\":2,\"z\":3},\"gps\":{\"lng\":2,\"lat\":1},\"time\":5}"
        );
        assertEquals(
                point.getJSON(true, true, 5).toString(),
                "{\"acc\":[1,2,3],\"gps\":[1,2],\"time\":5}"
        );
    }
}
