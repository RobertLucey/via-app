package via.android.roadquality;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import pub.devrel.easypermissions.EasyPermissions;
import via.android.roadquality.databinding.ActivityMainBinding;
import via.android.roadquality.utils.LokiLogger;


public class MainActivity extends AppCompatActivity /*implements EasyPermissions.PermissionCallbacks */{

    private LokiLogger logger = new LokiLogger("MainActivity.java");

    private static final int permissionsRequestCode = 41;
    private static final String[] perms = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACTIVITY_RECOGNITION,
            // Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    private boolean hasRequiredPermissions() {
        logger.log("Called hasRequiredPermissions...");
        return EasyPermissions.hasPermissions(
                this,
                perms
        );
    }

    private void requestRequiredPermissions(){
        logger.log("Requesting required permissions...");
        EasyPermissions.requestPermissions(
                this,
                "Via needs the following permissions to function properly",
                permissionsRequestCode,
                perms
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults);

        logger.log("Trying to set up broadcast receiver after permissions request result...");
        this.setUpActivityTransitionBroadcastReceiver();
    }

    private void makeFullScreen() {
        // Hide the nav bottom bar and show when swiped up:
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().setDecorFitsSystemWindows(false);
            WindowInsetsController controller = getWindow().getInsetsController();
            if (controller != null) {
                controller.hide(WindowInsets.Type.navigationBars());
                controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        } else {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            );
        }
    }

    private List<ActivityTransition> getActivityTransitionsList() {
        List<ActivityTransition> transitions = new ArrayList<>();

        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.STILL)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                        .build());
        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.STILL)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                        .build());
        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.WALKING)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                        .build());
        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.WALKING)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                        .build());
        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.RUNNING)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                        .build());
        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.RUNNING)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                        .build());
        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.ON_BICYCLE)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                        .build());
        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.ON_BICYCLE)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                        .build());
        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.IN_VEHICLE)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                        .build());
        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.IN_VEHICLE)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                        .build());

        return transitions;
    }

    private void setUpActivityTransitionBroadcastReceiver() {
        logger.log("Setting up ActivityTransitionBroadcastReceiver...");

        // TODO: Needs to check background too:
        if (this.hasRequiredPermissions() && EasyPermissions.hasPermissions(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        ) {
            logger.log("Have sufficient permissions for background activities");
            ActivityTransitionRequest request = new ActivityTransitionRequest(this.getActivityTransitionsList());

            Intent intent = new Intent(this, AutomaticJourneyCreator.class);
            intent.setAction(AutomaticJourneyCreator.INTENT_ACTION);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent,
                    PendingIntent.FLAG_MUTABLE);

            Task<Void> task = ActivityRecognition.getClient(this)
                    .requestActivityUpdates(150 * 1000, pendingIntent);
                    //.requestActivityTransitionUpdates(request, pendingIntent);

            task.addOnSuccessListener(unused -> logger.log("task was successful"));
            task.addOnFailureListener(unused -> logger.log("task was unsuccessful"));
        } else {
            logger.log("Don't have permissions to start background service");
            this.requestRequiredPermissions();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Check the device ID is set or not:
        SharedPreferences sharedPreferences = this.getSharedPreferences("Via Preferences", MODE_PRIVATE);
        if (Objects.equals(sharedPreferences.getString("device_id", "device_id_not_set"), "device_id_not_set")) {
            sharedPreferences.edit().putString("device_id", UUID.randomUUID().toString()).apply();
        }

        this.logger = new LokiLogger(this, "MainActivity.java");
        this.logger.log("onCreate called.");

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // Hides the title
        getSupportActionBar().hide();

        // It would be nice to make this work but it just messes up the sizing on the app fragments.
        // makeFullScreen();

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,
                R.id.navigation_web_view,
                R.id.navigation_settings_view
        ).build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        if (!this.hasRequiredPermissions()) {
            this.requestRequiredPermissions();
        }

        this.logger.log("onCreate getting SharedPreferences");
        SharedPreferences preferences = this.getSharedPreferences("Via Preferences", MODE_PRIVATE);
        this.logger.log("onCreate got value of backgroundCollection: " + String.valueOf(preferences.getBoolean("backgroundCollection", false)));

        if (preferences.getBoolean("backgroundCollection", false)) {
            this.logger.log("onCreate setting up ActivityTransitionBroadcastReceiver");
            this.setUpActivityTransitionBroadcastReceiver();
        } else {
            this.logger.log("onCreate not setting up ActivityTransitionBroadcastReceiver");
        }
    }

    @Override
    protected void onDestroy() {
        this.logger.log("onDestroy called.");
        super.onDestroy();
        this.logger.close();
        logger.log("onDestroy completed.");
    }
}
