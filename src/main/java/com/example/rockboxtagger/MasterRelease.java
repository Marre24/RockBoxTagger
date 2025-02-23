package com.example.rockboxtagger;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.*;

public record MasterRelease(
        long masterID,
        String title,
        String artist,
        int year,
        Genre genre,
        String imageUrl,
        List<TrackList> trackLists
) {

    public static String genreToString(Genre g) {
        return GENRE_STRING_MAP.get(g);
    }

    @Override
    public String toString() {
        return artist + " - " + title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MasterRelease masterRelease = (MasterRelease) o;
        return masterID == masterRelease.masterID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(masterID);
    }

    public static Genre getGenre(JsonNode node) {
        for (var n : node){
            var text = n.asText().replace("-", "").replace(" ", "").replace("/", "");
            if (STRING_GENRE_MAP.containsKey(text))
                return STRING_GENRE_MAP.get(text);
        }
        return Genre.NONE;
    }

    private static final Map<String, Genre> STRING_GENRE_MAP;
    private static final Map<Genre, String> GENRE_STRING_MAP;

    static {

        GENRE_STRING_MAP = Map.ofEntries(
                Map.entry(Genre.HipHop, "HipHop"),
                Map.entry(Genre.Soul, "Soul"),
                Map.entry(Genre.Rock, "Rock"),
                Map.entry(Genre.Pop, "Pop"),
                Map.entry(Genre.Jazz, "Jazz"),
                Map.entry(Genre.Classical, "Classical"),
                Map.entry(Genre.Electronic, "Electronic"),
                Map.entry(Genre.RnB, "RnB"),
                Map.entry(Genre.Metal, "Metal"),
                Map.entry(Genre.Indie, "Indie"),
                Map.entry(Genre.Reggae, "Reggae"),
                Map.entry(Genre.Country, "Country"),
                Map.entry(Genre.Blues, "Blues"),
                Map.entry(Genre.Folk, "Folk"),
                Map.entry(Genre.Alternative, "Alternative"),
                Map.entry(Genre.Punk, "Punk"),
                Map.entry(Genre.Latin, "Latin"),
                Map.entry(Genre.EDM, "EDM"),
                Map.entry(Genre.Rap, "Rap"),
                Map.entry(Genre.Funk, "Funk"),
                Map.entry(Genre.NONE, "NONE"));

        STRING_GENRE_MAP = Map.ofEntries(
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

    public void addTrackList(TrackList tracklist) {
        if (trackLists.contains(tracklist))
            return;
        trackLists.add(tracklist);
    }
}
