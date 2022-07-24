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

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}