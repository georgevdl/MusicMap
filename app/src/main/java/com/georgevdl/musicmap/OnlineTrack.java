package com.georgevdl.musicmap;

public class OnlineTrack {

    public String title;

    public String artist;

    public String genre;

    public String albumArtURL;

    public OnlineTrack(String title, String artist, String genre, String albumArtURL) {
        this.title = title;
        this.artist = artist;
        this.genre = genre;
        this.albumArtURL = albumArtURL;
    }

    public OnlineTrack() {
    }
}
