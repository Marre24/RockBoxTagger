package com.example.rockboxtagger;

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class MP3Tagger extends Thread {
    private final String path;
    ArrayList<Album> committedAlbums;
    private int totalTracks = 0;
    private int tracksCompleated = 0;

    public MP3Tagger(ArrayList<Album> committedAlbums, String p) {
        this.committedAlbums = committedAlbums;
        for (var album : committedAlbums)
            totalTracks += album.trackList().songs().size();
        path = p;
    }

    @Override
    public void run() {
        for (var album : committedAlbums)
            writeAlbum(album);
    }

    public void writeAlbum(Album album) {
        var list = getAllFilesFor(album.title());
        if (list == null)
            return;

        if (list.size() != album.trackList().songs().size()) {
            System.err.println("Amount of files: " + list.size() +
                    " does not match up with album track size: " + album.trackList().songs().size() +
                    " for: " + album);
            return;
        }
        for (var key : list.keySet()) {

            var audioFile = list.get(key);
            Tag tag = audioFile.createDefaultTag();
            audioFile.setTag(tag);

            try {
                album.writeTagToTrackNr(tag, key);
                audioFile.commit();
            } catch (FieldDataInvalidException | CannotWriteException e) {
                System.err.println("Could not write to: " + audioFile);
                return;
            }

            File oldFileName = audioFile.getFile();
            File newFileName = new File(oldFileName.getParent() + "\\" + album.getFileNameFor(key));

            if (oldFileName.renameTo(newFileName))

            //rename file

            tracksCompleated++;
            System.out.println(audioFile + " was tagged: " + audioFile.getID3v2Tag());

        }
    }

    private Map<Integer, MP3File> getAllFilesFor(String title) {
        String dirPath = path + "\\Albums\\" + title;
        File dir = new File(dirPath);
        if (!dir.isDirectory()) {
            System.err.println(dirPath + " was not a directory");
            return null;
        }
        Map<Integer, MP3File> mp3Files = new TreeMap<>();

        for (var f : Objects.requireNonNull(dir.listFiles()))
            try {
                MP3File audioFile = (MP3File) AudioFileIO.read(f);
                var tag = audioFile.getTag();
                if (tag != null && tag.getFirst(FieldKey.TRACK) != null) {
                    int trackNr = Integer.parseInt(tag.getFirst(FieldKey.TRACK).trim());

                    if (mp3Files.containsKey(trackNr)) {
                        System.err.println("Duplicate track number detected: " + trackNr);
                    } else {
                        mp3Files.put(trackNr, audioFile);
                        System.out.println("Added track #" + trackNr + ": " + f.getName());
                    }
                } else {
                    System.err.println("No track number found for: " + f.getName());
                }

            } catch (CannotReadException e) {
                System.err.println("Could not read from file: " + f.getPath());
                return null;
            } catch (IOException e) {
                System.err.println("IOException thrown: " + e.getMessage());
                return null;
            } catch (TagException e) {
                System.err.println("Could not get tag: " + e.getMessage());
                return null;
            } catch (ReadOnlyFileException e) {
                System.err.println("File: " + f.getPath() + " was a readonly file" + e.getMessage());
                return null;
            } catch (InvalidAudioFrameException e) {
                System.err.println("InvalidAudioFrameException: " + e.getMessage());
                return null;
            }

        return mp3Files;
    }

    public double getProgress() {
        return tracksCompleated / (double) totalTracks;
    }


    // TODO: 2025-02-18 writePlaylist
}
