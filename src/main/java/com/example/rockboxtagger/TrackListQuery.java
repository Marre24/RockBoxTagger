package com.example.rockboxtagger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TrackListQuery {

    public static void setTrackLists(MasterRelease masterRelease) {
        OkHttpClient client = new OkHttpClient();

        String url = DiscogsConstants.MASTER_URL + masterRelease.masterID() + "/versions";

        Request request = new Request.Builder().url(url)
                .header("User-Agent", DiscogsConstants.USER_AGENT)
                .header("Authorization", "Discogs token=" + DiscogsConstants.TOKEN)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                System.err.println("Failed to fetch releases for: " + masterRelease);
                return;
            }

            String jsonResponse = response.body().string();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            JsonNode releases = rootNode.get("versions");

            if (releases == null || !releases.isArray() || releases.isEmpty()) {
                System.err.println("No releases found for:" + masterRelease);
                return;
            }
            for (var release : releases){
                int releaseId = release.has("id") ? release.get("id").asInt() : -1;

                if (releaseId != -1){
                    var tracklist = getTrackList(releaseId);
                    if (tracklist != null)
                        masterRelease.addTrackList(tracklist);
                }
            }

        } catch (IOException e) {
            System.err.println("Error fetching trackList: " + e.getMessage());
        }
    }

    private static TrackList getTrackList(int releaseId) {
        OkHttpClient client = new OkHttpClient();
        String url = DiscogsConstants.RELEASE_URL + releaseId;
        TrackList trackList = new TrackList(new ArrayList<>());

        Request request = new Request.Builder().url(url)
                .header("User-Agent", DiscogsConstants.USER_AGENT)
                .header("Authorization", "Discogs token=" + DiscogsConstants.TOKEN)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                System.err.println("Failed to fetch tracklist for Release ID: " + releaseId);
                return null;
            }

            String jsonResponse = response.body().string();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            JsonNode jsonTrackList = rootNode.get("tracklist");

            if (jsonTrackList == null || !jsonTrackList.isArray() || jsonTrackList.isEmpty()) {
                System.err.println("No tracklist available for Release ID: " + releaseId);
                return null;
            }
            int trackNr = 0;
            for (JsonNode track : jsonTrackList) {
                var song = getTrack(track, ++trackNr);
                if (song != null)
                    trackList.songs().add(song);
            }
        } catch (IOException e) {
            System.err.println("Error fetching tracklist: " + e.getMessage());
        }

        return trackList;
    }


    private static Song getTrack(JsonNode track, int trackNr) {
        var title = track.get("title");
        var duration = track.has("duration") ? track.get("duration").asText() : "N/A";
        var extraartists = track.get("extraartists");
        if (title == null){
            System.err.println("Track don't have either title or position");
            return null;
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

        return new Song(title.asText(), duration, trackNr, features);
    }
}
