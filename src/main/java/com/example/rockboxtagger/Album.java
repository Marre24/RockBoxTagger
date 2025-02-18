package com.example.rockboxtagger;

public record Album(
        long masterID,
        String title,
        String artist,
        int year,
        String genre,
        String imageUrl,
        TrackList trackList
) {

}
