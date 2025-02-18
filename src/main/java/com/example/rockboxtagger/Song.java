package com.example.rockboxtagger;

import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import java.util.List;
import java.util.Objects;

public record Song(String title, String duration, int trackNumber, List<String> features) {
    @Override
    public String toString() {
        if (!features().isEmpty()){
            StringBuilder featureString = new StringBuilder("(feat. ");

            for (var f : features())
                featureString.append(f);

            featureString.append(")");

            return trackNumber + ". " + title + featureString + " " + duration;
        }

        return trackNumber + ". " + title + " " + duration;
    }

    public void writeToTag(Tag tag, Album album) throws FieldDataInvalidException {
        tag.setField(FieldKey.ALBUM_ARTIST, album.artist());
        tag.setField(FieldKey.ALBUM, album.title());
        tag.setField(FieldKey.TITLE, title);
        tag.setField(FieldKey.TRACK, Integer.toString(trackNumber));
        tag.setField(FieldKey.YEAR, Integer.toString(album.year()));
        tag.setField(FieldKey.GENRE, album.genre());
        tag.setField(FieldKey.COVER_ART, album.imageUrl());
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
}
