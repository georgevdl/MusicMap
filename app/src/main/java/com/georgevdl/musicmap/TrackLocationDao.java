package com.georgevdl.musicmap;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TrackLocationDao {

    @Insert
    void insert(TrackLocation trackLocation);

    @Query("SELECT * from trackLocation_table ORDER BY timestamp asc")
    LiveData<List<TrackLocation>> getAllResults();

}
