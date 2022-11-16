package com.georgevdl.musicmap.ui.global_map;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.georgevdl.musicmap.databinding.FragmentGlobalmapBinding;

public class GlobalMapFragment extends Fragment {

    private FragmentGlobalmapBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GlobalMapViewModel notificationsViewModel =
                new ViewModelProvider(this).get(GlobalMapViewModel.class);

        binding = FragmentGlobalmapBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textGlobalmap;
        notificationsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}