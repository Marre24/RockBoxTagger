package com.example.rockboxtagger;

import javax.imageio.ImageIO;
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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class MP3Tagger extends Thread {
    private static final int WIDTH_HEIGHT = 500;
    private final String path;
    ArrayList<Album> committedAlbums;
    private int totalTracks = 0;
    private int tracksCompleted = 0;

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
            } catch (FieldDataInvalidException | CannotWriteException | IOException e) {
                System.err.println("Could not write to: " + audioFile);
                return;
            }

            File oldFileName = audioFile.getFile();
            File newFileName = new File(oldFileName.getParent() + "\\" + album.getFileNameFor(key));

            if (!oldFileName.renameTo(newFileName)){
                System.err.println("Could not rename: " + oldFileName + " to: " + newFileName);
            }
            tracksCompleted++;
            System.out.println(audioFile + " was tagged: " + audioFile.getID3v2Tag());

        }

        exportCoverImage(album.imagePath(), path + "\\Albums\\" + album.title() + "\\Cover.jpg");



    }

    private void exportCoverImage(String oldPath, String newPath) {
        File coverImage = new File(oldPath);
        File coverDestination = new File(newPath);

        try {
            BufferedImage image = ImageIO.read(coverImage);
            Image tmp = image.getScaledInstance(WIDTH_HEIGHT, WIDTH_HEIGHT, Image.SCALE_SMOOTH);
            BufferedImage resizedImage = new BufferedImage(WIDTH_HEIGHT, WIDTH_HEIGHT, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = resizedImage.createGraphics();
            g2d.drawImage(tmp, 0, 0, null);
            g2d.dispose();

            ImageIO.write(resizedImage, "jpg", coverImage);
        } catch (IOException ex) {
            System.err.println("Could not convert to image via read: " + coverImage.getPath());
            return;
        }

        deleteFile(coverDestination);

        if (!coverImage.renameTo(coverDestination))
            System.err.println("Could not move: " + coverImage.getPath() + " to: " + coverDestination.getPath());
    }

    private static void deleteFile(File file) {
        if (file.isFile()) {
            if (file.delete()) {
                System.out.println("Deleted: " + file.getPath());
            } else {
                System.err.println("Could not delete: " + file.getPath());
            }
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
                if (!f.getPath().contains(".mp3")){
                    System.out.println("File: " + f.getPath() + " is not a mp3 file");
                    continue;
                }
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
        return tracksCompleted / (double) totalTracks;
    }


    // TODO: 2025-02-18 writePlaylist
}
