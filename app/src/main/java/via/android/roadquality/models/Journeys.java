package via.android.roadquality.models;

import org.json.JSONException;

import java.util.ArrayList;

public class Journeys {

    public ArrayList<Journey> journeys;

    public Journeys() {
        this.journeys = new ArrayList<>();
    }

    public void add(Journey journey) {
        this.journeys.add(journey);
    }

    public void addToLast(DataPoint dp) throws JSONException {
        if (this.journeys.size() == 0) {
            this.journeys.add(new Journey());
            this.addToLast(dp);
        } else {
            this.journeys.get(this.journeys.size() - 1).append(dp);
        }
    }
}
