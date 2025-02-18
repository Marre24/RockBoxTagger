package com.example.rockboxtagger;

import java.util.List;

public record Song(String title, String artist, String duration, int trackNumber, List<String> features) {
    @Override
    public String toString() {
        if (!features().isEmpty()){
            StringBuilder featureString = new StringBuilder("(feat. ");

            for (var f : features())
                featureString.append(f);

            featureString.append(")");

            return trackNumber + ". " + title + featureString + " " + duration;
        }

        return trackNumber + ". " + title + " " + duration;
    }
}
