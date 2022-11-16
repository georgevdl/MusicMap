package com.georgevdl.musicmap;

import android.util.Log;
import android.webkit.JavascriptInterface;

public class MyJavaScriptInterface {

    MainActivity ma;

    private String title = "Title: ";
    private String artist = "Artist: ";
    private String genre = "Genre: ";
    private String albumArtURL = "Album Art URL: ";
    private String lyrics = "Lyrics: ";
    private String URL;

    MyJavaScriptInterface(MainActivity mainActivity, String trackURL){
        ma = mainActivity;
        URL = trackURL;
    }


    @JavascriptInterface
    public synchronized void showHTML(String _html) {

        if(_html.startsWith(title) && _html.length() > 8)
            title = _html.substring(7);
        else if(_html.startsWith(artist) && _html.length() > 9)
            artist = _html.substring(8);
        else if(_html.startsWith(genre) && _html.length() > 8)
            genre = _html.substring(7);
        else if(_html.startsWith(albumArtURL) && _html.length() > 16)
            albumArtURL = _html.substring(15);
        else if(_html.startsWith(lyrics)){
            if(_html.length() > 9)
                lyrics = _html.substring(8);
        }
        else if(_html.length() > 0){
            Log.d("mjsi", _html);
            lyrics += "\n\n";
            lyrics += _html;
        }

        String[] s = {title, artist, genre, albumArtURL, lyrics, URL};
        if(!title.equals("Title: ") && !artist.equals("Artist: ") && !genre.equals("Genre: ")){
            ma.setTrackDetails(s);
        }
    }
}
