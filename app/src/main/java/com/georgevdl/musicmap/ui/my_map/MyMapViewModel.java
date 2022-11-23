package com.georgevdl.musicmap.ui.my_map;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.georgevdl.musicmap.MusicMapRoomRepository;
import com.georgevdl.musicmap.Track;
import com.georgevdl.musicmap.TrackWithLocations;

import java.util.List;

public class MyMapViewModel extends AndroidViewModel {

    private MusicMapRoomRepository mRepo;
    private LiveData<List<TrackWithLocations>> mResults;


    //private final MutableLiveData<String> mText;

    public MyMapViewModel(Application application) {
        super(application);
        mRepo = new MusicMapRoomRepository(application);
        mResults = mRepo.getAllResults();

        /*mText = new MutableLiveData<>();
        mText.setValue("Feature not yet implemented");*/
    }

    LiveData<List<TrackWithLocations>> getAllResults() {
        return mResults;
    }

    LiveData<Track> getTrackById(long id) {
        return mRepo.getTrackById(id);
    }

    /*public LiveData<String> getText() {
        return mText;
    }*/
}