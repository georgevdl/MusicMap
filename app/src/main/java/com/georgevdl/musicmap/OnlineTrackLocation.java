package com.georgevdl.musicmap;

public class OnlineTrackLocation {

    public double latitude;

    public double longitude;

    public long timestamp;

    public long trackId;

    public String genre;

    public String userId;

    public OnlineTrackLocation(double latitude, double longitude, long timestamp, long trackId, String genre, String userId) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
        this.trackId = trackId;
        this.genre = genre;
        this.userId = userId;
    }

    public OnlineTrackLocation() {
    }

}
