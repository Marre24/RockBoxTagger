package com.example.rockboxtagger;

import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.datatype.Artwork;
import org.jaudiotagger.tag.reference.PictureTypes;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public record Song(String title, String duration, int trackNumber, List<String> features) {
    private String getFeatureTitle(){
        if (features().isEmpty())
            return title;

        StringBuilder featureString = new StringBuilder("(feat. ");

        for (var f : features())
            featureString.append(f);

        featureString.append(")");

        return title + featureString;
    }
    @Override
    public String toString() {
        return trackNumber + ". " + getFeatureTitle() + " " + duration;
    }

    public void writeToTag(Tag tag, Album album) throws FieldDataInvalidException, IOException {
        tag.setField(FieldKey.ALBUM_ARTIST, album.artist());
        tag.setField(FieldKey.ARTIST, album.artist());
        tag.setField(FieldKey.ALBUM, album.title());
        tag.setField(FieldKey.TITLE, getFeatureTitle());
        tag.setField(FieldKey.TRACK, Integer.toString(trackNumber));
        tag.setField(FieldKey.YEAR, Integer.toString(album.year()));
        tag.setField(FieldKey.GENRE, album.genre());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Song song = (Song) o;
        return trackNumber == song.trackNumber && Objects.equals(title, song.title) && Objects.equals(features, song.features);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, trackNumber, features);
    }

    public String getFileName() {
        String nr = Integer.toString(trackNumber).trim();
        if (nr.length() == 1)
            nr = "0" + nr;
        return nr + " " + title.replaceAll("[\\\\/:*?\"<>|]", "_") + ".mp3";
    }
}
