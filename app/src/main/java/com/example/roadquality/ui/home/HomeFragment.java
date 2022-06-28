package com.example.roadquality.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.roadquality.R;
import com.example.roadquality.databinding.FragmentHomeBinding;
import com.example.roadquality.services.MainService;
import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.Slider;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private boolean running = false;

    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Set a LabelFormatter on the Sliders to not show ugly floats.
        Slider minutesToCutSlider = binding.minutesToCutSlider;
        Slider metresToCutSlider = binding.metresToCutSlider;

        LabelFormatter sliderInputLabelFormatter = new LabelFormatter() {
            @NonNull
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf(Math.round(value));
            }
        };
        minutesToCutSlider.setLabelFormatter(sliderInputLabelFormatter);
        metresToCutSlider.setLabelFormatter(sliderInputLabelFormatter);


        // "Enhanced Privacy" is simply sending partials and relative times.
        CheckBox enhancedPrivacyCheckbox = binding.enhancedPrivacyCheckBox;

        Button startStop = root.findViewById(R.id.start_stop_button);

        final Button button = binding.startStopButton;
        button.setOnClickListener(v -> {
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
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}