package com.example.rockboxtagger;



import okhttp3.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class AlbumQuery {
    private int topLeft = 0;
    private int topRight = 1;
    private int bottomLeft = 2;
    private int bottomRight = 3;
    private final List<MasterRelease> masterReleases = new ArrayList<>();
    private final OkHttpClient client = new OkHttpClient();

    public MasterRelease[] getAlbums(){
        MasterRelease[] masterReleases = new MasterRelease[4];

        var tlAlbum = this.masterReleases.get(topLeft);
        var trAlbum = this.masterReleases.get(topRight);
        var blAlbum = this.masterReleases.get(bottomLeft);
        var brAlbum = this.masterReleases.get(bottomRight);

        ImageDownloader.downloadImage(tlAlbum.imageUrl(), Long.toString(tlAlbum.masterID()));
        ImageDownloader.downloadImage(trAlbum.imageUrl(), Long.toString(trAlbum.masterID()));
        ImageDownloader.downloadImage(blAlbum.imageUrl(), Long.toString(blAlbum.masterID()));
        ImageDownloader.downloadImage(brAlbum.imageUrl(), Long.toString(brAlbum.masterID()));

        masterReleases[0] = tlAlbum;
        masterReleases[1] = trAlbum;
        masterReleases[2] = blAlbum;
        masterReleases[3] = brAlbum;

        return masterReleases;
    }

    public void startQuery(String query){
        String url = DiscogsConstants.BASE_URL + query + "&type=master";

        Request request = new Request.Builder().url(url)
                .header("User-Agent", DiscogsConstants.USER_AGENT)
                .header("Authorization", "Discogs token=" + DiscogsConstants.TOKEN)
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

        if (masterReleases.isEmpty()){
            System.err.println("Could not find any albums");
            return;
        }

        for (var a : masterReleases)
            System.out.println(a);
    }

    public void reRoll(int index){
        int newIndex = Math.max(Math.max(topLeft, topRight), Math.max(bottomLeft, bottomRight)) + 1;

        if (masterReleases.size() <= newIndex){
            System.out.println("Could not find any more albums from the query");
            return;
        }

        if (index == 0){
            var a = masterReleases.get(topLeft);
            if (!ImageDownloader.removeFile(Long.toString(a.masterID())))
                System.err.println("Could not remove file: " + a.masterID());
            topLeft = newIndex;
            return;
        }
        if (index == 1){
            var a = masterReleases.get(topRight);
            if (!ImageDownloader.removeFile(Long.toString(a.masterID())))
                System.err.println("Could not remove file: " + a.masterID());
            topRight = newIndex;
            return;
        }
        if (index == 2){
            var a = masterReleases.get(bottomLeft);
            if (!ImageDownloader.removeFile(Long.toString(a.masterID())))
                System.err.println("Could not remove file: " + a.masterID());
            bottomLeft = newIndex;
            return;
        }
        var a = masterReleases.get(bottomRight);
        if (!ImageDownloader.removeFile(Long.toString(a.masterID())))
            System.err.println("Could not remove file: " + a.masterID());
        bottomRight = newIndex;
    }

    public MasterRelease commit(int index){
        MasterRelease commited = masterReleases.get(topLeft);

        if (index == 1)
            commited = masterReleases.get(topRight);
        if (index == 2)
            commited = masterReleases.get(bottomLeft);
        if (index == 3)
            commited = masterReleases.get(bottomRight);

        ImageDownloader.commitFile(Long.toString(commited.masterID()));

        if (index != 0)
            ImageDownloader.removeFile(Long.toString(masterReleases.get(topLeft).masterID()));
        if (index != 1)
            ImageDownloader.removeFile(Long.toString(masterReleases.get(topRight).masterID()));
        if (index != 2)
            ImageDownloader.removeFile(Long.toString(masterReleases.get(bottomLeft).masterID()));
        if (index != 3)
            ImageDownloader.removeFile(Long.toString(masterReleases.get(bottomRight).masterID()));

        TrackListQuery.setTrackLists(commited);

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

        MasterRelease a = new MasterRelease(
                masterId.asLong(),
                list[1].trim(),
                list[0].trim(),
                year.asInt(),
                MasterRelease.getGenre(genres),
                coverImage.asText(),
                new ArrayList<>());

        if (masterReleases.contains(a))
            return;
        masterReleases.addLast(a);
    }
}
