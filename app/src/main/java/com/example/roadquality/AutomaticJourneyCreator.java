package com.example.roadquality;

import static android.content.Context.MODE_PRIVATE;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.roadquality.services.MainService;
import com.example.roadquality.utils.LokiLogger;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

public class AutomaticJourneyCreator extends BroadcastReceiver {
    private LokiLogger logger = new LokiLogger();

    public static final String INTENT_ACTION = "com.example.roadquality.ACTION_PROCESS_ACTIVITY_TRANSITIONS";

    private void showNotification(Context context, String content) {
        String CHANNEL_ID = "xrtckujvhb";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_directions_bike_24)
                .setContentTitle("Via Activity Detected")
                .setContentText("Detected: " + content)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "channel name",
                NotificationManager.IMPORTANCE_HIGH
        );

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.createNotificationChannel(channel);
        notificationManager.notify(1, builder.build());
    }

    private void startCycleJourney(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Via Preferences", MODE_PRIVATE);
        logger.log("Got sharedPrefs in startCycleJourney");
        Boolean enhancedPrivacy = sharedPreferences.getBoolean("enhancedPrivacy", false);
        int metresToCut = sharedPreferences.getInt("metresToCut", 100);
        int minutesToCut = sharedPreferences.getInt("minutesToCut", 1);

        Intent mainService = new Intent(context.getApplicationContext(), MainService.class);
        mainService.putExtra("transportType", "bike");
        mainService.putExtra("suspension", Boolean.FALSE);
        mainService.putExtra("sendRelativeTime", enhancedPrivacy);
        mainService.putExtra("minutesToCut", minutesToCut);
        mainService.putExtra("metresToCut", metresToCut);
        mainService.putExtra("sendPartials", enhancedPrivacy);

        logger.log("Created intent in startCycleJourney");
        context.getApplicationContext().startService(mainService);
        logger.log("Started bike journey!");
    }

    private void stopCycleJourney(Context context) {
        context.getApplicationContext().stopService(new Intent(context.getApplicationContext(), MainService.class));
        logger.log("Stopped bike journey!");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && INTENT_ACTION.equals(intent.getAction())) {
            if (ActivityTransitionResult.hasResult(intent)) {
                ActivityTransitionResult intentResult = ActivityTransitionResult
                        .extractResult(intent);
                List<ActivityTransitionEvent> transitionEvents = intentResult.getTransitionEvents();

                for(int i=0; i<transitionEvents.size(); i++) {
                    ActivityTransitionEvent transitionEvent = transitionEvents.get(i);
                    int transitionType = transitionEvent.getTransitionType();

                    if (transitionEvent.getActivityType() == DetectedActivity.ON_BICYCLE) {
                        if (transitionEvent.getTransitionType() == ActivityTransition.ACTIVITY_TRANSITION_ENTER) {
                            logger.log(
                                    "AutomaticJourneyCreator",
                                    "Cycle started."
                            );

                            try {
                                startCycleJourney(context);
                            } catch (Exception exception) {
                                logger.log("startCycleJourney threw! " + exception.toString());
                            }
                        } else {
                            logger.log(
                                    "AutomaticJourneyCreator",
                                    "Cycle finished."
                            );

                            stopCycleJourney(context);
                        }
                    }

                    // showNotification(context, "Activity " + String.valueOf(activityType) + " changed to " + String.valueOf(transitionType));
                }
            }
        }
    }
}