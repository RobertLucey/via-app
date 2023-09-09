package via.android.roadquality.ui.home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import via.android.roadquality.AutomaticJourneyCreator;

import via.android.roadquality.databinding.FragmentHomeBinding;

import via.android.roadquality.utils.LokiLogger;
import com.google.android.gms.common.internal.safeparcel.SafeParcelableSerializer;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class HomeFragment extends Fragment {

    private final LokiLogger logger = new LokiLogger("HomeFragment.java");
    private FragmentHomeBinding binding;

    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Button startCycle = binding.simulateStartCycleButton;
        Button stopCycle = binding.simulateStopCycleButton;

        startCycle.setOnClickListener(view -> sendCycleEvent(true));
        stopCycle.setOnClickListener(view -> sendCycleEvent(false));

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void sendCycleEvent(boolean entered) {

        if (entered) {

            CharSequence text = "Hello toast!";
            int duration = Toast.LENGTH_SHORT;

            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast toast = Toast.makeText(getContext(), "Must allow location all the time", Toast.LENGTH_LONG);
                toast.show();
                return;
            }
            this.logger.log("Manually firing start cycle activity transition event");
        } else {
            this.logger.log("Manually firing stop cycle activity transition event");
        }
        Intent intent = new Intent(getContext(), AutomaticJourneyCreator.class);
        intent.setAction(AutomaticJourneyCreator.INTENT_ACTION);

        List<ActivityTransitionEvent> events = new ArrayList<>();
        ActivityTransitionEvent event = new ActivityTransitionEvent(
                DetectedActivity.ON_BICYCLE,
                entered ? ActivityTransition.ACTIVITY_TRANSITION_ENTER : ActivityTransition.ACTIVITY_TRANSITION_EXIT,
                SystemClock.elapsedRealtimeNanos()
        );
        events.add(event);
        ActivityTransitionResult result = new ActivityTransitionResult(events);
        SafeParcelableSerializer.serializeToIntentExtra(result, intent, "com.google.android.location.internal.EXTRA_ACTIVITY_TRANSITION_RESULT");
        try {
            getContext().sendBroadcast(intent);
            this.logger.log("Event sent successfully");
        } catch (Exception exception) {
            this.logger.log("Event failed to broadcast:");
            this.logger.log(exception.toString());
            this.logger.log(exception.getMessage());
            this.logger.log(Arrays.toString(exception.getStackTrace()));
        }
    }
}
