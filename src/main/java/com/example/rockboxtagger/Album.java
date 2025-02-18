package com.example.rockboxtagger;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.*;

public record Album(
        long masterID,
        String title,
        String artist,
        int year,
        Genre genre,
        String imageUrl,
        ArrayList<Song> songs
) {

    public void addSong(Song s) {
        songs.add(s);
    }

    @Override
    public String toString() {
        return artist + " - " + title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Album album = (Album) o;
        return masterID == album.masterID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(masterID);
    }

    public static Genre getGenre(JsonNode node) {
        for (var n : node)
            if (genreMap.containsKey(n.asText()))
                return genreMap.get(n.asText());
        return Genre.NONE;
    }

    private static final Map<String, Genre> genreMap;

    static {
        genreMap = Map.ofEntries(
                Map.entry("Alternative", Genre.Alternative),
                Map.entry("Electronic", Genre.Electronic),
                Map.entry("Classical", Genre.Classical),
                Map.entry("HipHop", Genre.HipHop),
                Map.entry("Soul", Genre.Soul),
                Map.entry("Rock", Genre.Rock),
                Map.entry("Pop", Genre.Pop),
                Map.entry("Jazz", Genre.Jazz),
                Map.entry("RnB", Genre.RnB),
                Map.entry("Metal", Genre.Metal),
                Map.entry("Indie", Genre.Indie),
                Map.entry("Reggae", Genre.Reggae),
                Map.entry("Country", Genre.Country),
                Map.entry("Blues", Genre.Blues),
                Map.entry("Folk", Genre.Folk),
                Map.entry("Punk", Genre.Punk),
                Map.entry("Latin", Genre.Latin),
                Map.entry("EDM", Genre.EDM),
                Map.entry("Rap", Genre.Rap),
                Map.entry("Funk", Genre.Funk),
                Map.entry("NONE", Genre.NONE));
    }
}
