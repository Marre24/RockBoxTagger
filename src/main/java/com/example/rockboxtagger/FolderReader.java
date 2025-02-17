package com.example.rockboxtagger;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class FolderReader {

    public static LinkedList<String> getAlbumNames(String path){

        File file = new File(path + "\\Albums");

        String[] directories = file.list((current, name) -> new File(current, name).isDirectory());
        if (directories == null){
            System.err.println("Could not find directory: " + file.getAbsolutePath());
            return null;
        }
        if (directories.length == 0){
            System.err.println("Could not find any sub-folders to directory: " + file.getAbsolutePath());
            return null;
        }
        return new LinkedList<>(Arrays.asList(directories));
    }

}
