package com.example.roadquality.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.roadquality.BuildConfig;

import pl.mjaron.tinyloki.ILogStream;
import pl.mjaron.tinyloki.LogController;
import pl.mjaron.tinyloki.TinyLoki;

public class LokiLogger {
    private LogController lokiLogController = null;
    private ILogStream lokiLogStream = null;
    private String tag;
    private String device_id;


    public LokiLogger(String tag) {
        this.tag = tag;
        this.device_id = "device_id_not_set";

        this.initLokiLogStream();
    }

    public LokiLogger(Context context, String tag) {
        try {
            if (context != null) {
                SharedPreferences sp = context.getSharedPreferences("Via Preferences", Context.MODE_PRIVATE);
                this.device_id = sp.getString("device_id", "device_id_not_set");
            } else {
                this.device_id = "device_id_not_set";
            }
        } catch (Exception e) {
            this.device_id = "no_context_for_logger";
        }

        this.tag = tag;

        this.initLokiLogStream();
    }

    private void initLokiLogStream() {
        this.lokiLogController = TinyLoki
                .withUrl("http://loki.randombits.host/loki/api/v1/push")
                // TODO: Add auth:
//                .withBasicAuth("user", "pass")
                .start();

        this.lokiLogStream = this.lokiLogController.createStream(
                TinyLoki.info()
                        .l("source", "Via App")
                        .l("version", BuildConfig.VERSION_NAME)
                        .l("device_id", this.device_id)
        );
    }

    public void close() {
        this.lokiLogController.softStop().hardStop();
    }

    public void log(String tag, String message, int level) {
        message = "tag=" + tag + " " + "level=" + String.valueOf(level) + " " + message;
        try {
            Log.println(Math.round(level / 10), tag, message);
        } catch (Exception e) {
            ;
        }
        this.lokiLogStream.log(message);
    }

    public void log(String tag, String message) {
        this.log(tag, message, 20);
    }

    public void log(String message) {
        if (this.tag == null) {
            this.log("default", message);
        } else {
            this.log(this.tag, message);
        }
    }
}
