package com.georgevdl.musicmap.ui.my_map;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.georgevdl.musicmap.databinding.FragmentMymapBinding;

public class MyMapFragment extends Fragment {

    private FragmentMymapBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MyMapViewModel myMapViewModel =
                new ViewModelProvider(this).get(MyMapViewModel.class);

        binding = FragmentMymapBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textMymap;
        myMapViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}