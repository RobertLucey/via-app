package com.example.roadquality.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.roadquality.AutomaticJourneyCreator;
import com.example.roadquality.R;
import com.example.roadquality.databinding.FragmentHomeBinding;
import com.example.roadquality.services.MainService;
import com.google.android.gms.common.internal.safeparcel.SafeParcelableSerializer;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.Slider;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

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

        startCycle.setOnClickListener(view -> {
            sendCycleEvent(true);
        });

        stopCycle.setOnClickListener(view -> {
            sendCycleEvent(false);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void sendCycleEvent(boolean entered) {
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
        } catch (Exception exception) {
            System.out.println("Fuck android");
            System.out.println(exception);
        }
    }
}