package com.example.roadquality.utils;

import pl.mjaron.tinyloki.ILogStream;
import pl.mjaron.tinyloki.LogController;
import pl.mjaron.tinyloki.TinyLoki;

public class LokiLogger {
    private LogController lokiLogController = null;
    private ILogStream lokiLogStream = null;

    public LokiLogger() {
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

        this.lokiLogStream.log("lokiLogStream initialized.");
    }

    public void close() {
        this.lokiLogController.softStop().hardStop();
    }

    public void log(String tag, String message, int level) {
        message = "tag=" + tag + " " + "level=" + String.valueOf(level) + " " + message;
        this.lokiLogStream.log(message);
    }

    public void log(String tag, String message) {
        this.log(tag, message, 20);
    }

    public void log(String message) {
        this.log("default", message);
    }
}