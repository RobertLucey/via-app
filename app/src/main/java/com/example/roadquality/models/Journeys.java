package com.example.roadquality.models;

import android.os.Environment;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

public class Journeys {

    public ArrayList<Journey> journeys;

    public Journeys() {

    }

    public void add(Journey journey) {
        this.journeys.add(journey);
    }

    public Journeys fromFiles() throws IOException {
        Journeys journeys = new Journeys();

        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();

        Files.list(new File(root + "/bike").toPath())
                .forEach(path -> {
                    try {
                        journeys.add(Journey.fromFile(path.toString()));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });

        return journeys;
    }

    public Journeys getMegaJourneys() {
        Hashtable<String, Journey> megaJourneys = new Hashtable<String, Journey>();

        for (Journey journey : this.journeys) {
            String key = journey.transportType + "_" + journey.suspension;
            if (megaJourneys.get(key) == null) {
                megaJourneys.put(key, journey);
            } else {
                megaJourneys.get(key).mergeDataWith(journey);
            }
            megaJourneys.get(key).includedJourneys += 1;
        }

        Journeys journeys = new Journeys();
        for (Journey journey : megaJourneys.values()) {
            journeys.add(journey);
        }
        return journeys;
    }
}
