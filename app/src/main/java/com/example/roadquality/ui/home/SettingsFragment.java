package com.example.roadquality.ui.home;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.example.roadquality.R;
import com.example.roadquality.databinding.FragmentHomeBinding;
import com.example.roadquality.databinding.FragmentSettingsBinding;
import com.example.roadquality.services.MainService;
import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.Slider;

public class SettingsFragment extends Fragment {
    private FragmentSettingsBinding binding;
    private boolean running = false;

    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Get the SharedPreferences:
        SharedPreferences preferences = getActivity().getSharedPreferences("Via Preferences", MODE_PRIVATE);
        // This is just a default that should always be set. If not, set all the things we need:
        if (!preferences.contains("metresToCut")) {
            preferences.edit()
                    .putInt("metresToCut", 200)
                    .putInt("minutesToCut", 2)
                    .putBoolean("enhancedPrivacy", false)
                    .apply();
        }

        // Bind the input components:
        Slider minutesToCutSlider = binding.minutesToCutSlider;
        Slider metresToCutSlider = binding.metresToCutSlider;
        // "Enhanced Privacy" is simply sending partials and relative times.
        CheckBox enhancedPrivacyCheckbox = binding.enhancedPrivacyCheckBox;

        // Initialize inputs with values from SharedPreferences:
        metresToCutSlider.setValue((float) preferences.getInt("metresToCut", 200));
        minutesToCutSlider.setValue((float) preferences.getInt("minutesToCut", 2));
        enhancedPrivacyCheckbox.setChecked(preferences.getBoolean("enhancedPrivacy", false));

        // And bind changes to the inputs to the SharedPreferences:
        metresToCutSlider.addOnChangeListener((slider, value, fromUser) -> preferences.edit().putInt("metresToCut", Math.round(value)).apply());
        minutesToCutSlider.addOnChangeListener((slider, value, fromUser) -> preferences.edit().putInt("minutesToCut", Math.round(value)).apply());
        enhancedPrivacyCheckbox.setOnCheckedChangeListener((compoundButton, b) -> preferences.edit().putBoolean("enhancedPrivacy", b).apply());

        // Set a LabelFormatter on the Sliders to not show ugly floats.
        LabelFormatter sliderInputLabelFormatter = value -> String.valueOf(Math.round(value));
        minutesToCutSlider.setLabelFormatter(sliderInputLabelFormatter);
        metresToCutSlider.setLabelFormatter(sliderInputLabelFormatter);



        // TODO: not yet refactored
        Button startStop = root.findViewById(R.id.start_stop_button);

        final Button button = binding.startStopButton;
        button.setOnClickListener(v -> {
            /*
            if (!running) {
                startStop.setText("Stop Journey");

                String transportType = "bike";
                boolean sendRelativeTime = enhancedPrivacyCheckbox.isChecked();
                boolean sendPartials = enhancedPrivacyCheckbox.isChecked();
                int minutesToCut = Math.round(minutesToCutSlider.getValue());
                int metresToCut = Math.round(metresToCutSlider.getValue());

                Intent mainService = new Intent(getActivity(), MainService.class);
                mainService.putExtra("transportType", transportType);
                mainService.putExtra("suspension", Boolean.FALSE);
                mainService.putExtra("sendRelativeTime", sendRelativeTime);
                mainService.putExtra("minutesToCut", minutesToCut);
                mainService.putExtra("metresToCut", metresToCut);
                mainService.putExtra("sendPartials", sendPartials);

                // DELETE ON SEND? - not really important

                getActivity().startForegroundService(mainService);
                running = true;
            } else {
                getActivity().stopService(new Intent(getActivity(), MainService.class));
                running = false;
                startStop.setText("Start Journey");
            }
             */
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}