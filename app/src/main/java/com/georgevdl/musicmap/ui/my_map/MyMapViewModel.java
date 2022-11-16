package com.georgevdl.musicmap.ui.my_map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MyMapViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public MyMapViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Feature not yet implemented");
    }

    public LiveData<String> getText() {
        return mText;
    }
}