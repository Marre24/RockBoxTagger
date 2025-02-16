package com.example.rockboxtagger;

import java.io.*;

public class DiscogsApi {

    private static final String BASE_URL = "https://api.discogs.com";
    private static final String USER_AGENT = "YourAppName/1.0";
    private static final String TOKEN = "pGtSjsiRGpheBMCzoVLlwJgZamcUKGmqBaeNaxHe";

    public static AlbumQuery searchForAlbum(String query){
        String url = BASE_URL + "/database/search?q=" + query + "&type=release";
        AlbumQuery albumQuery = new AlbumQuery();
        try {
            albumQuery.startQuery(TOKEN, USER_AGENT, url);
        } catch (Exception e) {
            System.err.println("Error when searching for: " + query + "\n" + e.getMessage());
            return null;
        }
        return albumQuery;
    }
}
