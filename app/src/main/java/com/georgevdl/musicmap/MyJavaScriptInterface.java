package com.georgevdl.musicmap;

import android.webkit.JavascriptInterface;

public class MyJavaScriptInterface {

    MainActivity ma;

    public static final String titleStart = "Title: ";
    public static final String artistStart = "Artist: ";
    public static final String genreStart = "Genre: ";
    public static final String albumArtURLStart = "Album Art URL: ";
    public static final String lyricsStart = "Lyrics: ";
    public static final String emptyString = "";
    private String title = titleStart;
    private String artist = artistStart;
    private String genre = genreStart;
    private String albumArtURL = albumArtURLStart;
    private String lyrics = lyricsStart;
    private boolean ready = false;
    private final String URL;

    MyJavaScriptInterface(MainActivity mainActivity, String trackURL) {
        ma = mainActivity;
        URL = trackURL;
    }


    @JavascriptInterface
    public synchronized void showHTML(String _html) {

        if (_html.startsWith(titleStart) && _html.length() > 8)
            title = _html.substring(7);
        else if (_html.startsWith(artistStart) && _html.length() > 9)
            artist = _html.substring(8);
        else if (_html.startsWith(genreStart)) {
            if (_html.length() > 8)
                genre = _html.substring(7);
            else
                genre = emptyString;
        } else if (_html.startsWith(albumArtURLStart)) {
            if (_html.endsWith("/nocoverart.jpg") || _html.length() <= 16)
                albumArtURL = emptyString;
            else
                albumArtURL = _html.substring(15);
        } else if (_html.startsWith(lyricsStart)) {
            if (_html.length() > 9)
                lyrics = _html.substring(8);
            else
                lyrics = emptyString;
        } else if (_html.length() > 0) {
            lyrics += "\n\n";
            lyrics += _html;
            ready = true;
        } else if (_html.length() == 0)
            ready = true;

        String[] s = {title, artist, genre, albumArtURL, lyrics, URL};
        if (!title.equals(titleStart) && !artist.equals(artistStart) && !genre.equals(genreStart) && !albumArtURL.equals(albumArtURLStart) && !lyrics.equals(lyricsStart) && ready) {
            ma.setTrackInfo(s);
        }
    }
}
