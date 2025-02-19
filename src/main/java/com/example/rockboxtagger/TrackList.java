package com.example.rockboxtagger;

import java.util.ArrayList;
import java.util.Objects;

public record TrackList(ArrayList<Song> songs) {

    public Song getSongWithTrackNumber(int nr){
        for (var s : songs)
            if (s.trackNumber() == nr)
                return s;
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrackList trackList = (TrackList) o;
        if (trackList.songs.size() != songs.size())
            return false;
        for (int i = 0; i < trackList.songs.size(); i++)
            if (!songs.get(i).equals(trackList.songs.get(i)))
                return false;
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(songs);
    }
}
