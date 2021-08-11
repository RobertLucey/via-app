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
        assertTrue(new AccelerometerPoint(1, 2, 3).isValid());
        assertTrue(new AccelerometerPoint(1.1).isValid());
    }

    @Test
    public void assertIsNotValid() {
        assertFalse(new AccelerometerPoint(0, 0.1, 0.2).isValid());
        assertFalse(new AccelerometerPoint(0.1).isValid());
    }

    @Test
    public void getJson() throws JSONException {
        assertEquals(
                new AccelerometerPoint(0, 0.1, 0.2).getJSON(false).toString(),
                "{\"x\":0,\"y\":0.1,\"z\":0.2}"
        );
        assertEquals(
                new AccelerometerPoint(1.1).getJSON(false).toString(),
                "1.1"
        );
    }
}
