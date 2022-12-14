package com.georgevdl.musicmap;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "trackLocation_table")
public class TrackLocation {

    @ColumnInfo(name = "latitude")
    public double mLatitude;

    @ColumnInfo(name = "longitude")
    public double mLongitude;

    @PrimaryKey
    @ColumnInfo(name = "timestamp")
    public long mTimestamp;

    @ColumnInfo(name = "trackId")
    public long mTrackId;

    public TrackLocation(@NonNull double latitude, @NonNull double longitude, @NonNull long timestamp, @NonNull long trackId) {
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        this.mTimestamp = timestamp;
        this.mTrackId = trackId;
    }

}
