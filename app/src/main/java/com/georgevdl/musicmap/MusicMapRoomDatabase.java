package com.georgevdl.musicmap;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Track.class, TrackLocation.class}, version = 1, exportSchema = false)
public abstract class MusicMapRoomDatabase extends RoomDatabase {

    public abstract TrackDao trackDao();

    public abstract TrackLocationDao trackLocationDao();

    private static MusicMapRoomDatabase INSTANCE;

    public static MusicMapRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (MusicMapRoomDatabase.class) {
                if (INSTANCE == null) {
                    // Create database here
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    MusicMapRoomDatabase.class, "musicMap_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }


}
