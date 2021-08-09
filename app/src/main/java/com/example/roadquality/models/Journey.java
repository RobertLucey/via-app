package com.example.roadquality.models;

import android.os.Environment;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Journey {

    final private UUID uuid;
    final private String transportType;
    final private boolean suspension;
    final private boolean sendRelativeTime;
    final private int minutesToCut;
    final private int metresToCut;
    private boolean isCulled;
    public ArrayList<DataPoint> frames;

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

    public void mergeDataWith(Journey toMerge) {
        // To get a mega journey

        // TODO: make sure the transport / suspension / device are the same

        this.frames.addAll(toMerge.frames);
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

        Journey journey = new Journey(
                journeyJson.getString("transport_type"),
                journeyJson.getBoolean("suspension"),
                journeyJson.getBoolean("sendRelativeTime"),
                journeyJson.getInt("minutesToCut"),
                journeyJson.getInt("metresToCut")
        );
        journey.frames = frames;

        return journey;
    }

    public void append(DataPoint dp) {
        if (frames != null) {
            frames.add(dp);
        }
    }

    public void save() throws IOException, JSONException {
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();

        File file = new File(root, "bike_" + this.uuid + ".json");

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

    public void cullDistance() {
        int firstFrameAwayIdx = 0;
        int lastFrameAwayIdx = 0;

        int idx = 0;
        for (DataPoint frame : this.frames) {
            if (frame.distanceFrom(this.frames.get(0)) > this.metresToCut) {
                firstFrameAwayIdx = idx;
                break;
            }
            idx += 1;
        }

        Collections.reverse(this.frames);

        // TODO: might need to reverse again
        int idx2 = 0;
        for (DataPoint frame : this.frames) {
            if (frame.distanceFrom(this.frames.get(0)) > this.metresToCut) {
                lastFrameAwayIdx = this.frames.size() - idx2;
                break;
            }
            idx2 += 1;
        }

        Collections.reverse(this.frames);

        if (idx == 0 || idx2 == 0) {
            // warn about not having enough?
            this.frames = new ArrayList<>();
        } else {
            ArrayList<DataPoint> tmpFrames = new ArrayList<>();
            int idx3 = 0;
            for (DataPoint frame : this.frames) {
                idx3 += 1;
                if (idx3 > firstFrameAwayIdx && idx3 < lastFrameAwayIdx) {
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

    public void postData(String str) throws IOException {
        // TODO: set up some backend, can just upload from Downloads for the mo

        RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), str);

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://hl7soqwrx3.execute-api.eu-west-1.amazonaws.com/default/upload-road-quality")
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

    private double startTime() {
        return this.frames.get(0).time;
    }

    private JSONArray getDataJSON(boolean simplify) throws JSONException {
        JSONArray data = new JSONArray();
        for (DataPoint d : this.frames) {
            data.put(d.getJSON(simplify, this.sendRelativeTime, this.startTime()));
        }
        return data;
    }

    public JSONObject getJSON(boolean simplify, boolean forSend) throws JSONException {
        JSONObject data = new JSONObject();
        data.put("uuid", this.uuid.toString());
        data.put("device", "phone");
        data.put("data", this.getDataJSON(simplify));
        data.put("transport_type", this.transportType);
        data.put("suspension", this.suspension);

        if (!forSend) {
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
