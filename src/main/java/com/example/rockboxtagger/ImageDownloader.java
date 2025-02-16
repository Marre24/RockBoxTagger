package com.example.rockboxtagger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;

public abstract class ImageDownloader {
    final static String COVER_ART_PATH = "./CoverArt";
    public static void downloadImage(String imageUrl, String fileName) throws IOException {
        URL url = new URL(imageUrl);
        try (InputStream inputStream = url.openStream();
             FileOutputStream outputStream = new FileOutputStream(COVER_ART_PATH + fileName)) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            System.out.println("Downloaded image: " + fileName);
        } catch (IOException e) {
            System.err.println("Failed to download image: " + e.getMessage());
        }
    }
}
