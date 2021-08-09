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

import com.example.roadquality.MainService;
import com.example.roadquality.R;
import com.example.roadquality.databinding.FragmentHomeBinding;


public class HomeFragment extends Fragment  {

    private FragmentHomeBinding binding;
    private boolean running = false;

    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        Spinner staticSpinner = binding.staticSpinner;
        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter.createFromResource(
                getActivity(),
                R.array.transport_type,
                android.R.layout.simple_spinner_item
        );
        staticAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        staticSpinner.setAdapter(staticAdapter);


        Spinner minutesToCut = binding.minutesToCut;
        ArrayAdapter<CharSequence> minutesToCutAdapter = ArrayAdapter.createFromResource(
                getActivity(),
                R.array.minutes_to_cut,
                android.R.layout.simple_spinner_item
        );
        minutesToCutAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        minutesToCut.setAdapter(minutesToCutAdapter);


        Spinner metresToCut = binding.metresToCut;
        ArrayAdapter<CharSequence> metresToCutAdapter = ArrayAdapter.createFromResource(
                getActivity(),
                R.array.metres_to_cut,
                android.R.layout.simple_spinner_item
        );
        metresToCutAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        metresToCut.setAdapter(metresToCutAdapter);


        CheckBox suspensionCheckbox = (CheckBox) binding.suspensionCheckbox;
        CheckBox relativeTimeCheckbox = (CheckBox) binding.relativeTimeCheckbox;

        final Button button = binding.startStopButton;
        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                if (!running) {

                    boolean suspension = suspensionCheckbox.isChecked();
                    boolean sendRelativeTime = relativeTimeCheckbox.isChecked();

                    Intent mainService = new Intent(getActivity(), MainService.class);
                    mainService.putExtra("transportType", staticSpinner.getSelectedItem().toString());
                    mainService.putExtra("suspension", suspension);
                    mainService.putExtra("sendRelativeTime", sendRelativeTime);
                    mainService.putExtra("minutesToCut", Integer.parseInt(minutesToCut.getSelectedItem().toString()));
                    mainService.putExtra("metresToCut", Integer.parseInt(metresToCut.getSelectedItem().toString()));

                    // DELETE ON SEND? - not really important

                    getActivity().startForegroundService(mainService);
                    running = true;
                } else {
                    getActivity().stopService(new Intent(getActivity(), MainService.class));
                    running = false;
                }
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