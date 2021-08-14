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
        assertEquals(
                new DataPoint(new AccelerometerPoint(1), new GPSPoint(1, 2), 10).getJSON(false, true, 1).toString(),
                "{\"acc\":1,\"gps\":{\"lng\":2,\"lat\":1},\"time\":9}"
        );
        assertEquals(
                new DataPoint(new AccelerometerPoint(1), new GPSPoint(1, 2), 10).getJSON(true, false, 1).toString(),
                "{\"acc\":1,\"gps\":[1,2]}"
        );
        assertEquals(
                new DataPoint(new AccelerometerPoint(1), new GPSPoint(1, 2), 10).getJSON(true, true, 1).toString(),
                "{\"acc\":1,\"gps\":[1,2],\"time\":9}"
        );

        assertEquals(
                new DataPoint(new AccelerometerPoint(1.1), new GPSPoint(1, 2), 10).getJSON(false, true, 1).toString(),
                "{\"acc\":1.1,\"gps\":{\"lng\":2,\"lat\":1},\"time\":9}"
        );
        assertEquals(
                new DataPoint(new AccelerometerPoint(1.1), new GPSPoint(1, 2), 10).getJSON(true, false, 1).toString(),
                "{\"acc\":1.1,\"gps\":[1,2]}"
        );
        assertEquals(
                new DataPoint(new AccelerometerPoint(1.1), new GPSPoint(1, 2), 10).getJSON(true, true, 1).toString(),
                "{\"acc\":1.1,\"gps\":[1,2],\"time\":9}"
        );
    }
}
