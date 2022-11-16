package com.georgevdl.musicmap.ui.add;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.georgevdl.musicmap.MainActivity;
import com.georgevdl.musicmap.databinding.FragmentAddBinding;

public class AddFragment extends Fragment {

    private FragmentAddBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        com.georgevdl.musicmap.ui.add.AddViewModel homeViewModel =
                new ViewModelProvider(this).get(com.georgevdl.musicmap.ui.add.AddViewModel.class);

        binding = FragmentAddBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        ((MainActivity)getActivity()).setHomeViewModel(homeViewModel);


        final TextView textViewAdd = binding.textAdd;
        homeViewModel.getTextStart().observe(getViewLifecycleOwner(), textViewAdd::setText);
        homeViewModel.getStartVisibility().observe(getViewLifecycleOwner(), textViewAdd::setVisibility);

        // Set the text and the visibility of the results
        final TextView textViewTitleResult = binding.textTitleResult;
        homeViewModel.getTextTitleResult().observe(getViewLifecycleOwner(), textViewTitleResult::setText);
        homeViewModel.getResultsVisibility().observe(getViewLifecycleOwner(), textViewTitleResult::setVisibility);

        final TextView textViewArtistResult = binding.textArtistResult;
        homeViewModel.getTextArtistResult().observe(getViewLifecycleOwner(), textViewArtistResult::setText);
        homeViewModel.getResultsVisibility().observe(getViewLifecycleOwner(), textViewArtistResult::setVisibility);

        final TextView textViewGenreResult = binding.textGenreResult;
        homeViewModel.getTextGenreResult().observe(getViewLifecycleOwner(), textViewGenreResult::setText);
        homeViewModel.getResultsVisibility().observe(getViewLifecycleOwner(), textViewGenreResult::setVisibility);

        final TextView textViewAlbumArtURLResult = binding.textAlbumArtURLResult;
        homeViewModel.getTextAlbumArtURLResult().observe(getViewLifecycleOwner(), textViewAlbumArtURLResult::setText);
        homeViewModel.getResultsVisibility().observe(getViewLifecycleOwner(), textViewAlbumArtURLResult::setVisibility);

        final TextView textViewLyricsResult = binding.textLyricsResult;
        homeViewModel.getTextLyricsResult().observe(getViewLifecycleOwner(), textViewLyricsResult::setText);
        homeViewModel.getLyricsVisibility().observe(getViewLifecycleOwner(), textViewLyricsResult::setVisibility);

        final TextView textViewTitle = binding.textTitle;
        homeViewModel.getResultsVisibility().observe(getViewLifecycleOwner(), textViewTitle::setVisibility);

        final TextView textViewArtist = binding.textArtist;
        homeViewModel.getResultsVisibility().observe(getViewLifecycleOwner(), textViewArtist::setVisibility);

        final TextView textViewGenre = binding.textGenre;
        homeViewModel.getResultsVisibility().observe(getViewLifecycleOwner(), textViewGenre::setVisibility);

        final TextView textViewAlbumArtURL = binding.textAlbumArtURL;
        homeViewModel.getResultsVisibility().observe(getViewLifecycleOwner(), textViewAlbumArtURL::setVisibility);

        final TextView textViewLyrics = binding.textLyrics;
        homeViewModel.getLyricsVisibility().observe(getViewLifecycleOwner(), textViewLyrics::setVisibility);


        final TextView textViewProgressBarDescription = binding.textProgressBarDescription;
        homeViewModel.getTextStatus().observe(getViewLifecycleOwner(), textViewProgressBarDescription::setText);
        homeViewModel.getStatusTextVisibility().observe(getViewLifecycleOwner(), textViewProgressBarDescription::setVisibility);

        final ProgressBar progressBar = binding.progressBar;
        homeViewModel.getProgressBarVisibility().observe(getViewLifecycleOwner(), progressBar::setVisibility);

        final Button buttonTryGPSAgain = binding.buttonTryGpsAgain;
        homeViewModel.getButtonTryGPSAgainVisibility().observe(getViewLifecycleOwner(), buttonTryGPSAgain::setVisibility);

        final Button buttonManuallyPickLocation = binding.buttonManuallyPickLocation;
        homeViewModel.getButtonManuallyPickLocationVisibility().observe(getViewLifecycleOwner(), buttonManuallyPickLocation::setVisibility);

        final Button buttonAddToMyMap = binding.buttonAddToMyMap;
        homeViewModel.getButtonAddToMyMapVisibility().observe(getViewLifecycleOwner(), buttonAddToMyMap::setVisibility);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}