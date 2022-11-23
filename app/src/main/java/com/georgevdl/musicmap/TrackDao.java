package com.georgevdl.musicmap;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface TrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Track track);

    @Query("SELECT * from track_table WHERE id = :id")
    LiveData<Track> getTrackById(long id);

    @Transaction
    @Query("SELECT * FROM track_table")
    LiveData<List<TrackWithLocations>> getAllResults();


}
