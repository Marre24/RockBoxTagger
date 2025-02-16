package com.example.rockboxtagger;



import okhttp3.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;


public class AlbumQuery {

    Map<Long, Album> map = new HashMap<>();

    OkHttpClient client = new OkHttpClient();

    public void startQuery(String token, String userAgent, String url) throws Exception{

        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", userAgent)
                .header("Authorization", "Discogs token=" + token)
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
            for (JsonNode album : albums) {
                var masterId = album.get("master_id");
                var title = album.get("title");
                var year = album.get("year");
                var genres = album.get("genre");
                var coverImage = album.get("cover_image");

                if ( masterId == null || title == null || year == null || genres == null || coverImage == null ){
                    System.out.println("Required information was not given");
                    continue;
                }

                var list = title.asText().split("-");

                if (list.length != 2){
                    System.out.println("Title has bad format: " + album.get("title").asText());
                    continue;
                }

                if (masterId.asLong() == 0)
                    continue;

                map.put(masterId.asLong(), new Album(masterId.asLong(), list[1].trim(), list[0].trim(), year.asInt(), Album.getGenre(genres), coverImage.asText()));
            }
        }
        for (var a : map.values())
            System.out.println(a);
    }
}
