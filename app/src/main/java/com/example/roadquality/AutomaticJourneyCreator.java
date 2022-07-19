package com.example.roadquality;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.ActivityTransitionResult;

public class AutomaticJourneyCreator extends BroadcastReceiver {

    public static final String INTENT_ACTION = "com.example.roadquality.ACTION_PROCESS_ACTIVITY_TRANSITIONS";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && INTENT_ACTION.equals(intent.getAction())) {
            Log.w("asdf", "here");
            if(ActivityTransitionResult.hasResult(intent)) {
                Log.w("awf", ActivityTransitionResult.extractResult(intent).toString());
            }

            if (ActivityTransitionResult.hasResult(intent)) {


                ActivityTransitionResult intentResult = ActivityTransitionResult
                        .extractResult(intent);
                Log.w("Via Automatic Journey Creator Tag", intentResult.toString());
                // handle activity transition result ...
            }
        }

        Log.w("Via Automatic Journey Creator Tag", "I'VE HIT A TRANSITION!!");
        Log.w("asd", intent.toString());
    }
}