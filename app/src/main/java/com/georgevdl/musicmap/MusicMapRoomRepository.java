package com.georgevdl.musicmap;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class MusicMapRoomRepository {

    private TrackDao mTrackDao;
    private TrackLocationDao mTrackLocationDao;

    private LiveData<List<TrackWithLocations>> mAllResults;


    public MusicMapRoomRepository(Application application) {
        MusicMapRoomDatabase db = MusicMapRoomDatabase.getDatabase(application);
        mTrackDao = db.trackDao();
        mTrackLocationDao = db.trackLocationDao();
        mAllResults = mTrackDao.getAllResults();
    }

    public LiveData<List<TrackWithLocations>> getAllResults() {
        return mAllResults;
    }

    public void insert(Track track) {
        new insertTrackAsyncTask(mTrackDao).execute(track);
    }

    public LiveData<Track> getTrackById(long id) {
        return mTrackDao.getTrackById(id);
    }

    public void insert(TrackLocation trackLocation) {
        new insertTrackLocationAsyncTask(mTrackLocationDao).execute(trackLocation);
    }

    private static class insertTrackAsyncTask extends AsyncTask<Track, Void, Void> {

        private TrackDao mAsyncTaskDao;

        insertTrackAsyncTask(TrackDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Track... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    private static class insertTrackLocationAsyncTask extends AsyncTask<TrackLocation, Void, Void> {

        private TrackLocationDao mAsyncTaskDao;

        insertTrackLocationAsyncTask(TrackLocationDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final TrackLocation... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }


}
