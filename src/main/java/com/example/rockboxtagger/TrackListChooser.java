package com.example.rockboxtagger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.LinkedList;

public class TrackListChooser {

    private static LinkedList<MasterRelease> masterReleases;
    private static MasterRelease current;
    private static int trackListCounter;
    private static final ArrayList<Album> committedAlbums = new ArrayList<>();

    public static ObservableList<String> getNextTrackList() {
        if (++trackListCounter >= current.trackLists().size())
            trackListCounter = 0;
        ObservableList<String> ob = FXCollections.observableArrayList();
        for (var s : current.trackLists().get(trackListCounter).songs())
            ob.add(s.toString());
        return ob;
    }

    public static ArrayList<Album> getCommittedAlbums() {
        return committedAlbums;
    }

    public static void init(LinkedList<MasterRelease> committedMasterReleases) {
        masterReleases = committedMasterReleases;
        current = masterReleases.getFirst();
        masterReleases.removeFirst();
    }

    public static boolean commitTrackList() {
        committedAlbums.add(new Album(
                current.masterID(),
                current.title(),
                current.artist(),
                current.year(),
                MasterRelease.genreToString(current.genre()),
                current.imageUrl(),
                current.trackLists().get(trackListCounter)));

        if (masterReleases.isEmpty()){
            return false;
        }
        trackListCounter = 0;
        current = masterReleases.getFirst();
        return true;
    }

    public static String getCount() {
        return (trackListCounter + 1) + " / " + current.trackLists().size();
    }

    public static ObservableList<String> getPreviousTrackList() {
        if (--trackListCounter < 0)
            trackListCounter = current.trackLists().size() - 1;
        ObservableList<String> ob = FXCollections.observableArrayList();
        for (var s : current.trackLists().get(trackListCounter).songs())
            ob.add(s.toString());
        return ob;
    }
}
