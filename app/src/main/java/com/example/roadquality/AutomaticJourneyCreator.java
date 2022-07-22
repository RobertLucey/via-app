package com.example.roadquality;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.roadquality.utils.LokiLogger;
import com.google.android.gms.location.ActivityTransitionResult;

public class AutomaticJourneyCreator extends BroadcastReceiver {
    private LokiLogger logger = new LokiLogger();

    public static final String INTENT_ACTION = "com.example.roadquality.ACTION_PROCESS_ACTIVITY_TRANSITIONS";

    @Override
    public void onReceive(Context context, Intent intent) {
        logger.log("AutomaticJourneyCreator", "onReceive fired.");

        if (intent != null && INTENT_ACTION.equals(intent.getAction())) {
            logger.log("AutomaticJourneyCreator", "Correct intent action.");
            if (ActivityTransitionResult.hasResult(intent)) {
                logger.log("AutomaticJourneyCreator", "ActivityTransitionResult has result!");
                ActivityTransitionResult intentResult = ActivityTransitionResult
                        .extractResult(intent);
                logger.log("AutomaticJourneyCreator", "intentResult=" + intentResult.toString());
                // handle activity transition result ...
            }
        }
    }
}