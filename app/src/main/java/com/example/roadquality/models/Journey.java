package com.example.roadquality.models;

import android.os.Environment;

import androidx.annotation.NonNull;

import com.example.roadquality.BuildConfig;
import com.example.roadquality.utils.LokiLogger;

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

    public UUID uuid;
    private boolean isCulled;
    public String transportType;
    public boolean suspension;
    private int minutesToCut;
    private int metresToCut;
    private boolean sendRelativeTime;
    public ArrayList<DataPoint> frames = new ArrayList();
    public boolean sendInPartials;

    private LokiLogger logger = new LokiLogger("Journey.java");

    public Journey() {
        this.uuid = UUID.randomUUID();
    }

    public static Journey fromFile(String filepath) throws JSONException {
        String journeyStr = null;
        try {
            File journeyFile = new File(filepath);
            Scanner journeyFileReader = new Scanner(journeyFile);
            journeyStr = journeyFileReader.nextLine();
            journeyFileReader.close();
        } catch (FileNotFoundException e) {
            new LokiLogger("JourneyFromFile").log("File not found error: " + e);
        } catch (Exception e) {
            new LokiLogger("JourneyFromFile").log("Unusual error in file reading: " + e);
        }

        return Journey.parse(journeyStr);
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
        Journey journey = new Journey();
        if (!journeyJson.getString("uuid").equals("")) {
            journey.setUUID(UUID.fromString(journeyJson.getString("uuid")));
        }
        journey.setTransportType(journeyJson.getString("transport_type"));
        journey.setSuspension(journeyJson.getBoolean("suspension"));
        journey.setSendRelativeTime(journeyJson.getBoolean("send_relative_time"));
        journey.setMinutesToCut(journeyJson.getInt("minutes_to_cut"));
        journey.setMetresToCut(journeyJson.getInt("metres_to_cut"));

        journey.frames = frames;

        return journey;
    }

    public void append(DataPoint dp) {
        if (this.frames == null) {
            this.frames = new ArrayList<>();
        }
        this.frames.add(dp);

    }

    public String filePath() {
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File viaBase = new File(root + "/via");
        if (!viaBase.exists()) {
            viaBase.mkdirs();
        }
        return viaBase.toString() + "/" + this.uuid + ".json";
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
        logger.log("Beginning cullTime with " + this.frames.size() + " frames");
        ArrayList<DataPoint> tmpFrames = new ArrayList<>();

        for (DataPoint frame : this.frames) {
            if (frame.time >= originTime + (60 * this.minutesToCut) && frame.time <= destinationTime - (60 * this.minutesToCut)) {
                tmpFrames.add(frame);
            }
        }

        this.frames = tmpFrames;
        logger.log(this.frames.size() + " frames remaining after cullTime");
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
        logger.log("Culling distance...");
        int firstFrameAwayIdx = 0;

        for (DataPoint frame : this.frames) {
            if (frame.gpsPoint.isPopulated() && frame.distanceFrom(this.getOrigin()) >= this.metresToCut) {
                break;
            }

            firstFrameAwayIdx += 1;
        }
        logger.log("Removing " + firstFrameAwayIdx + "frames from beginning...");

        Collections.reverse(this.frames);

        int lastFrameAwayIdx = 0;

        for (DataPoint frame : this.frames) {
            if (frame.gpsPoint.isPopulated() && frame.distanceFrom(this.getOrigin()) > this.metresToCut) {
                lastFrameAwayIdx = this.frames.size() - lastFrameAwayIdx;
                break;
            }
            lastFrameAwayIdx += 1;
        }

        logger.log("Removing " + lastFrameAwayIdx + "frames from end...");

        Collections.reverse(this.frames);

        ArrayList<DataPoint> tmpFrames = new ArrayList<>();
        int idx = 0;
        for (DataPoint frame : this.frames) {
            idx += 1;
            if (idx > firstFrameAwayIdx && idx < lastFrameAwayIdx) {
                tmpFrames.add(frame);
            }
        }

        this.frames = tmpFrames;
        logger.log(this.frames.size() + " frames remaining after cull distance.");
    }

    public boolean cull() {
        logger.log("Beginning cull");

        if (this.isCulled) {
            logger.log("Already culled. Returning");
            return false;
        }

        if (this.frames.size() == 0) {
            logger.log("No frames before cull! Returning");
            return true;
        }

        logger.log("Have " + this.frames.size() + " frames to work with...");

        double originTime = this.frames.get(0).time;
        double destinationTime = this.frames.get(this.frames.size() - 1).time;

        try {
            cullDistance();
            cullTime(originTime, destinationTime);
            this.isCulled = true;
        } catch (Exception e) {
            logger.log("Exception thrown in cullDistance/Time: " + e);
            return false;
        }

        logger.log(this.frames.size() + " frames remaining after culls");
        return true;
    }

    public void send(boolean removeOnSuccess) throws JSONException, IOException {
        if (this.cull()) {
            if (this.frames.size() == 0) {
                this.logger.log("No frames after cull so not sending");
            } else {
                if (this.sendInPartials) {
                    logger.log("Sending in partials...");
                    for (Journey partialJourney : this.getPartials().journeys) {
                        logger.log("Saving partial");
                        partialJourney.save();
                        logger.log("Sending partial");
                        partialJourney.send(true);
                        logger.log("Partial sent.");
                    }
                } else {
                    this.logger.log("Sending normally");
                    this.postData(
                            this.getJSON(true, true).toString(),
                            removeOnSuccess
                    );
                    this.logger.log("Journey sent.");
                }
            }
        } else {
            this.logger.log("Could not cull so not bothering to send");
        }
    }

    public void postData(String str, boolean removeOnSuccess) {
        this.logger.log("Sending off the data: " + str);

        RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), str);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(this.uploadUrl)
                .post(body)
                .build();

        Call call = client.newCall(request);

        String filePath = this.filePath();

        call.enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                new LokiLogger().log("JourneyPostData", "postData got response: " + response.toString());
                if (removeOnSuccess) {
                    if (response.code() == 201 || response.code() == 200) {
                        new File(filePath).delete();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                new LokiLogger().log("JourneyPostData", "Post failure: " + e.toString());
            }
        });
    }

    public void setSendInPartials(boolean sendInPartials) {
        this.sendInPartials = sendInPartials;
    }

    private JSONArray getDataJSON(boolean simplify, boolean sending) throws JSONException {
        JSONArray data = new JSONArray();
        if (this.frames != null) {
            for (DataPoint d : this.frames) {
                data.put(d.getJSON(simplify, this.sendRelativeTime || !sending, this.frames.get(0).time));
            }
        }

        return data;
    }

    public JSONObject getJSON(boolean simplify, boolean sending) throws JSONException {

        JSONObject data = new JSONObject();
        data.put("version", BuildConfig.VERSION_NAME);
        data.put("uuid", this.uuid.toString());
        data.put("device", "phone");
        data.put("data", this.getDataJSON(simplify, sending));
        data.put("transport_type", this.transportType);
        data.put("suspension", this.suspension);
        data.put("is_partial", this.sendInPartials);

        if (!sending) {
            // Data below should not be shared remotely. It can be used to generate data to send or
            // is implied by the lack of data being sent
            data.put("is_culled", this.isCulled);
            data.put("send_relative_time", this.sendRelativeTime);
            data.put("minutes_to_cut", this.minutesToCut);
            data.put("metres_to_cut", this.metresToCut);
        }

        return data;
    }

    public Journeys getPartials() throws JSONException {
        Journeys journeys = new Journeys();

        GPSPoint previousGPSPoint = null;
        GPSPoint lastCheckpoint = null;
        boolean inbetween = false;
        int skippedDistance = 0;
        for (DataPoint dp : this.frames) {
            if (lastCheckpoint == null) {
                if (dp.gpsPoint.isPopulated()) {
                    lastCheckpoint = dp.gpsPoint;
                } else {
                    continue;
                }
            }
            if (inbetween) {

                if (skippedDistance > 50) {
                    inbetween = false;

                    Journey journey = new Journey();
                    journey.setTransportType(this.transportType);
                    journey.setSuspension(this.suspension);
                    journeys.add(journey);
                } else {
                    skippedDistance += dp.distanceFrom(previousGPSPoint);  // the distance from current dp to previous dp
                }

            } else {
                if (dp.distanceFrom(lastCheckpoint) < 200) {  // TODO: configure 200?

                    // We don't want to include time when sending partials
                    dp.time = 0;
                    journeys.addToLast(dp);
                } else {
                    inbetween = true;
                    lastCheckpoint = null;

                }
            }
            previousGPSPoint = dp.gpsPoint;
        }

        // TODO: maybe skip the next x metres to make it more difficult to stitch up

        // For each partial randomly reverse so direction can't be found
        for (Journey journey : journeys.journeys) {
            if (Math.random() > 0.5) {
                Collections.reverse(journey.frames);
            }
        }

        return journeys;
    }

    // Get the distance of the journey so that we can see if partials are a good size
    public double getIndirectDistance() {
        double accumulatedDist = 0;
        GPSPoint prevPoint = null;

        for (DataPoint dp : this.frames) {
            if (!dp.gpsPoint.isPopulated()) {
                continue;
            }
            if (prevPoint == null) {
                if (dp.gpsPoint.isPopulated()) {
                    prevPoint = dp.gpsPoint;
                }
            } else {
                accumulatedDist += dp.distanceFrom(prevPoint);
                prevPoint = dp.gpsPoint;
            }
        }
        return accumulatedDist;
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    public void setTransportType(String transportType) {
        this.transportType = transportType;
    }

    public void setSuspension(boolean suspension) {
        this.suspension = suspension;
    }

    public void setSendRelativeTime(boolean sendRelativeTime) {
        this.sendRelativeTime = sendRelativeTime;
    }

    public void setMinutesToCut(int minutesToCut) {
        this.minutesToCut = minutesToCut;
    }

    public void setMetresToCut(int metresToCut) {
        this.metresToCut = metresToCut;
    }
}
