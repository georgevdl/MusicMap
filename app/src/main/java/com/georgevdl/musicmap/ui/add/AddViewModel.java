package com.georgevdl.musicmap.ui.add;

import android.app.Application;
import android.widget.TextView;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.georgevdl.musicmap.MusicMapRoomRepository;
import com.georgevdl.musicmap.Track;
import com.georgevdl.musicmap.TrackLocation;

public class AddViewModel extends AndroidViewModel {

    private final MutableLiveData<String> mTextStart;
    private final MutableLiveData<Integer> mStartVisibility;

    private final MutableLiveData<String> mTextTitleResult;
    private final MutableLiveData<String> mTextArtistResult;
    private final MutableLiveData<String> mTextGenreResult;
    private final MutableLiveData<String> mTextAlbumArtURLResult;
    private final MutableLiveData<String> mTextLyricsResult;

    private final MutableLiveData<Integer> mResultsVisibility;
    private final MutableLiveData<Integer> mLyricsVisibility;

    private final MutableLiveData<String> mTextProgressBarDescription;

    private final MutableLiveData<Integer> mProgressBarVisibility;
    private final MutableLiveData<Integer> mStatusTextVisibility;

    private final MutableLiveData<Integer> mTryGPSAgainButtonVisibility;
    private final MutableLiveData<Integer> mManuallyPickLocationButtonVisibility;
    private final MutableLiveData<Integer> mAddToMyMapButtonVisibility;

    private MusicMapRoomRepository mRepository;


    public AddViewModel(Application application) {
        super(application);
        mTextStart = new MutableLiveData<>();
        mTextStart.setValue("To start, open Shazam and share a song to this app.\n\n\nThis app is not endorsed by, directly affiliated with, maintained, authorized, or sponsored by Shazam or Apple.");
        mStartVisibility = new MutableLiveData<>();
        mStartVisibility.setValue(TextView.VISIBLE);

        mTextTitleResult = new MutableLiveData<>();
        mTextTitleResult.setValue("");

        mTextArtistResult = new MutableLiveData<>();
        mTextArtistResult.setValue("");

        mTextGenreResult = new MutableLiveData<>();
        mTextGenreResult.setValue("");

        mTextAlbumArtURLResult = new MutableLiveData<>();
        mTextAlbumArtURLResult.setValue("");

        mTextLyricsResult = new MutableLiveData<>();
        mTextLyricsResult.setValue("");

        mResultsVisibility = new MutableLiveData<>();
        mResultsVisibility.setValue(TextView.GONE);

        mLyricsVisibility = new MutableLiveData<>();
        mLyricsVisibility.setValue(TextView.GONE);

        mTextProgressBarDescription = new MutableLiveData<>();
        mTextProgressBarDescription.setValue("Downloading track metadata");

        mProgressBarVisibility = new MutableLiveData<>();
        mProgressBarVisibility.setValue(TextView.GONE);
        mStatusTextVisibility = new MutableLiveData<>();
        mStatusTextVisibility.setValue((TextView.GONE));

        mTryGPSAgainButtonVisibility = new MutableLiveData<>();
        mTryGPSAgainButtonVisibility.setValue((TextView.GONE));
        mManuallyPickLocationButtonVisibility = new MutableLiveData<>();
        mManuallyPickLocationButtonVisibility.setValue((TextView.GONE));
        mAddToMyMapButtonVisibility = new MutableLiveData<>();
        mAddToMyMapButtonVisibility.setValue((TextView.GONE));

        mRepository = new MusicMapRoomRepository(application);
    }

    public LiveData<String> getTextStart() {
        return mTextStart;
    }
    public LiveData<Integer> getStartVisibility() {
        return mStartVisibility;
    }

    public LiveData<String> getTextTitleResult() {
        return mTextTitleResult;
    }
    public LiveData<String> getTextArtistResult() {
        return mTextArtistResult;
    }
    public LiveData<String> getTextGenreResult() {
        return mTextGenreResult;
    }
    public LiveData<String> getTextAlbumArtURLResult() {
        return mTextAlbumArtURLResult;
    }
    public LiveData<String> getTextLyricsResult() {
        return mTextLyricsResult;
    }

    public LiveData<Integer> getResultsVisibility() {
        return mResultsVisibility;
    }
    public LiveData<Integer> getLyricsVisibility() {
        return mLyricsVisibility;
    }

    public LiveData<String> getTextStatus() {
        return mTextProgressBarDescription;
    }

    public LiveData<Integer> getProgressBarVisibility() { return mProgressBarVisibility; }
    public LiveData<Integer> getStatusTextVisibility() { return mStatusTextVisibility; }

    public LiveData<Integer> getButtonTryGPSAgainVisibility() { return mTryGPSAgainButtonVisibility; }
    public LiveData<Integer> getButtonManuallyPickLocationVisibility() { return mManuallyPickLocationButtonVisibility; }
    public LiveData<Integer> getButtonAddToMyMapVisibility() { return mAddToMyMapButtonVisibility; }


    /*public void setTextStart(String s) {
        mTextStart.postValue(s);
    }*/

    public void setTextTitleResult(String s) {
        mTextTitleResult.postValue(s);
    }

    public void setTextArtistResult(String s) {
        mTextArtistResult.postValue(s);
    }

    public void setTextGenreResult(String s) {
        mTextGenreResult.postValue(s);
    }

    public void setTextAlbumArtURLResult(String s) {
        mTextAlbumArtURLResult.postValue(s);
    }

    public void setTextLyricsResult(String s) {
        mTextLyricsResult.postValue(s);
    }

    public void setStartVisibility(Integer i) {
        mStartVisibility.postValue(i);
    }

    public void setResultsVisibility(Integer i) {
        mResultsVisibility.postValue(i);
    }

    public void setLyricsVisibility(Integer i) {
        mLyricsVisibility.postValue(i);
    }

    public void setTextStatus(String s) {
        mTextProgressBarDescription.postValue(s);
    }

    public void setProgressBarVisibility(Integer i) {
        mProgressBarVisibility.postValue(i);
    }

    public void setStatusTextVisibility(Integer i) {
        mStatusTextVisibility.postValue(i);
    }

    public void setTryGPSAgainButtonVisibility(Integer i) {
        mTryGPSAgainButtonVisibility.postValue(i);
    }

    public void setManuallyPickLocationButtonVisibility(Integer i) {
        mManuallyPickLocationButtonVisibility.postValue(i);
    }

    public void setAddToMyMapButtonVisibility(Integer i) {
        mAddToMyMapButtonVisibility.postValue(i);
    }

    public void insert(TrackLocation trackLocation) {
        mRepository.insert(trackLocation);
    }

    public void insert(Track track) {
        mRepository.insert(track);
    }

}