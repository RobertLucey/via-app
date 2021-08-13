package com.example.roadquality.models;

import android.os.Environment;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Journey {

    final private String uploadUrl = "https://hl7soqwrx3.execute-api.eu-west-1.amazonaws.com/default/upload-road-quality";

    final public UUID uuid;
    private boolean isCulled;
    public String transportType;
    public boolean suspension;
    private int minutesToCut;
    private int metresToCut;
    private boolean sendRelativeTime;
    public ArrayList<DataPoint> frames;
    public int includedJourneys = 0;

    public Journey(
            String transportType,
            boolean suspension,
            boolean sendRelativeTime,
            int minutesToCut,
            int metresToCut
    ) {
        this.uuid = UUID.randomUUID();
        this.transportType = transportType;
        this.suspension = suspension;
        this.sendRelativeTime = sendRelativeTime;
        this.minutesToCut = minutesToCut;
        this.metresToCut = metresToCut;
        this.frames = new ArrayList<>();
    }

    public Journey(
            UUID uuid,
            String transportType,
            boolean suspension,
            boolean sendRelativeTime,
            int minutesToCut,
            int metresToCut
    ) {
        this.uuid = uuid;
        this.transportType = transportType;
        this.suspension = suspension;
        this.sendRelativeTime = sendRelativeTime;
        this.minutesToCut = minutesToCut;
        this.metresToCut = metresToCut;
        this.frames = new ArrayList<>();
    }

    public Journey() {
        this.uuid = UUID.randomUUID();
    }

    public static Journey fromFile(String filepath) throws JSONException {
        String journeyStr = null;
        try {
            File myObj = new File(filepath);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                journeyStr = myReader.nextLine();
                break;
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        return Journey.parse(journeyStr);
    }

    public boolean mergeDataWith(Journey toMerge) {
        if (this.transportType == toMerge.transportType && this.suspension == toMerge.suspension) {
            this.frames.addAll(toMerge.frames);
            return true;
        } else {
            return false;
        }
    }

    public static Journey parse(String journeyStr) throws JSONException {
        JSONObject journeyJson = new JSONObject(journeyStr);

        ArrayList<DataPoint> frames = new ArrayList<>();
        JSONArray frameArr = journeyJson.getJSONArray("data");

        for (int i = 0; i < frameArr.length(); i++) {
            JSONObject frameObj = frameArr.getJSONObject(i);

            AccelerometerPoint accelerometerPoint = AccelerometerPoint.parse(frameObj.get("acc"));
            GPSPoint gpsPoint = GPSPoint.parse(frameObj.get("gps"));

            // FIXME: time might not be present (only if working with remote files locally)
            DataPoint dp = new DataPoint(
                    accelerometerPoint,
                    gpsPoint,
                    frameObj.getInt("time")
            );
            frames.add(dp);
        }

        // FIXME: gross. Have journey take in the json
        Journey journey;
        if (journeyJson.getString("uuid") != null) {
            journey = new Journey(
                    UUID.fromString(journeyJson.getString("uuid")),
                    journeyJson.getString("transport_type"),
                    journeyJson.getBoolean("suspension"),
                    journeyJson.getBoolean("sendRelativeTime"),
                    journeyJson.getInt("minutesToCut"),
                    journeyJson.getInt("metresToCut")
            );
        } else {
            journey = new Journey(
                    journeyJson.getString("transport_type"),
                    journeyJson.getBoolean("suspension"),
                    journeyJson.getBoolean("sendRelativeTime"),
                    journeyJson.getInt("minutesToCut"),
                    journeyJson.getInt("metresToCut")
            );
        }

        journey.frames = frames;

        return journey;
    }

    public void append(DataPoint dp) {
        if (frames != null) {
            frames.add(dp);
        }
    }

    public String filePath() {
        // FIXME: put these in a subdir
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();

        File bikeBase = new File(root + "/bike");

        if (!bikeBase.exists()){
            bikeBase.mkdirs();
        }

        return bikeBase.toString() + "/" + this.uuid + ".json";
    }

    public void save() throws IOException, JSONException {


        File file = new File(this.filePath());

        try (FileOutputStream stream = new FileOutputStream(file)) {
            stream.write(
                    this.getJSON(
                            true,
                            false
                    ).toString().getBytes(StandardCharsets.UTF_8)
            );
        }
    }

    public void cullTime(double originTime, double destinationTime) {
        ArrayList<DataPoint> tmpFrames = new ArrayList<>();

        for (DataPoint frame : this.frames) {
            if (frame.time >= originTime + (60 * this.minutesToCut) && frame.time <= destinationTime - (60 * this.minutesToCut)) {
                tmpFrames.add(frame);
            }
        }

        this.frames = tmpFrames;
    }

    public GPSPoint getOrigin() {
        for (DataPoint frame : this.frames) {
            if (frame.gpsPoint.isPopulated()) {
                return frame.gpsPoint;
            }
        }
        return new GPSPoint(0, 0);
    }

    public void cullDistance() throws JSONException {
        int firstFrameAwayIdx = 0;

        for (DataPoint frame : this.frames) {
            if (frame.gpsPoint.isPopulated()) {
                System.out.println(frame.gpsPoint.getJSON(true).toString());
            }
        }

        for (DataPoint frame : this.frames) {
            if (frame.gpsPoint.isPopulated() && frame.distanceFrom(this.getOrigin()) > this.metresToCut) {
                break;
            }
            firstFrameAwayIdx += 1;
        }

        Collections.reverse(this.frames);

        int lastFrameAwayIdx = 0;

        for (DataPoint frame : this.frames) {
            if (frame.gpsPoint.isPopulated() && frame.distanceFrom(this.getOrigin()) > this.metresToCut) {
                lastFrameAwayIdx = this.frames.size() - lastFrameAwayIdx;
                break;
            }
            lastFrameAwayIdx += 1;
        }

        Collections.reverse(this.frames);

        if (firstFrameAwayIdx == 0 || lastFrameAwayIdx == 0) {
            // Warn about not having enough?
            // Bother even sending?
            this.frames = new ArrayList<>();
        } else {
            ArrayList<DataPoint> tmpFrames = new ArrayList<>();
            int idx = 0;
            for (DataPoint frame : this.frames) {
                idx += 1;
                if (idx > firstFrameAwayIdx && idx < lastFrameAwayIdx) {
                    tmpFrames.add(frame);
                }
            }
            this.frames = tmpFrames;
        }
    }

    public boolean cull() {
        if (this.isCulled) {
            return false;
        }

        if (this.frames.size() == 0) {
            return true;
        }
        double originTime = this.frames.get(0).time;
        double destinationTime = this.frames.get(this.frames.size() - 1).time;

        try {
            cullDistance();
            cullTime(originTime, destinationTime);
            this.isCulled = true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public void send() throws JSONException, IOException {
        if (this.cull()) {
            this.postData(this.getJSON(true, true).toString());
        } else {
            System.out.println("Could not cull so not bothering to send");
        }
    }

    public void postData(String str) {
        RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), str);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(this.uploadUrl)
                .post(body)
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }
        });
    }

    private JSONArray getDataJSON(boolean simplify, boolean sending) throws JSONException {
        JSONArray data = new JSONArray();
        for (DataPoint d : this.frames) {
            data.put(d.getJSON(simplify, this.sendRelativeTime || !sending, this.frames.get(0).time));
        }
        return data;
    }

    public JSONObject getJSON(boolean simplify, boolean sending) throws JSONException {
        JSONObject data = new JSONObject();
        data.put("uuid", this.uuid.toString());
        data.put("device", "phone");
        data.put("data", this.getDataJSON(simplify, sending));
        data.put("transport_type", this.transportType);
        data.put("suspension", this.suspension);

        if (!sending) {
            // Data below should not be shared remotely. It can be used to generate data to send or
            // is implied by the lack of data being sent
            data.put("is_culled", this.isCulled);
            data.put("sendRelativeTime", this.sendRelativeTime);
            data.put("minutesToCut", this.minutesToCut);
            data.put("metresToCut", this.metresToCut);
        }

        return data;
    }
}
