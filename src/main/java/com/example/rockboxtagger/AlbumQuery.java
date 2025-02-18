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
    private static final String MASTER_URL = "https://api.discogs.com/masters/";
    private int topLeft = 0;
    private int topRight = 1;
    private int bottomLeft = 2;
    private int bottomRight = 3;
    private final List<Album> albums = new ArrayList<>();
    private final OkHttpClient client = new OkHttpClient();

    public Album[] getAlbums(){
        Album[] albums = new Album[4];

        var tlAlbum = this.albums.get(topLeft);
        var trAlbum = this.albums.get(topRight);
        var blAlbum = this.albums.get(bottomLeft);
        var brAlbum = this.albums.get(bottomRight);

        ImageDownloader.downloadImage(tlAlbum.imageUrl(), Long.toString(tlAlbum.masterID()));
        ImageDownloader.downloadImage(trAlbum.imageUrl(), Long.toString(trAlbum.masterID()));
        ImageDownloader.downloadImage(blAlbum.imageUrl(), Long.toString(blAlbum.masterID()));
        ImageDownloader.downloadImage(brAlbum.imageUrl(), Long.toString(brAlbum.masterID()));

        albums[0] = tlAlbum;
        albums[1] = trAlbum;
        albums[2] = blAlbum;
        albums[3] = brAlbum;

        return albums;
    }

    public void startQuery(String query){
        String url = BASE_URL + query + "&type=master";

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
            var a = albums.get(topLeft);
            if (!ImageDownloader.removeFile(Long.toString(a.masterID())))
                System.err.println("Could not remove file: " + a.masterID());
            topLeft = newIndex;
            return;
        }
        if (index == 1){
            var a = albums.get(topRight);
            if (!ImageDownloader.removeFile(Long.toString(a.masterID())))
                System.err.println("Could not remove file: " + a.masterID());
            topRight = newIndex;
            return;
        }
        if (index == 2){
            var a = albums.get(bottomLeft);
            if (!ImageDownloader.removeFile(Long.toString(a.masterID())))
                System.err.println("Could not remove file: " + a.masterID());
            bottomLeft = newIndex;
            return;
        }
        var a = albums.get(bottomRight);
        if (!ImageDownloader.removeFile(Long.toString(a.masterID())))
            System.err.println("Could not remove file: " + a.masterID());
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

        ImageDownloader.commitFile(Long.toString(commited.masterID()));

        if (index != 0)
            ImageDownloader.removeFile(Long.toString(albums.get(topLeft).masterID()));
        if (index != 1)
            ImageDownloader.removeFile(Long.toString(albums.get(topRight).masterID()));
        if (index != 2)
            ImageDownloader.removeFile(Long.toString(albums.get(bottomLeft).masterID()));
        if (index != 3)
            ImageDownloader.removeFile(Long.toString(albums.get(bottomRight).masterID()));

        fetchTrackList(commited);

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

        Album a = new Album(masterId.asLong(), list[1].trim(), list[0].trim(), year.asInt(), Album.getGenre(genres), coverImage.asText(), new ArrayList<>());
        if (albums.contains(a))
            return;
        albums.addLast(a);
    }

    public static void fetchTrackList(Album album) {
        OkHttpClient client = new OkHttpClient();

        String url = MASTER_URL + album.masterID();

        Request request = new Request.Builder().url(url)
                .header("User-Agent", USER_AGENT)
                .header("Authorization", "Discogs token=" + TOKEN)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                System.err.println("Failed to fetch trackList for: " + album);
                return;
            }

            String jsonResponse = response.body().string();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            JsonNode trackList = rootNode.get("tracklist");

            if (trackList == null || !trackList.isArray() || trackList.isEmpty()) {
                System.err.println("No trackList available for:" + album);
                return;
            }

            for (JsonNode track : trackList) {
                tryToAddTrackTo(track, album);
            }
            //{
            // "position":"12",
            // "type_":"track",
            // "title":"Are We Still Friends?",
            // "extraartists":[{"name":"Pharrell Williams","anv":"","join":"","role":"Backing Vocals","tracks":"","id":90037,"resource_url":"https://api.discogs.com/artists/90037"},{"name":"Pharrell Williams","anv":"","join":"","role":"Featuring","tracks":"","id":90037,"resource_url":"https://api.discogs.com/artists/90037"}],
            // "duration":"4:25"
            // }


        } catch (IOException e) {
            System.err.println("Error fetching trackList: " + e.getMessage());
        }
    }

    private static void tryToAddTrackTo(JsonNode track, Album album) {
        var title = track.get("title");
        var position = track.get("position");
        var duration = track.has("duration") ? track.get("duration").asText() : "N/A";
        var extraartists = track.get("extraartists");
        if (title == null || position == null){
            System.err.println("Track don't have either title or position");
            return;
        }

        List<String> features = new ArrayList<>();
        if (extraartists != null && extraartists.isArray())
            for(var a : extraartists){
                if (a.get("role").asText().equals("Featuring")){
                    String s = a.get("name").asText();
                    var arr = s.split("\\(");
                    if (arr.length > 1)
                        s = arr[0].trim();
                    features.add(s);
                }
            }

        Song s = new Song(title.asText(), album.artist(), duration, position.asInt(), features);
        System.out.println(s);
        album.addSong(s);
    }

}
