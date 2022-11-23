package com.georgevdl.musicmap;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "track_table")
public class Track {

    @ColumnInfo(name = "title")
    final public String mTitle;

    @ColumnInfo(name = "artist")
    final public String mArtist;

    @ColumnInfo(name = "genre")
    final public String mGenre;

    @ColumnInfo(name = "album_art_URL")
    final public String mAlbumArtURL;

    @ColumnInfo(name = "lyrics")
    final public String mLyrics;

    @PrimaryKey
    @ColumnInfo(name = "id")
    final public long mId;

    public Track(@NonNull String title, @NonNull String artist, @NonNull String genre, @NonNull String albumArtURL, @NonNull String lyrics, long id) {
        this.mTitle = title;
        this.mArtist = artist;
        this.mGenre = genre;
        this.mAlbumArtURL = albumArtURL;
        this.mLyrics = lyrics;
        this.mId = id;
    }
}
