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
        assertTrue(new AccelerometerPoint(1.1).isValid());
    }

    @Test
    public void assertIsNotValid() {
        assertFalse(new AccelerometerPoint(0.1).isValid());
    }

    @Test
    public void getJson() throws JSONException {
        assertEquals(
                new AccelerometerPoint(1.2).getJSON(),
                1.2
        );
    }
}
