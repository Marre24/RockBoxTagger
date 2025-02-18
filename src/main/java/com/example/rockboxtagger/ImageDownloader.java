package com.example.rockboxtagger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;


public abstract class ImageDownloader {


    public static void downloadImage(String imageUrl, String fileName) {
        File f = new File(PathBuilder.buildPath(fileName));
        if (f.isFile())
            return;

        URL url = null;
        try {
            url = new URL(imageUrl);
        } catch (MalformedURLException e) {
            System.err.println("Could not open " + imageUrl + ": " + e.getMessage());
            return;
        }
        try (InputStream inputStream = url.openStream(); FileOutputStream outputStream = new FileOutputStream(PathBuilder.buildPath(fileName))) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            System.out.println("Downloaded image: " + PathBuilder.buildPath(fileName));
        } catch (IOException e) {
            System.err.println("Failed to download image: " + e.getMessage());
        }
    }

    public static boolean removeFile(String filename) {

        File fileToDelete = new File(PathBuilder.buildPath(filename));

        if (!fileToDelete.isFile()) {
            System.err.println(fileToDelete + " is not a file");
            return false;
        }

        return fileToDelete.delete();
    }



    public static void commitFile(String fileName) {
        File fileToMove = new File(PathBuilder.buildPath(fileName));
        File filePathToMoveTo = new File(PathBuilder.buildCommitPath(fileName));

        if (filePathToMoveTo.isFile()){
            removeFile(fileName);
            System.out.println("File already existed in : " + PathBuilder.buildCommitPath(fileName));
            return;
        }

        if (!fileToMove.renameTo(filePathToMoveTo)) {
            System.err.println("Could not move file to: " + PathBuilder.buildCommitPath(fileName));
            return;
        }
        System.out.println("Committed file: " + PathBuilder.buildCommitPath(fileName));
    }
}
