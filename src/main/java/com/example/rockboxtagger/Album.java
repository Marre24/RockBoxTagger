package com.example.rockboxtagger;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

enum Genre {
    HipHop,
    Soul,
    Rock,
    Pop,
    Jazz,
    Classical,
    Electronic,
    RnB,
    Metal,
    Indie,
    Reggae,
    Country,
    Blues,
    Folk,
    Alternative,
    Punk,
    Latin,
    EDM,
    Rap,
    Funk,
    NONE,
};



public record Album(
        long masterID,
        String title,
        String artist,
        int year,
        Genre genre,
        String imageUrl
)
{
    @Override
    public String toString() {
        return "Album{" +
                "title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                '}';
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

    public static Genre getGenre(JsonNode node){
        for (var n : node)
            return Genre.NONE;
        return Genre.NONE;
    }

    private static final Map<Genre, String> genreMap;

    static {
        Map<Genre, String> tempMap = new HashMap<>();
        tempMap.put(Genre.HipHop, "HipHop");
        tempMap.put(Genre.Soul, "Soul");
        tempMap.put(Genre.Rock, "Rock");
        tempMap.put(Genre.Pop, "Pop");
        tempMap.put(Genre.Jazz, "Jazz");
        tempMap.put(Genre.Classical, "Classical");
        tempMap.put(Genre.Electronic, "Electronic");
        tempMap.put(Genre.RnB, "RnB");
        tempMap.put(Genre.Metal, "Metal");
        tempMap.put(Genre.Indie, "Indie");
        tempMap.put(Genre.Reggae, "Reggae");
        tempMap.put(Genre.Country, "Country");
        tempMap.put(Genre.Blues, "Blues");
        tempMap.put(Genre.Folk, "Folk");
        tempMap.put(Genre.Alternative, "Alternative");
        tempMap.put(Genre.Punk, "Punk");
        tempMap.put(Genre.Latin, "Latin");
        tempMap.put(Genre.EDM, "EDM");
        tempMap.put(Genre.Rap, "Rap");
        tempMap.put(Genre.Funk, "Funk");
        tempMap.put(Genre.NONE, "NONE");

        genreMap = Collections.unmodifiableMap(tempMap);
    }
}
