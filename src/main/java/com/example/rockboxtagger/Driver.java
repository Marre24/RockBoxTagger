package com.example.rockboxtagger;

import javafx.scene.image.Image;

import java.util.LinkedList;

public class Driver {

    private static LinkedList<String> albumNames;

    private static AlbumQuery activeQuery;

    private static final LinkedList<Album> committedAlbums = new LinkedList<>();

    private static boolean finished = false;

    public static boolean isFinished() {
        return finished;
    }

    public static LinkedList<Album> getCommittedAlbums() {
        return committedAlbums;
    }

    public static boolean init(String folderPath) {
        albumNames = FolderReader.getAlbumNames(folderPath);

        return albumNames != null && !albumNames.isEmpty() && goNext();
    }

    public static void commit(int i) {
        committedAlbums.add(activeQuery.commit(i));
        finished = !goNext();
    }

    public static void reRoll(int i) {
        activeQuery.reRoll(0);
    }

    public static boolean goNext(){
        if (albumNames.isEmpty()){
            System.out.println("Finished!");
            return false;
        }

        activeQuery = new AlbumQuery();
        activeQuery.startQuery(albumNames.getFirst());
        albumNames.removeFirst();
        return true;
    }

    public static Image getImageFor(int i){
        //String path = ImageDownloader.COVER_ART_PATH + activeQuery.getAlbums()[i].masterID() + ImageDownloader.ENDING;
        String path = activeQuery.getAlbums()[i].imageUrl();
        try {
            return new Image(path);
        } catch (Exception e){
            System.err.println("Could not create " + path + ": " + e.getMessage());
            return null;
        }
    }

    public static String getTitleFor(int i){
        var album = activeQuery.getAlbums()[i];
        return album.artist() + "-" + album.title();
    }
}
