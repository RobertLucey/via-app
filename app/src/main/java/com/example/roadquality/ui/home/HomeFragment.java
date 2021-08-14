package com.example.roadquality.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.roadquality.R;
import com.example.roadquality.databinding.FragmentHomeBinding;
import com.example.roadquality.services.MainService;


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

        Spinner transportTypeSpinner = binding.staticSpinner;
        ArrayAdapter<CharSequence> transportTypeAdapter = ArrayAdapter.createFromResource(
                getActivity(),
                R.array.transport_type,
                android.R.layout.simple_spinner_item
        );
        transportTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        transportTypeSpinner.setAdapter(transportTypeAdapter);


        Spinner minutesToCutSpinner = binding.minutesToCut;
        ArrayAdapter<CharSequence> minutesToCutAdapter = ArrayAdapter.createFromResource(
                getActivity(),
                R.array.minutes_to_cut,
                android.R.layout.simple_spinner_item
        );
        minutesToCutAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        minutesToCutSpinner.setAdapter(minutesToCutAdapter);


        Spinner metresToCutSpinner = binding.metresToCut;
        ArrayAdapter<CharSequence> metresToCutAdapter = ArrayAdapter.createFromResource(
                getActivity(),
                R.array.metres_to_cut,
                android.R.layout.simple_spinner_item
        );
        metresToCutAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        metresToCutSpinner.setAdapter(metresToCutAdapter);


        CheckBox suspensionCheckbox = (CheckBox) binding.suspensionCheckbox;
        CheckBox relativeTimeCheckbox = (CheckBox) binding.relativeTimeCheckbox;
        relativeTimeCheckbox.setChecked(true);
        CheckBox sendPartialsCheckbox = (CheckBox) binding.sendInPartials;

        // If we send partials we should never send relative time
        sendPartialsCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    relativeTimeCheckbox.setChecked(false);
                }
            }
        });

        final Button button = binding.startStopButton;
        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                if (!running) {

                    boolean suspension = suspensionCheckbox.isChecked();
                    boolean sendRelativeTime = relativeTimeCheckbox.isChecked();
                    boolean sendPartials = sendPartialsCheckbox.isChecked();

                    Intent mainService = new Intent(getActivity(), MainService.class);
                    mainService.putExtra("transportType", transportTypeSpinner.getSelectedItem().toString());
                    mainService.putExtra("suspension", suspension);
                    mainService.putExtra("sendRelativeTime", sendRelativeTime);
                    mainService.putExtra("minutesToCut", Integer.parseInt(minutesToCutSpinner.getSelectedItem().toString()));
                    mainService.putExtra("metresToCut", Integer.parseInt(metresToCutSpinner.getSelectedItem().toString()));
                    mainService.putExtra("sendPartials", sendPartials);

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