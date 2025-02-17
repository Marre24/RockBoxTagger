package com.example.rockboxtagger;



import okhttp3.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class AlbumQuery {

    private static final String BASE_URL = "https://api.discogs.com/database/search?q=";
    private static final String USER_AGENT = "YourAppName/1.0";
    private static final String TOKEN = "pGtSjsiRGpheBMCzoVLlwJgZamcUKGmqBaeNaxHe";
    private int topLeft = 0;
    private int topRight = 1;
    private int bottomLeft = 2;
    private int bottomRight = 3;
    private final List<Album> albums = new ArrayList<>();
    private final OkHttpClient client = new OkHttpClient();

    public Album[] getAlbums(){
        Album[] albums = new Album[4];

        albums[0] = this.albums.get(topLeft);
        albums[1] = this.albums.get(topRight);
        albums[2] = this.albums.get(bottomLeft);
        albums[3] = this.albums.get(bottomRight);

        return albums;
    }

    public void startQuery(String query){
        String url = BASE_URL + query + "&type=release";

        Request request = new Request.Builder().url(url)
                .header("User-Agent", USER_AGENT)
                .header("Authorization", "Discogs token=" + TOKEN)
                .build();

        try (var response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null){
                System.out.println("Request failed. Response code: " + response.code());
                return;
            }

            String jsonResponse = response.body().string();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonResponse);

            JsonNode albums = rootNode.get("results");
            for (JsonNode album : albums)
                tryToAddAlbum(album);

        } catch (IOException e) {
            System.err.println("Could not complete query: IOException was thrown");
            return;
        }

        if (albums.isEmpty()){
            System.err.println("Could not find any albums");
            return;
        }

        for (var a : albums)
            System.out.println(a);
    }

    public void reRoll(int index){
        int newIndex = Math.max(Math.max(topLeft, topRight), Math.max(bottomLeft, bottomRight)) + 1;

        if (albums.size() <= newIndex){
            System.out.println("Could not find any more albums from the query");
            return;
        }

        if (index == 0){
            topLeft = newIndex;
            return;
        }
        if (index == 1){
            topRight = newIndex;
            return;
        }
        if (index == 2){
            bottomLeft = newIndex;
            return;
        }
        bottomRight = newIndex;
    }

    public Album commit(int index){
        Album commited = albums.get(topLeft);

        if (index == 1)
            commited = albums.get(topRight);
        if (index == 2)
            commited = albums.get(bottomLeft);
        if (index == 3)
            commited = albums.get(bottomRight);

        for (var a : albums)
            if (a != commited){
                if (!ImageDownloader.removeFile(Long.toString(a.masterID())))
                    System.err.println("Could not remove file: " + a.masterID());
            }

        return commited;
    }

    private void tryToAddAlbum(JsonNode album) {
        var masterId = album.get("master_id");
        var title = album.get("title");
        var year = album.get("year");
        var genres = album.get("genre");
        var coverImage = album.get("cover_image");

        if ( masterId == null || title == null || year == null || genres == null || coverImage == null ){
            System.out.println("Required information was not given");
            return;
        }

        var list = title.asText().split("-");

        if (list.length != 2){
            System.out.println("Title has bad format: " + album.get("title").asText());
            return;
        }

        if (masterId.asLong() == 0)
            return;

        Album a = new Album(masterId.asLong(), list[1].trim(), list[0].trim(), year.asInt(), Album.getGenre(genres), coverImage.asText());
        if (albums.contains(a))
            return;
        ImageDownloader.downloadImage(coverImage.asText(), masterId.asText());
        albums.addLast(a);
    }
}
