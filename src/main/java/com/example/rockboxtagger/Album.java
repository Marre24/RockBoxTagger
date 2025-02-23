package com.example.rockboxtagger;

import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.Tag;

import java.io.IOException;

public record Album(
        long masterID,
        String title,
        String artist,
        int year,
        String genre,
        String imagePath,
        TrackList trackList
) {

    public void writeTagToTrackNr(Tag tag, int trackNr) throws FieldDataInvalidException, IOException {
        var track = trackList.getSongWithTrackNumber(trackNr);
        if (track == null) {
            System.err.println("Could not find track with number: " + trackNr + " in : " + this);
            return;
        }
        track.writeToTag(tag, this);
    }

    public String getFileNameFor(Integer trackNr) {
        if (trackList == null || trackList.getSongWithTrackNumber(trackNr) == null){
            System.err.println("Could not find track with trackNr: "+ trackNr + " in " + this);
            return null;
        }

        return trackList.getSongWithTrackNumber(trackNr).getFileName();
    }
}
