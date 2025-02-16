package com.example.rockboxtagger;

import okhttp3.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.URL;

public class DiscogsApi {

    private static final String BASE_URL = "https://api.discogs.com";
    private static final String USER_AGENT = "YourAppName/1.0";
    private static final String TOKEN = "pGtSjsiRGpheBMCzoVLlwJgZamcUKGmqBaeNaxHe"; // Replace with your Discogs Token

    public static void main(String[] args) throws IOException {
        OkHttpClient client = new OkHttpClient();

        String searchTerm = "Daft Punk";
        String url = BASE_URL + "/database/search?q=" + searchTerm + "&type=artist";

        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", USER_AGENT)
                .header("Authorization", "Discogs token=" + TOKEN) // Use your token here
                .build();

        // Execute the request
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // Parse the JSON response
                String jsonResponse = response.body().string();
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(jsonResponse);

                // Get the list of results
                JsonNode artists = rootNode.get("results");
                for (JsonNode artist : artists) {
                    String name = artist.get("title").asText();
                    System.out.println("Artist Name: " + name);

                    // Get the first image (cover art) from the result
                    JsonNode coverImages = artist.get("cover_image");
                    if (coverImages != null) {
                        String coverImageUrl = artist.get("cover_image").asText();
                        System.out.println("Cover Image URL: " + coverImageUrl);

                        // Download the cover image
                        downloadImage(coverImageUrl, name + "_cover.jpg");
                    }
                }
            } else {
                System.out.println("Request failed. Response code: " + response.code());
            }
        }
    }

    // Method to download the image
    private static void downloadImage(String imageUrl, String fileName) throws IOException {
        // Open a connection to the image URL
        URL url = new URL(imageUrl);
        try (InputStream inputStream = url.openStream();
             FileOutputStream outputStream = new FileOutputStream(fileName)) {

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
