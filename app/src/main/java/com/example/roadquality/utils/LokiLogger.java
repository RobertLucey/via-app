package com.example.roadquality.utils;

import android.util.Log;

import pl.mjaron.tinyloki.ILogStream;
import pl.mjaron.tinyloki.LogController;
import pl.mjaron.tinyloki.TinyLoki;

public class LokiLogger {
    private LogController lokiLogController = null;
    private ILogStream lokiLogStream = null;
    private String tag = null;

    public LokiLogger() {
        this.initLokiLogStream();
    }

    public LokiLogger(String tag) {
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
            .l("version", "0.0.2")  // TODO: Use proper versioning.
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
