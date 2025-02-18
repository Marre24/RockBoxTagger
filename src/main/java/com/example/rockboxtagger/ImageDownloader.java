package com.example.rockboxtagger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;

public abstract class ImageDownloader {
    private final static String COVER_ART_PATH = ".\\CoverArt\\";
    private final static String COMMIT_PATH = "Committed\\";
    private final static String ENDING = ".jpg";

    public static void downloadImage(String imageUrl, String fileName) {
        File f = new File(buildPath(fileName));
        if (f.isFile())
            return;

        URL url = null;
        try {
            url = new URL(imageUrl);
        } catch (MalformedURLException e) {
            System.err.println("Could not open " + imageUrl + ": " + e.getMessage());
            return;
        }
        try (InputStream inputStream = url.openStream(); FileOutputStream outputStream = new FileOutputStream(buildPath(fileName))) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            System.out.println("Downloaded image: " + buildPath(fileName));
        } catch (IOException e) {
            System.err.println("Failed to download image: " + e.getMessage());
        }
    }

    public static boolean removeFile(String filename) {

        File fileToDelete = new File(buildPath(filename));

        if (!fileToDelete.isFile()) {
            System.err.println(fileToDelete + " is not a file");
            return false;
        }

        return fileToDelete.delete();
    }

    public static String buildPath(String s) {
        return COVER_ART_PATH + s + ENDING;
    }

    public static String buildCommitPath(String s) {
        return COVER_ART_PATH + COMMIT_PATH + s + ENDING;
    }

    public static void commitFile(String fileName) {
        File fileToMove = new File(buildPath(fileName));
        File filePathToMoveTo = new File(buildCommitPath(fileName));

        if (filePathToMoveTo.isFile()){
            removeFile(fileName);
            System.out.println("File already existed in : " + buildCommitPath(fileName));
            return;
        }

        if (!fileToMove.renameTo(filePathToMoveTo)) {
            System.err.println("Could not move file to: " + buildCommitPath(fileName));
            return;
        }
        System.out.println("Committed file: " + buildCommitPath(fileName));
    }
}
