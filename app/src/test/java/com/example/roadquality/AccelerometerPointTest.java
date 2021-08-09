package com.example.roadquality;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.roadquality.models.AccelerometerPoint;

import org.json.JSONException;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class AccelerometerPointTest {
    @Test
    public void assertIsValid() {
        AccelerometerPoint point = new AccelerometerPoint(1, 2, 3);
        assertTrue(point.isValid());
    }

    @Test
    public void assertIsNotValid() {
        AccelerometerPoint point = new AccelerometerPoint(0, 0.1, 0.2);
        assertFalse(point.isValid());
    }

    @Test
    public void getJson() throws JSONException {
        AccelerometerPoint point = new AccelerometerPoint(0, 0.1, 0.2);

        assertEquals(
                point.getJSON(false).toString(),
                "{\"x\":0,\"y\":0.1,\"z\":0.2}"
        );
    }
}
