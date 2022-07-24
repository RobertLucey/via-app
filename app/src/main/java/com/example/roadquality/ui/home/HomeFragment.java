package com.example.roadquality.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.roadquality.R;
import com.example.roadquality.databinding.FragmentHomeBinding;
import com.example.roadquality.services.MainService;
import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.Slider;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}