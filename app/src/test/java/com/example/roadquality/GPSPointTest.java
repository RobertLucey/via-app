package com.example.roadquality;

import static org.junit.Assert.assertEquals;

import com.example.roadquality.models.GPSPoint;

import org.json.JSONException;
import org.junit.Test;

public class GPSPointTest {
    @Test
    public void getJson() throws JSONException {
        GPSPoint point = new GPSPoint(1.1, 2.2);

        assertEquals(
                point.getJSON(false).toString(),
                "{\"lng\":2.2,\"lat\":1.1}"
        );

        assertEquals(
                point.getJSON(true).toString(),
                "[1.1,2.2]"
        );
    }

}
