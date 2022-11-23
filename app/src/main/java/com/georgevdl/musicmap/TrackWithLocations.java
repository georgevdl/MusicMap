package com.georgevdl.musicmap;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class TrackWithLocations {
    @Embedded
    public Track track;
    @Relation(
            parentColumn = "id",
            entityColumn = "trackId"
    )
    public List<TrackLocation> trackLocations;

}
