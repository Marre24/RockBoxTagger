package com.example.rockboxtagger;

public class PathBuilder {

    private final static String COVER_ART_PATH = ".\\CoverArt\\";
    private final static String COMMIT_PATH = "Committed\\";
    private final static String ENDING = ".jpg";

    public static String buildPath(String s) {
        return COVER_ART_PATH + s + ENDING;
    }

    public static String buildCommitPath(String s) {
        return COVER_ART_PATH + COMMIT_PATH + s + ENDING;
    }
}
