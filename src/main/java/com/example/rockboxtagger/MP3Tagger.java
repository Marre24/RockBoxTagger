package com.example.rockboxtagger;

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.Tag;

import java.io.File;

public class MP3Tagger {

    public static void writeAlbum(Album album){

        try {
            File mp3File = new File("path/to/your/song.mp3");
            MP3File audioFile = (MP3File) AudioFileIO.read(mp3File);

            Tag tag = audioFile.getTagOrCreateAndSetDefault();
            for (var s : album.trackList().songs())
                s.writeToTag(tag, album);

            audioFile.commit();

        } catch (Exception e) {
            System.err.println("Could not write tag data for" + album + ": " + e.getMessage());
        }

    }

    // TODO: 2025-02-18 writePlaylist
}
